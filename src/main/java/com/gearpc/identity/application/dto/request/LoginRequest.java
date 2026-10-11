package com.gearpc.identity.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(

        @NotBlank(message = "Email là bắt buộc")
        @Email(message = "Địa chỉ email không hợp lệ")
        String email,

        @NotBlank(message = "Mật khẩu là bắt buộc")
        @Length(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
        String password

) {
}
