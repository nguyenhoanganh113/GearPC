package com.gearpc.identity.application.service.impl;

import com.gearpc.identity.application.service.CustomUserDetailService;
import com.gearpc.identity.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailServiceImpl implements CustomUserDetailService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public @NonNull UserDetails loadUserByUsername(@NonNull  String username) {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new
                        UsernameNotFoundException("User not found with email: " + username)
                );

        String[] authorities = user.getUserHasRoles()
                .stream()
                .map(userHasRole -> userHasRole.getRole().getName().name())
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isEmailVerified())
                .build();
    }

}
