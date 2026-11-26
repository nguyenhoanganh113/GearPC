package com.gearpc.user.mapper;

import com.gearpc.user.dto.UserDetailResponse;
import com.gearpc.user.dto.request.RegisterUserRequest;
import com.gearpc.user.dto.response.AdminUserResponse;
import com.gearpc.user.dto.response.RegisterUserResponse;
import com.gearpc.user.entity.Role;
import com.gearpc.user.entity.User;
import com.gearpc.user.entity.UserHasRole;
import com.gearpc.user.enums.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    User toUser(RegisterUserRequest registerUserRequest);

    RegisterUserResponse toRegisterUserResponse(User user);

    UserDetailResponse toUserDetailResponse(User user);

    @Mapping(
            target = "roles",
            source = "userHasRoles"
    )
    AdminUserResponse toAdminUserResponse(User user);

    default Set<String> toRoleNames(Set<UserHasRole> userHasRoles) {
        if (userHasRoles == null) {
            return Set.of();
        }

        return userHasRoles.stream()
                .map(UserHasRole::getRole)
                .filter(Objects::nonNull)
                .map(Role::getName)
                .filter(Objects::nonNull)
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    default Gender toGender(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Gender.valueOf(
                value.strip().toUpperCase(Locale.ROOT)
        );
    }

}
