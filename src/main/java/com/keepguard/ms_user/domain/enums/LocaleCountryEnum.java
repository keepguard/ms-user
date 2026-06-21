package com.keepguard.ms_user.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Enum representing valid combinations of language and country (locale).
 * Provides comprehensive locale support for different regions worldwide.
 * 
 * <p>This enum follows the ISO 639-1 language codes and ISO 3166-1 alpha-2 country codes
 * standard format: language-COUNTRY (e.g., pt-BR, en-US).</p>
 * 
 * @since 1.0
 */
@Schema(description = "Enum representing valid combinations of language and country (locale).")
public enum LocaleCountryEnum {

    // 🌎 Americas
    ARGENTINA("es-AR", "Argentina"),
    BRAZIL("pt-BR", "Brazil"),
    CANADA("en-CA", "Canada"),
    CHILE("es-CL", "Chile"),
    COLOMBIA("es-CO", "Colombia"),
    MEXICO("es-MX", "Mexico"),
    PERU("es-PE", "Peru"),
    UNITED_STATES("en-US", "United States"),
    URUGUAY("es-UY", "Uruguay"),
    VENEZUELA("es-VE", "Venezuela"),

    // 🌍 Europe
    AUSTRIA("de-AT", "Austria"),
    BELGIUM("nl-BE", "Belgium"),
    CROATIA("hr-HR", "Croatia"),
    CZECH_REPUBLIC("cs-CZ", "Czech Republic"),
    DENMARK("da-DK", "Denmark"),
    FINLAND("fi-FI", "Finland"),
    FRANCE("fr-FR", "France"),
    GERMANY("de-DE", "Germany"),
    GREECE("el-GR", "Greece"),
    HUNGARY("hu-HU", "Hungary"),
    IRELAND("en-IE", "Ireland"),
    ITALY("it-IT", "Italy"),
    NETHERLANDS("nl-NL", "Netherlands"),
    NORWAY("no-NO", "Norway"),
    POLAND("pl-PL", "Poland"),
    PORTUGAL("pt-PT", "Portugal"),
    ROMANIA("ro-RO", "Romania"),
    RUSSIA("ru-RU", "Russia"),
    SPAIN("es-ES", "Spain"),
    SWEDEN("sv-SE", "Sweden"),
    SWITZERLAND("de-CH", "Switzerland"),
    UNITED_KINGDOM("en-GB", "United Kingdom"),

    // 🌍 Africa
    ALGERIA("ar-DZ", "Algeria"),
    ANGOLA("pt-AO", "Angola"),
    EGYPT("ar-EG", "Egypt"),
    KENYA("en-KE", "Kenya"),
    MOROCCO("ar-MA", "Morocco"),
    MOZAMBIQUE("pt-MZ", "Mozambique"),
    NIGERIA("en-NG", "Nigeria"),
    SENEGAL("fr-SN", "Senegal"),
    SOUTH_AFRICA("en-ZA", "South Africa"),
    TUNISIA("ar-TN", "Tunisia"),

    // 🌏 Asia & Oceania
    AUSTRALIA("en-AU", "Australia"),
    BANGLADESH("bn-BD", "Bangladesh"),
    CHINA("zh-CN", "China"),
    HONG_KONG("zh-HK", "Hong Kong"),
    INDIA("hi-IN", "India"),
    INDONESIA("id-ID", "Indonesia"),
    ISRAEL("he-IL", "Israel"),
    JAPAN("ja-JP", "Japan"),
    MALAYSIA("ms-MY", "Malaysia"),
    NEW_ZEALAND("en-NZ", "New Zealand"),
    PAKISTAN("ur-PK", "Pakistan"),
    PHILIPPINES("en-PH", "Philippines"),
    SAUDI_ARABIA("ar-SA", "Saudi Arabia"),
    SINGAPORE("en-SG", "Singapore"),
    SOUTH_KOREA("ko-KR", "South Korea"),
    TAIWAN("zh-TW", "Taiwan"),
    THAILAND("th-TH", "Thailand"),
    TURKEY("tr-TR", "Turkey"),
    UNITED_ARAB_EMIRATES("ar-AE", "United Arab Emirates"),
    VIETNAM("vi-VN", "Vietnam");

    private final String code;
    private final String country;

    LocaleCountryEnum(String code, String country) {
        this.code = code;
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Find enum by locale code (e.g. "pt-BR").
     * Case-insensitive search.
     *
     * @param code locale code (e.g. "pt-BR")
     * @return matching LocaleCountryEnum
     * @throws IllegalArgumentException if not found
     */
    public static LocaleCountryEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Locale code cannot be null or empty");
        }
        
        return Arrays.stream(values())
                .filter(locale -> locale.code.equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid locale: " + code));
    }

    /**
     * Checks if a given locale code is valid.
     *
     * @param code locale code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        
        return Arrays.stream(values())
                .anyMatch(locale -> locale.code.equalsIgnoreCase(code.trim()));
    }

    @Override
    public String toString() {
        return code + " (" + country + ")";
    }
}

