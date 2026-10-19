package com.gearpc.identity.config.security.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

@Component
public class KeycloakAuthoritiesConverter implements AuthoritiesConverter{

    @Value("${keycloak.resource-client-id:gear-pc-app}")
    String resourceClientId;

    @Override
    public Collection<GrantedAuthority> convert(Map<String, Object> claims) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        /*
        if (claims.get("realm_access") instanceof Map<?, ?> realmAccess
                && realmAccess.get("roles") instanceof Collection<?> roles) {
            authorities.addAll(toRealmAuthorities(roles));
        }
        **/

        if (claims.get("resource_access") instanceof Map<?, ?> resourceAccess
                && resourceAccess.get(resourceClientId) instanceof Map<?, ?> clientAccess
                && clientAccess.get("roles") instanceof Collection<?> roles) {
            authorities.addAll(toPermissionAuthorities(roles));
        }

        return authorities.stream().distinct().toList();
    }

    /**
    private Collection<GrantedAuthority> toRealmAuthorities(Collection<?> roles) {
        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(role -> role.toUpperCase(Locale.ROOT))
                .filter(role -> Arrays.stream(RoleType.values())
                        .map(Enum::name)
                        .anyMatch(role::equals))
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
     */

    private Collection<GrantedAuthority> toPermissionAuthorities(Collection<?> roles) {
        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(role -> role.toUpperCase(Locale.ROOT))
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("PERM_" + role))
                .toList();
    }
}
