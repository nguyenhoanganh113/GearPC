package com.gearpc.identity.application.service;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailService extends UserDetailsService {

    @NonNull UserDetails loadUserByUsername(@NonNull String email);

}
