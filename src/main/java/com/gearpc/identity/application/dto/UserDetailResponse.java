package com.gearpc.identity.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gearpc.identity.domain.valueobject.enums.Gender;
import com.gearpc.identity.domain.valueobject.enums.UserStatus;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record UserDetailResponse(

        String email,
        String firstName,
        String lastName,
        Gender gender,
        String phone,
        String avatarKey,
        UserStatus userStatus,
        boolean emailVerified

) {
}
