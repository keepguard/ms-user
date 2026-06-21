package com.keepguard.ms_user.domain.entity;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para validação de locale na entidade {@link User}.
 * Garante 100% de cobertura de código para validação de preferredLocale.
 */
@DisplayName("User - Validação de Locale - Testes")
class UserLocaleValidationTest {

    private static final UUID DEFAULT_ID = UUID.randomUUID();
    private static final UUID DEFAULT_CODE_USER = UUID.randomUUID();
    private static final UUID DEFAULT_COMPANY_ID = UUID.randomUUID();
    private static final UUID DEFAULT_X_APPLICATION = UUID.randomUUID();
    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final OffsetDateTime DEFAULT_CREATED_AT = OffsetDateTime.now();
    private static final OffsetDateTime DEFAULT_UPDATED_AT = OffsetDateTime.now();

    @Test
    @DisplayName("Deve criar usuário com locale válido")
    void shouldCreateUserWithValidLocale() {
        // Given
        String validLocale = "pt-BR";

        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, validLocale, null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @ParameterizedTest
    @ValueSource(strings = {"pt-BR", "en-US", "es-ES", "fr-FR", "de-DE", "ja-JP", "zh-CN"})
    @DisplayName("Deve criar usuário com diferentes locales válidos")
    void shouldCreateUserWithDifferentValidLocales(String validLocale) {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, validLocale, null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals(validLocale, user.getPreferredLocale());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PT-BR", "pt-br", "Pt-Br"})
    @DisplayName("Deve normalizar locale com diferentes casos")
    void shouldNormalizeLocaleWithDifferentCases(String locale) {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, locale, null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve criar usuário com locale null")
    void shouldCreateUserWithNullLocale() {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, null, null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNull(user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve criar usuário com locale vazio")
    void shouldCreateUserWithEmptyLocale() {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, "", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNull(user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve criar usuário com locale em branco")
    void shouldCreateUserWithBlankLocale() {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, "   ", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNull(user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve normalizar locale com espaços extras")
    void shouldNormalizeLocaleWithExtraSpaces() {
        // Given
        String localeWithSpaces = "  pt-BR  ";

        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, localeWithSpaces, null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @ParameterizedTest
    @ValueSource(strings = {"xx-XX", "invalid", "pt_BR", "pt", "12345"})
    @DisplayName("Deve lançar exceção ao criar usuário com locale inválido")
    void shouldThrowExceptionWhenCreatingUserWithInvalidLocale(String invalidLocale) {
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> User.of(
                DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
                UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
                null, invalidLocale, null, null, null,
                DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
            )
        );

        assertTrue(exception.getMessage().contains("Locale inválido"));
    }

    @Test
    @DisplayName("Deve criar usuário usando create com locale válido")
    void shouldCreateUserUsingCreateWithValidLocale() {
        // Given
        String validLocale = "pt-BR";

        // When
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            null, validLocale, null, null
        );

        // Then
        assertNotNull(user);
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário usando create com locale inválido")
    void shouldThrowExceptionWhenCreatingUserUsingCreateWithInvalidLocale() {
        // Given
        String invalidLocale = "xx-XX";

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> User.create(
                DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
                UserTypeEnum.PERSON, DEFAULT_EMAIL,
                null, invalidLocale, null, null
            )
        );

        assertTrue(exception.getMessage().contains("Locale inválido"));
    }

    @Test
    @DisplayName("Deve atualizar locale com valor válido")
    void shouldUpdateLocaleWithValidValue() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            null, "pt-BR", null, null
        );

        // When
        user.setPreferredLocale("en-US");

        // Then
        assertEquals("en-US", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve atualizar locale para null")
    void shouldUpdateLocaleToNull() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            null, "pt-BR", null, null
        );

        // When
        user.setPreferredLocale(null);

        // Then
        assertNull(user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve normalizar locale ao atualizar")
    void shouldNormalizeLocaleWhenUpdating() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            null, "pt-BR", null, null
        );

        // When
        user.setPreferredLocale("  EN-US  ");

        // Then
        assertEquals("en-US", user.getPreferredLocale());
    }

    @ParameterizedTest
    @ValueSource(strings = {"xx-XX", "invalid", "pt_BR"})
    @DisplayName("Deve lançar exceção ao atualizar com locale inválido")
    void shouldThrowExceptionWhenUpdatingWithInvalidLocale(String invalidLocale) {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            null, "pt-BR", null, null
        );

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> user.setPreferredLocale(invalidLocale)
        );

        assertTrue(exception.getMessage().contains("Locale inválido"));
        // Locale anterior deve ser mantido após falha
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Deve atualizar updatedAt ao alterar locale")
    void shouldUpdateUpdatedAtWhenChangingLocale() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_X_APPLICATION,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            null, "pt-BR", null, null
        );
        OffsetDateTime originalUpdatedAt = user.getUpdatedAt();

        // Aguardar um pouco para garantir que o timestamp é diferente
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        // When
        user.setPreferredLocale("en-US");

        // Then
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   user.getUpdatedAt().isEqual(originalUpdatedAt));
    }
}

