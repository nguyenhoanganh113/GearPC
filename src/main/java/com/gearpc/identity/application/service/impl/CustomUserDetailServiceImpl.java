package com.gearpc.identity.application.service.impl;

import com.gearpc.identity.application.service.CustomUserDetailService;
import com.gearpc.identity.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailServiceImpl implements CustomUserDetailService {

    private final UserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull  String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Invalid credentials")
                );
    }

}
