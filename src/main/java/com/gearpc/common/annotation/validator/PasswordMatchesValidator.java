package com.gearpc.common.annotation.validator;

import com.gearpc.common.annotation.PasswordMatches;
import com.gearpc.common.dto.PasswordMatchable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, PasswordMatchable> {
    private String message;

    @Override
    public void initialize(PasswordMatches annotation) {
        message = annotation.message();
    }

    @Override
    public boolean isValid(
            PasswordMatchable request,
            ConstraintValidatorContext context
    ) {
        if (request == null
                || request.newPassword() == null
                || request.newPassword().isBlank()
                || request.confirmNewPassword() == null
                || request.confirmNewPassword().isBlank()) {
            return true;
        }

        if (Objects.equals(
                request.newPassword(),
                request.confirmNewPassword()
        )) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("confirmNewPassword")
                .addConstraintViolation();

        return false;
    }
}
