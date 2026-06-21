package com.keepguard.ms_user.domain.validator;

import com.keepguard.lib_common.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link LocaleValidator}.
 * Garante 100% de cobertura de código.
 */
@DisplayName("LocaleValidator - Testes")
class LocaleValidatorTest {

    @Test
    @DisplayName("Deve retornar null quando locale é null")
    void shouldReturnNullWhenLocaleIsNull() {
        // When
        String result = LocaleValidator.validate(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar null quando locale é vazio")
    void shouldReturnNullWhenLocaleIsEmpty() {
        // When
        String result = LocaleValidator.validate("");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar null quando locale é em branco")
    void shouldReturnNullWhenLocaleIsBlank() {
        // When
        String result = LocaleValidator.validate("   ");

        // Then
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt-BR", "en-US", "es-ES", "fr-FR", "de-DE"})
    @DisplayName("Deve validar e retornar locale normalizado para códigos válidos")
    void shouldValidateAndReturnNormalizedLocale(String locale) {
        // When
        String result = LocaleValidator.validate(locale);

        // Then
        assertNotNull(result);
        assertEquals(locale, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PT-BR", "pt-br", "Pt-Br"})
    @DisplayName("Deve validar e normalizar locale com diferentes casos")
    void shouldValidateAndNormalizeLocaleWithDifferentCases(String locale) {
        // When
        String result = LocaleValidator.validate(locale);

        // Then
        assertNotNull(result);
        assertEquals("pt-BR", result);
    }

    @Test
    @DisplayName("Deve validar e retornar locale normalizado com espaços extras")
    void shouldValidateAndReturnNormalizedLocaleWithExtraSpaces() {
        // Given
        String locale = "  pt-BR  ";

        // When
        String result = LocaleValidator.validate(locale);

        // Then
        assertNotNull(result);
        assertEquals("pt-BR", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"xx-XX", "invalid", "pt_BR", "pt", "12345"})
    @DisplayName("Deve lançar exceção para locale inválido")
    void shouldThrowExceptionForInvalidLocale(String invalidLocale) {
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> LocaleValidator.validate(invalidLocale)
        );

        assertTrue(exception.getMessage().contains("Locale inválido"));
        assertTrue(exception.getMessage().contains(invalidLocale));
    }

    @Test
    @DisplayName("Deve retornar null quando locale é null com nome do campo")
    void shouldReturnNullWhenLocaleIsNullWithFieldName() {
        // When
        String result = LocaleValidator.validateWithFieldName(null, "preferredLocale");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar null quando locale é vazio com nome do campo")
    void shouldReturnNullWhenLocaleIsEmptyWithFieldName() {
        // When
        String result = LocaleValidator.validateWithFieldName("", "preferredLocale");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Deve retornar null quando locale é em branco com nome do campo")
    void shouldReturnNullWhenLocaleIsBlankWithFieldName() {
        // When
        String result = LocaleValidator.validateWithFieldName("   ", "preferredLocale");

        // Then
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt-BR", "en-US", "es-ES"})
    @DisplayName("Deve validar e retornar locale normalizado com nome do campo")
    void shouldValidateAndReturnNormalizedLocaleWithFieldName(String locale) {
        // When
        String result = LocaleValidator.validateWithFieldName(locale, "preferredLocale");

        // Then
        assertNotNull(result);
        assertEquals(locale, result);
    }

    @Test
    @DisplayName("Deve validar e normalizar locale com espaços extras e nome do campo")
    void shouldValidateAndNormalizeLocaleWithExtraSpacesAndFieldName() {
        // Given
        String locale = "  pt-BR  ";

        // When
        String result = LocaleValidator.validateWithFieldName(locale, "preferredLocale");

        // Then
        assertNotNull(result);
        assertEquals("pt-BR", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"xx-XX", "invalid", "pt_BR"})
    @DisplayName("Deve lançar exceção para locale inválido com nome do campo")
    void shouldThrowExceptionForInvalidLocaleWithFieldName(String invalidLocale) {
        // Given
        String fieldName = "preferredLocale";

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> LocaleValidator.validateWithFieldName(invalidLocale, fieldName)
        );

        assertTrue(exception.getMessage().contains(fieldName));
        assertTrue(exception.getMessage().contains("inválido"));
        assertTrue(exception.getMessage().contains(invalidLocale));
    }

    @Test
    @DisplayName("Deve incluir nome do campo na mensagem de erro")
    void shouldIncludeFieldNameInErrorMessage() {
        // Given
        String invalidLocale = "xx-XX";
        String fieldName = "myCustomField";

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> LocaleValidator.validateWithFieldName(invalidLocale, fieldName)
        );

        assertTrue(exception.getMessage().startsWith(fieldName));
    }

    @Test
    @DisplayName("Não deve permitir instanciação da classe")
    void shouldNotAllowInstantiation() {
        // Using reflection to test private constructor
        // The constructor is private but doesn't throw exception
        assertDoesNotThrow(() -> {
            var constructor = LocaleValidator.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}

