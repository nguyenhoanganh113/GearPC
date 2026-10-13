package com.gearpc.identity.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.service.AuthenticationService;
import com.gearpc.identity.application.service.JwtService;
import com.gearpc.identity.domain.entity.RevokedToken;
import com.gearpc.identity.domain.entity.User;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import com.gearpc.identity.domain.valueobject.enums.TokenType;
import com.gearpc.identity.jwt.JwtClaims;
import com.gearpc.identity.repository.RevokedTokenRepository;
import com.gearpc.identity.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        if (principal == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        User domainUser = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID userId = domainUser.getId();

        Set<String> validRoles = Arrays.stream(RoleType.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<@Nullable String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(validRoles::contains)
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(userId, roles);
        String refreshToken = jwtService.generateRefreshToken(userId);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken) throws ParseException, JOSEException {

        SignedJWT signedJWT = verifyToken(refreshToken);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        if (!TokenType.REFRESH_TOKEN.name().equals(
                signedJWT.getJWTClaimsSet().getStringClaim(JwtClaims.TOKEN_TYPE))) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if (revokedTokenRepository.existsById(jwtId)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        UUID userId = UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
        User user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Set<String> roles = user.getUserHasRoles()
                .stream()
                .map(userRole -> userRole.getRole().getName().name())
                .collect(Collectors.toSet());
        String newAccessToken = jwtService.generateAccessToken(userId, roles);
        String newRefreshToken = jwtService.generateRefreshToken(userId);

        revokedTokenRepository.save(RevokedToken.builder().jti(jwtId).build());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) throws ParseException, JOSEException {
        // 1. Validate refresh token có tồn tại không
        if (refreshToken == null) {
            throw new AppException(ErrorCode.MISSING_LOGOUT_INFO);
        }

        // 2. Lấy thông tin user từ SecurityContext (từ access token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new AppException(ErrorCode.TOKEN_INVALID);
        String userId = authentication.getName();

        // 3. Validate refresh-token rồi lấy thông tin refreshToken bằng cách convert thành SignedJWT
        // SignedJWT cho phép lấy thông tin trong token để xét logic nào đó
        SignedJWT signedJWT = jwtService.validateToken(refreshToken);

        String refreshUserId = signedJWT.getJWTClaimsSet().getSubject();
        String refreshJwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Instant expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

        // 4. Verify userId từ access token và refresh token phải giống nhau
        // Tránh trường hợp user A dùng access token của mình + refresh token của user B
        if (!userId.equals(refreshUserId)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        // 5. Kiểm tra xem token gửi trong cookie có phải refreshToken hay không ?
        String claim = signedJWT.getJWTClaimsSet().getStringClaim(JwtClaims.TOKEN_TYPE);
        if (!TokenType.REFRESH_TOKEN.name().equals(claim)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if (expirationTime == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        RevokedToken revokedToken = RevokedToken.builder()
                .jti(refreshJwtId)
                .userId(userId)
                .expiresAt(expirationTime)
                .build();

        if (!revokedTokenRepository.existsById(refreshJwtId)) {
            revokedTokenRepository.save(revokedToken);
        }

    }

    private SignedJWT verifyToken(String refreshToken) throws ParseException, JOSEException {
        return jwtService.validateToken(refreshToken);
    }

}
