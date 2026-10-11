package com.gearpc.identity.application.service.impl;

import com.gearpc.identity.application.dto.request.LoginRequest;
import com.gearpc.identity.application.dto.response.LoginResponse;
import com.gearpc.identity.application.service.AuthenticationService;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        Set<String> validRoles = Arrays.stream(RoleType.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> roles = authenticate.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(validRoles::contains)
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .roles(roles)
                .build();
    }
}
