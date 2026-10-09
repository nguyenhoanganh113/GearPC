package com.gearpc.identity.application.dto.request;

import com.gearpc.common.annotation.EnumPattern;
import com.gearpc.identity.domain.valueobject.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 72, message = "Mật khẩu phải có từ 8 đến 72 ký tự")
        String password,

        @NotBlank(message = "Tên không được để trống")
        @Size(max = 255)
        String firstName,

        @NotBlank(message = "Họ không được để trống")
        @Size(max = 255)
        String lastName,

        @Size(max = 255)
        String phone,

        @EnumPattern(
                enumClass = Gender.class,
                message = "Giới tính không hợp lệ"
        )
        String gender

) {
}
