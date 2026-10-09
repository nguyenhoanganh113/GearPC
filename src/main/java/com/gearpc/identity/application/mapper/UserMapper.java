package com.gearpc.identity.application.mapper;

import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.domain.entity.User;
import com.gearpc.identity.domain.valueobject.enums.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Locale;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    User toUser(RegisterUserRequest registerUserRequest);

    RegisterUserResponse toRegisterUserResponse(User user);

    default Gender toGender(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Gender.valueOf(
                value.strip().toUpperCase(Locale.ROOT)
        );
    }

}
