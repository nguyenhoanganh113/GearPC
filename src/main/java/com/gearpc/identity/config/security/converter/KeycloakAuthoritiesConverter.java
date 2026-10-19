package com.gearpc.identity.config.security.converter;

import com.gearpc.identity.domain.valueobject.enums.RoleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class KeycloakAuthoritiesConverter implements AuthoritiesConverter{

    private static final EnumSet<RoleType> APPLICATION_ROLES =
            EnumSet.allOf(RoleType.class);

    @Value("${keycloak.resource-client-id:gear-pc-app}")
    String resourceClientId;

    @Override
    public Collection<GrantedAuthority> convert(Map<String, Object> claims) {
        if (claims.get("realm_access") instanceof Map<?, ?> realmAccess
                && realmAccess.get("roles") instanceof Collection<?> roles) {
            return toAuthorities(roles);
        }

        if (!(claims.get("resource_access")
                instanceof Map<?, ?> resourceAccess)) {
            return List.of();
        }

        if (!(resourceAccess.get(resourceClientId)
                instanceof Map<?, ?> clientAccess)) {
            return List.of();
        }

        if (!(clientAccess.get("roles")
                instanceof Collection<?> roles)) {
            return List.of();
        }

        return toAuthorities(roles);
    }

    private Collection<GrantedAuthority> toAuthorities(Collection<?> roles) {
        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(role -> role.toUpperCase(Locale.ROOT))
                .filter(role -> APPLICATION_ROLES.stream()
                        .map(Enum::name)
                        .anyMatch(role::equals))
                .map(role -> "ROLE_" + role)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
