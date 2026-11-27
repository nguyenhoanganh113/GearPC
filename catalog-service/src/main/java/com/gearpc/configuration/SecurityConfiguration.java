package com.gearpc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Value("${keycloak.resource-client-id:gear-pc-app}")
    private String resourceClientId;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object resourceAccessClaim = jwt.getClaims().get("resource_access");
            if (!(resourceAccessClaim instanceof Map<?, ?> resourceAccess)) {
                return List.of();
            }

            Object clientAccessClaim = resourceAccess.get(resourceClientId);
            if (!(clientAccessClaim instanceof Map<?, ?> clientAccess)) {
                return List.of();
            }

            Object rolesClaim = clientAccess.get("roles");
            if (!(rolesClaim instanceof Collection<?> roles)) {
                return List.of();
            }

            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(String::strip)
                    .filter(role -> !role.isBlank())
                    .map(role -> role.toUpperCase(Locale.ROOT))
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("PERM_" + role))
                    .distinct()
                    .toList();
        });

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/cache-demo/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

        return http.build();
    }
}
