package com.gearpc.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gearpc.user.enums.Gender;
import com.gearpc.user.enums.UserStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegisterUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        Gender gender,
        UserStatus userStatus,
        boolean emailVerified,
        Instant createdAt
) {
}
