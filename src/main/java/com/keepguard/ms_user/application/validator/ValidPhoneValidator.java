package com.keepguard.ms_user.application.validator;

import com.keepguard.ms_user.domain.validator.PhoneValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for {@link ValidPhone} annotation.
 * Checks if a phone string is valid according to Google's libphonenumber.
 * 
 * <p>This validator is part of the application layer and provides
 * validation at the DTO level before data reaches the domain.</p>
 * 
 * <p>The validator accepts:</p>
 * <ul>
 *   <li>null or empty values (use @NotNull/@NotBlank for required fields)</li>
 *   <li>Valid phone numbers in E.164 or international format</li>
 *   <li>Phone numbers with various formatting (spaces, dashes, parentheses)</li>
 * </ul>
 * 
 * @since 1.0
 */
public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private String localeField;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.localeField = constraintAnnotation.localeField();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or empty values are considered valid (use @NotNull/@NotBlank for required fields)
        if (value == null || value.isBlank()) {
            return true;
        }

        // For now, we validate without locale context at DTO level
        // The domain layer will handle locale-aware validation
        // This provides a first line of defense for obviously invalid formats
        return PhoneValidator.isValid(value.trim(), null);
    }
}

