package com.keepguard.ms_user.domain.validator;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.LocaleCountryEnum;

/**
 * Validator for locale strings based on LocaleCountryEnum.
 * Provides domain-level validation following DDD principles.
 * 
 * <p>This validator ensures that locale values conform to the supported
 * locales defined in the system.</p>
 * 
 * @since 1.0
 */
public final class LocaleValidator {

    private LocaleValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates a locale string.
     * 
     * @param locale the locale string to validate (can be null)
     * @return the validated and normalized locale string, or null if input is null
     * @throws ValidationException if the locale is invalid
     */
    public static String validate(String locale) {
        if (locale == null || locale.isBlank()) {
            return null; // locale is optional
        }

        String trimmedLocale = locale.trim();
        
        if (!LocaleCountryEnum.isValid(trimmedLocale)) {
            throw new ValidationException("Locale inválido: " + locale + ". Use um dos formatos suportados (ex: pt-BR, en-US)");
        }

        // Return the normalized code from the enum to ensure consistency
        return LocaleCountryEnum.fromCode(trimmedLocale).getCode();
    }

    /**
     * Validates a locale string and throws an exception if invalid.
     * Similar to validate() but with a custom error message.
     * 
     * @param locale the locale string to validate
     * @param fieldName the name of the field being validated (for error messages)
     * @return the validated and normalized locale string, or null if input is null
     * @throws ValidationException if the locale is invalid
     */
    public static String validateWithFieldName(String locale, String fieldName) {
        if (locale == null || locale.isBlank()) {
            return null;
        }

        String trimmedLocale = locale.trim();
        
        if (!LocaleCountryEnum.isValid(trimmedLocale)) {
            throw new ValidationException(
                fieldName + " inválido: " + locale + ". Use um dos formatos suportados (ex: pt-BR, en-US)"
            );
        }

        return LocaleCountryEnum.fromCode(trimmedLocale).getCode();
    }
}

