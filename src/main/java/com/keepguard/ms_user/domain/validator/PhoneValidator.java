package com.keepguard.ms_user.domain.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.LocaleCountryEnum;

/**
 * Validator for phone numbers using Google's libphonenumber library.
 * Provides domain-level validation following DDD principles.
 * 
 * <p>This validator ensures that phone numbers are valid E.164 format and
 * can parse numbers based on the user's locale (country code).</p>
 * 
 * <p>E.164 format: +[country code][number] (e.g., +5511999999999)</p>
 * 
 * @since 1.0
 */
public final class PhoneValidator {

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private PhoneValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates a phone number string without a default region.
     * Expects the phone to be in international format (with country code).
     * 
     * @param phone the phone number to validate (can be null)
     * @return the validated and normalized phone in E.164 format, or null if input is null
     * @throws ValidationException if the phone is invalid
     */
    public static String validate(String phone) {
        return validate(phone, null);
    }

    /**
     * Validates a phone number string with an optional locale for region detection.
     * Can parse both international (+55...) and national (11 9999...) formats.
     * 
     * @param phone the phone number to validate (can be null)
     * @param locale the user's locale to determine default region (can be null)
     * @return the validated and normalized phone in E.164 format, or null if input is null
     * @throws ValidationException if the phone is invalid
     */
    public static String validate(String phone, String locale) {
        if (phone == null || phone.isBlank()) {
            return null; // phone is optional
        }

        String trimmedPhone = phone.trim();
        
        // If phone starts with +, use null as region to auto-detect
        // Otherwise, use the region from locale
        String regionCode = trimmedPhone.startsWith("+") 
            ? null 
            : extractRegionFromLocale(locale);

        try {
            // Parse phone number
            PhoneNumber phoneNumber = phoneUtil.parse(trimmedPhone, regionCode);

            // Validate if it's a possible and valid number
            if (!phoneUtil.isValidNumber(phoneNumber)) {
                throw new ValidationException(
                    "Número de telefone inválido: " + phone + ". Use o formato internacional (ex: +5511999999999)"
                );
            }

            // Return normalized E.164 format
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        } catch (NumberParseException e) {
            throw new ValidationException(
                "Formato de telefone inválido: " + phone + ". Use o formato internacional (ex: +5511999999999). " +
                "Erro: " + e.getMessage()
            );
        }
    }

    /**
     * Validates a phone number string with custom error message.
     * 
     * @param phone the phone number to validate
     * @param locale the user's locale to determine default region
     * @param fieldName the name of the field being validated (for error messages)
     * @return the validated and normalized phone in E.164 format, or null if input is null
     * @throws ValidationException if the phone is invalid
     */
    public static String validateWithFieldName(String phone, String locale, String fieldName) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String trimmedPhone = phone.trim();
        
        // If phone starts with +, use null as region to auto-detect
        // Otherwise, use the region from locale
        String regionCode = trimmedPhone.startsWith("+") 
            ? null 
            : extractRegionFromLocale(locale);

        try {
            PhoneNumber phoneNumber = phoneUtil.parse(trimmedPhone, regionCode);

            if (!phoneUtil.isValidNumber(phoneNumber)) {
                throw new ValidationException(
                    fieldName + " inválido: " + phone + ". Use o formato internacional (ex: +5511999999999)"
                );
            }

            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        } catch (NumberParseException e) {
            throw new ValidationException(
                fieldName + " com formato inválido: " + phone + ". Use o formato internacional (ex: +5511999999999)"
            );
        }
    }

    /**
     * Checks if a phone number is valid.
     * 
     * @param phone the phone number to check
     * @param locale the user's locale to determine default region
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String phone, String locale) {
        if (phone == null || phone.isBlank()) {
            return true; // null/blank is considered valid (use @NotNull if required)
        }

        String trimmedPhone = phone.trim();
        
        // If phone starts with +, use null as region to auto-detect
        // Otherwise, use the region from locale
        String regionCode = trimmedPhone.startsWith("+") 
            ? null 
            : extractRegionFromLocale(locale);

        try {
            PhoneNumber phoneNumber = phoneUtil.parse(trimmedPhone, regionCode);
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }

    /**
     * Normalizes a phone number to E.164 format without throwing exceptions.
     * 
     * @param phone the phone number to normalize
     * @param locale the user's locale to determine default region
     * @return the normalized phone in E.164 format, or null if invalid or null input
     */
    public static String normalize(String phone, String locale) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String trimmedPhone = phone.trim();
        
        // If phone starts with +, use null as region to auto-detect
        // Otherwise, use the region from locale
        String regionCode = trimmedPhone.startsWith("+") 
            ? null 
            : extractRegionFromLocale(locale);

        try {
            PhoneNumber phoneNumber = phoneUtil.parse(trimmedPhone, regionCode);
            if (phoneUtil.isValidNumber(phoneNumber)) {
                return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
        } catch (NumberParseException e) {
            // Return null if cannot parse
        }

        return null;
    }

    /**
     * Extracts the ISO 3166-1 alpha-2 country code from a locale string.
     * 
     * @param locale the locale string (e.g., "pt-BR", "en-US")
     * @return the ISO country code (e.g., "BR", "US"), or null if not found
     */
    private static String extractRegionFromLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            return null;
        }

        // Validate if locale is supported
        if (!LocaleCountryEnum.isValid(locale)) {
            return null;
        }

        // Extract country code from locale (e.g., "pt-BR" -> "BR")
        String[] parts = locale.trim().split("-");
        if (parts.length == 2) {
            return parts[1].toUpperCase();
        }

        return null;
    }

    /**
     * Gets phone number type (MOBILE, FIXED_LINE, etc.).
     * 
     * @param phone the phone number
     * @param locale the user's locale
     * @return the phone number type, or null if invalid
     */
    public static PhoneNumberUtil.PhoneNumberType getPhoneType(String phone, String locale) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String trimmedPhone = phone.trim();
        
        // If phone starts with +, use null as region to auto-detect
        // Otherwise, use the region from locale
        String regionCode = trimmedPhone.startsWith("+") 
            ? null 
            : extractRegionFromLocale(locale);

        try {
            PhoneNumber phoneNumber = phoneUtil.parse(trimmedPhone, regionCode);
            
            // Check if the number is valid before returning the type
            if (!phoneUtil.isValidNumber(phoneNumber)) {
                return null;
            }
            
            return phoneUtil.getNumberType(phoneNumber);
        } catch (NumberParseException e) {
            return null;
        }
    }

    /**
     * Checks if the phone number is a mobile number.
     * 
     * @param phone the phone number
     * @param locale the user's locale
     * @return true if mobile, false otherwise
     */
    public static boolean isMobile(String phone, String locale) {
        PhoneNumberUtil.PhoneNumberType type = getPhoneType(phone, locale);
        return type == PhoneNumberUtil.PhoneNumberType.MOBILE ||
               type == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE;
    }
}

