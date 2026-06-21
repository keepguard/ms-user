package com.keepguard.ms_user.application.validator;

import com.keepguard.ms_user.domain.enums.LocaleCountryEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for {@link ValidLocale} annotation.
 * Checks if a locale string is valid according to LocaleCountryEnum.
 * 
 * <p>This validator is part of the application layer and provides
 * validation at the DTO level before data reaches the domain.</p>
 * 
 * @since 1.0
 */
public class ValidLocaleValidator implements ConstraintValidator<ValidLocale, String> {

    @Override
    public void initialize(ValidLocale constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or empty values are considered valid (use @NotNull/@NotBlank for required fields)
        if (value == null || value.isBlank()) {
            return true;
        }

        // Check if the locale is valid
        return LocaleCountryEnum.isValid(value.trim());
    }
}

