package com.keepguard.ms_user.application.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for phone numbers.
 * Validates that a string is a valid phone number in E.164 format
 * using Google's libphonenumber library.
 * 
 * <p>This annotation can be applied to String fields in DTOs to ensure
 * that phone values are valid before reaching the domain layer.</p>
 * 
 * <p>Accepted formats:</p>
 * <ul>
 *   <li>International format: +5511999999999</li>
 *   <li>With spaces/dashes: +55 11 99999-9999</li>
 *   <li>With parentheses: +55 (11) 99999-9999</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * public record UserDTO(
 *     {@literal @}ValidPhone
 *     String phoneE164
 * ) {}
 * </pre>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = ValidPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    
    String message() default "Número de telefone inválido. Use o formato internacional (ex: +5511999999999)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Optional locale field name to use for region detection.
     * If specified, the validator will try to use the locale from this field
     * to determine the default region for parsing.
     * 
     * @return the name of the locale field
     */
    String localeField() default "";
}

