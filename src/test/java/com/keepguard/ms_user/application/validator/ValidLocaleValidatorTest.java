package com.keepguard.ms_user.application.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link ValidLocaleValidator}.
 * Garante 100% de cobertura de código.
 */
@DisplayName("ValidLocaleValidator - Testes")
class ValidLocaleValidatorTest {

    private ValidLocaleValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ValidLocale annotation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ValidLocaleValidator();
        validator.initialize(annotation);
    }

    @Test
    @DisplayName("Deve considerar null como válido")
    void shouldConsiderNullAsValid() {
        // When
        boolean isValid = validator.isValid(null, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve considerar string vazia como válida")
    void shouldConsiderEmptyStringAsValid() {
        // When
        boolean isValid = validator.isValid("", context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve considerar string em branco como válida")
    void shouldConsiderBlankStringAsValid() {
        // When
        boolean isValid = validator.isValid("   ", context);

        // Then
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt-BR", "en-US", "es-ES", "fr-FR", "de-DE"})
    @DisplayName("Deve validar locales válidos")
    void shouldValidateValidLocales(String locale) {
        // When
        boolean isValid = validator.isValid(locale, context);

        // Then
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PT-BR", "pt-br", "Pt-Br", "EN-US", "en-us"})
    @DisplayName("Deve validar locales válidos com diferentes casos")
    void shouldValidateValidLocalesWithDifferentCases(String locale) {
        // When
        boolean isValid = validator.isValid(locale, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar locale com espaços extras")
    void shouldValidateLocaleWithExtraSpaces() {
        // Given
        String locale = "  pt-BR  ";

        // When
        boolean isValid = validator.isValid(locale, context);

        // Then
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"xx-XX", "invalid", "pt_BR", "pt", "12345", "abc"})
    @DisplayName("Deve invalidar locales inválidos")
    void shouldInvalidateInvalidLocales(String locale) {
        // When
        boolean isValid = validator.isValid(locale, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve validar todos os locales suportados")
    void shouldValidateAllSupportedLocales() {
        // Given
        String[] supportedLocales = {
            // Americas
            "es-AR", "pt-BR", "en-CA", "es-CL", "es-CO", "es-MX", "es-PE", "en-US", "es-UY", "es-VE",
            // Europe
            "de-AT", "nl-BE", "hr-HR", "cs-CZ", "da-DK", "fi-FI", "fr-FR", "de-DE", "el-GR", "hu-HU",
            "en-IE", "it-IT", "nl-NL", "no-NO", "pl-PL", "pt-PT", "ro-RO", "ru-RU", "es-ES", "sv-SE",
            "de-CH", "en-GB",
            // Africa
            "ar-DZ", "pt-AO", "ar-EG", "en-KE", "ar-MA", "pt-MZ", "en-NG", "fr-SN", "en-ZA", "ar-TN",
            // Asia & Oceania
            "en-AU", "bn-BD", "zh-CN", "zh-HK", "hi-IN", "id-ID", "he-IL", "ja-JP", "ms-MY", "en-NZ",
            "ur-PK", "en-PH", "ar-SA", "en-SG", "ko-KR", "zh-TW", "th-TH", "tr-TR", "ar-AE", "vi-VN"
        };

        // When & Then
        for (String locale : supportedLocales) {
            assertTrue(
                validator.isValid(locale, context),
                "Locale " + locale + " deveria ser válido"
            );
        }
    }

    @Test
    @DisplayName("Deve chamar initialize sem erros")
    void shouldCallInitializeWithoutErrors() {
        // When & Then
        assertDoesNotThrow(() -> validator.initialize(annotation));
    }
}

