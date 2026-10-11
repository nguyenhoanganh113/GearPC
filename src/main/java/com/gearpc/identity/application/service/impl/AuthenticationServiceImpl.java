package com.gearpc.identity.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.service.AuthenticationService;
import com.gearpc.identity.application.service.JwtService;
import com.gearpc.identity.domain.entity.User;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import com.gearpc.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
}
