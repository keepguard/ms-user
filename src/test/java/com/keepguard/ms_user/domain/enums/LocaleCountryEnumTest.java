package com.keepguard.ms_user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para {@link LocaleCountryEnum}.
 * Garante 100% de cobertura de código.
 */
@DisplayName("LocaleCountryEnum - Testes")
class LocaleCountryEnumTest {

    @Test
    @DisplayName("Deve retornar código do locale")
    void shouldReturnLocaleCode() {
        // Given
        LocaleCountryEnum locale = LocaleCountryEnum.BRAZIL;

        // When
        String code = locale.getCode();

        // Then
        assertEquals("pt-BR", code);
    }

    @Test
    @DisplayName("Deve retornar país do locale")
    void shouldReturnCountry() {
        // Given
        LocaleCountryEnum locale = LocaleCountryEnum.BRAZIL;

        // When
        String country = locale.getCountry();

        // Then
        assertEquals("Brazil", country);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt-BR", "PT-BR", "pt-br", "Pt-Br"})
    @DisplayName("Deve encontrar locale por código (case insensitive)")
    void shouldFindLocaleByCodeCaseInsensitive(String code) {
        // When
        LocaleCountryEnum result = LocaleCountryEnum.fromCode(code);

        // Then
        assertEquals(LocaleCountryEnum.BRAZIL, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "pt-BR", "en-US", "es-ES", "fr-FR", "de-DE", 
        "en-GB", "pt-PT", "es-MX", "ja-JP", "zh-CN"
    })
    @DisplayName("Deve encontrar todos os locales válidos")
    void shouldFindAllValidLocales(String code) {
        // When
        LocaleCountryEnum result = LocaleCountryEnum.fromCode(code);

        // Then
        assertNotNull(result);
        assertEquals(code, result.getCode());
    }

    @Test
    @DisplayName("Deve encontrar locale com espaços extras")
    void shouldFindLocaleWithExtraSpaces() {
        // Given
        String code = "  pt-BR  ";

        // When
        LocaleCountryEnum result = LocaleCountryEnum.fromCode(code);

        // Then
        assertEquals(LocaleCountryEnum.BRAZIL, result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar código inválido")
    void shouldThrowExceptionWhenCodeIsInvalid() {
        // Given
        String invalidCode = "xx-XX";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LocaleCountryEnum.fromCode(invalidCode)
        );

        assertTrue(exception.getMessage().contains("Invalid locale"));
        assertTrue(exception.getMessage().contains(invalidCode));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar código nulo")
    void shouldThrowExceptionWhenCodeIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LocaleCountryEnum.fromCode(null)
        );

        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar código vazio")
    void shouldThrowExceptionWhenCodeIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LocaleCountryEnum.fromCode("")
        );

        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar código em branco")
    void shouldThrowExceptionWhenCodeIsBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LocaleCountryEnum.fromCode("   ")
        );

        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt-BR", "en-US", "es-ES", "fr-FR", "de-DE"})
    @DisplayName("Deve validar códigos válidos")
    void shouldValidateValidCodes(String code) {
        // When
        boolean isValid = LocaleCountryEnum.isValid(code);

        // Then
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PT-BR", "En-Us", "ES-es"})
    @DisplayName("Deve validar códigos válidos (case insensitive)")
    void shouldValidateValidCodesCaseInsensitive(String code) {
        // When
        boolean isValid = LocaleCountryEnum.isValid(code);

        // Then
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"xx-XX", "invalid", "pt_BR", "pt"})
    @DisplayName("Deve invalidar códigos inválidos")
    void shouldInvalidateInvalidCodes(String code) {
        // When
        boolean isValid = LocaleCountryEnum.isValid(code);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar código nulo")
    void shouldInvalidateNullCode() {
        // When
        boolean isValid = LocaleCountryEnum.isValid(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar código vazio")
    void shouldInvalidateEmptyCode() {
        // When
        boolean isValid = LocaleCountryEnum.isValid("");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar código em branco")
    void shouldInvalidateBlankCode() {
        // When
        boolean isValid = LocaleCountryEnum.isValid("   ");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve validar código com espaços extras")
    void shouldValidateCodeWithExtraSpaces() {
        // When
        boolean isValid = LocaleCountryEnum.isValid("  pt-BR  ");

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve retornar string formatada com toString")
    void shouldReturnFormattedStringWithToString() {
        // Given
        LocaleCountryEnum locale = LocaleCountryEnum.BRAZIL;

        // When
        String result = locale.toString();

        // Then
        assertEquals("pt-BR (Brazil)", result);
    }

    @Test
    @DisplayName("Deve conter todos os locales das Américas")
    void shouldContainAllAmericasLocales() {
        assertNotNull(LocaleCountryEnum.ARGENTINA);
        assertNotNull(LocaleCountryEnum.BRAZIL);
        assertNotNull(LocaleCountryEnum.CANADA);
        assertNotNull(LocaleCountryEnum.CHILE);
        assertNotNull(LocaleCountryEnum.COLOMBIA);
        assertNotNull(LocaleCountryEnum.MEXICO);
        assertNotNull(LocaleCountryEnum.PERU);
        assertNotNull(LocaleCountryEnum.UNITED_STATES);
        assertNotNull(LocaleCountryEnum.URUGUAY);
        assertNotNull(LocaleCountryEnum.VENEZUELA);
    }

    @Test
    @DisplayName("Deve conter todos os locales da Europa")
    void shouldContainAllEuropeLocales() {
        assertNotNull(LocaleCountryEnum.GERMANY);
        assertNotNull(LocaleCountryEnum.SPAIN);
        assertNotNull(LocaleCountryEnum.FRANCE);
        assertNotNull(LocaleCountryEnum.ITALY);
        assertNotNull(LocaleCountryEnum.PORTUGAL);
        assertNotNull(LocaleCountryEnum.UNITED_KINGDOM);
    }

    @Test
    @DisplayName("Deve conter todos os locales da África")
    void shouldContainAllAfricaLocales() {
        assertNotNull(LocaleCountryEnum.SOUTH_AFRICA);
        assertNotNull(LocaleCountryEnum.EGYPT);
        assertNotNull(LocaleCountryEnum.KENYA);
        assertNotNull(LocaleCountryEnum.NIGERIA);
    }

    @Test
    @DisplayName("Deve conter todos os locales da Ásia e Oceania")
    void shouldContainAllAsiaOceaniaLocales() {
        assertNotNull(LocaleCountryEnum.AUSTRALIA);
        assertNotNull(LocaleCountryEnum.CHINA);
        assertNotNull(LocaleCountryEnum.JAPAN);
        assertNotNull(LocaleCountryEnum.INDIA);
        assertNotNull(LocaleCountryEnum.SINGAPORE);
        assertNotNull(LocaleCountryEnum.SOUTH_KOREA);
    }
}

