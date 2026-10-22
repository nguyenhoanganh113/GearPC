package com.gearpc.identity.application.service.impl;

/*

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.dto.response.TokenDetails;
import com.gearpc.identity.application.service.AuthenticationService;
import com.gearpc.identity.application.service.JwtService;
import com.gearpc.identity.application.service.RedisService;
import com.gearpc.identity.domain.entity.User;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import com.gearpc.identity.domain.valueobject.enums.TokenType;
import com.gearpc.identity.jwt.JwtClaims;
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
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    //private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisService redisService;

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
        TokenDetails refreshToken = jwtService.generateRefreshToken(userId);

        redisService.saveUserSession(refreshToken.jti(), userId, refreshToken.expiresAt(), refreshToken.ttl());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.value())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken) throws ParseException, JOSEException {

        SignedJWT signedJWT = verifyToken(refreshToken);

        String oldJti = signedJWT.getJWTClaimsSet().getJWTID();

        String tokenType = signedJWT.getJWTClaimsSet().getStringClaim(JwtClaims.TOKEN_TYPE);

        if (!TokenType.REFRESH_TOKEN.name().equals(tokenType)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if (!redisService.isRefreshTokenSessionActive(oldJti)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        UUID userId = UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
        User user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Set<String> roles = user.getUserHasRoles()
                .stream()
                .map(userRole -> userRole.getRole().getName().name())
                .collect(Collectors.toSet());

        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiration == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        Instant expirationTime = expiration.toInstant();

        String newAccessToken = jwtService.generateAccessToken(userId, roles);
        TokenDetails newRefreshToken = jwtService.generateRefreshToken(userId, expirationTime);

        // Xóa refreshToken dựa theo jti trong white list
        redisService.deleteUserSession(oldJti);

        // Lưu refresh token mới
        redisService.saveUserSession(
                newRefreshToken.jti(),
                userId,
                newRefreshToken.expiresAt(),
                newRefreshToken.ttl()
        );

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.value())
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
        UUID userId = UUID.fromString(authentication.getName());

        // 3. Validate refresh-token rồi lấy thông tin refreshToken bằng cách convert thành SignedJWT
        // SignedJWT cho phép lấy thông tin trong token để xét logic nào đó
        SignedJWT signedJWT = jwtService.validateToken(refreshToken);

        UUID refreshUserId = UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
        String refreshJwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        // 4. Verify userId từ access token và refresh token phải giống nhau
        // Tránh trường hợp user A dùng access token của mình + refresh token của user B
        if (!userId.equals(refreshUserId)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        // 5. Kiểm tra xem token gửi trong cookie có phải refreshToken hay không ?
        String tokenType = signedJWT.getJWTClaimsSet().getStringClaim(JwtClaims.TOKEN_TYPE);
        if (!TokenType.REFRESH_TOKEN.name().equals(tokenType)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if (expiration == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        redisService.deleteUserSession(refreshJwtId);

    }

    @Override
    @Transactional
    public void logoutAll() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        UUID userId = UUID.fromString(authentication.getName());

        redisService.deleteAllUserSessions(userId);
    }

    private SignedJWT verifyToken(String refreshToken) throws ParseException, JOSEException {
        return jwtService.validateToken(refreshToken);
    }

}
*/
