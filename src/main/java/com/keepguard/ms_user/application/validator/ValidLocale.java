package com.keepguard.ms_user.application.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for locale strings.
 * Validates that a string matches one of the supported locale codes
 * defined in LocaleCountryEnum.
 * 
 * <p>This annotation can be applied to String fields in DTOs to ensure
 * that locale values are valid before reaching the domain layer.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * public record UserDTO(
 *     {@literal @}ValidLocale
 *     String preferredLocale
 * ) {}
 * </pre>
 * 
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = ValidLocaleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocale {
    
    String message() default "Locale inválido. Use um dos formatos suportados (ex: pt-BR, en-US)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}

