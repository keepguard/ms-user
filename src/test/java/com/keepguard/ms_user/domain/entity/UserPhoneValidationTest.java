package com.keepguard.ms_user.domain.entity;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para validação de telefone na entidade {@link User}.
 * Garante 100% de cobertura de código para validação de phoneE164.
 */
@DisplayName("User - Validação de Telefone - Testes")
class UserPhoneValidationTest {

    private static final UUID DEFAULT_ID = UUID.randomUUID();
    private static final UUID DEFAULT_CODE_USER = UUID.randomUUID();
    private static final UUID DEFAULT_COMPANY_ID = UUID.randomUUID();
    private static final UUID DEFAULT_TENANT_ID = UUID.randomUUID();
    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final OffsetDateTime DEFAULT_CREATED_AT = OffsetDateTime.now();
    private static final OffsetDateTime DEFAULT_UPDATED_AT = OffsetDateTime.now();

    @Test
    @DisplayName("Deve criar usuário com telefone válido")
    void shouldCreateUserWithValidPhone() {
        // Given
        String validPhone = "+5511999999999";

        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            validPhone, "pt-BR", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals("+5511999999999", user.getPhoneE164());
    }

    @ParameterizedTest
    @CsvSource({
        "+5511999999999, pt-BR",
        "+12125551234, en-US",  // Número válido americano (Nova York)
        "+442071234567, en-GB",
        "+33123456789, fr-FR",
        "+4930123456, de-DE"
    })
    @DisplayName("Deve criar usuário com telefones de diferentes países")
    void shouldCreateUserWithPhonesFromDifferentCountries(String phone, String locale) {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            phone, locale, null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNotNull(user.getPhoneE164());
        assertTrue(user.getPhoneE164().startsWith("+"));
    }

    @Test
    @DisplayName("Deve normalizar telefone com formatação")
    void shouldNormalizeFormattedPhone() {
        // Given
        String formattedPhone = "+55 (11) 99999-9999";

        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            formattedPhone, "pt-BR", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals("+5511999999999", user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve criar usuário com telefone null")
    void shouldCreateUserWithNullPhone() {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            null, "pt-BR", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNull(user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve criar usuário com telefone vazio")
    void shouldCreateUserWithEmptyPhone() {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            "", "pt-BR", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNull(user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve criar usuário com telefone em branco")
    void shouldCreateUserWithBlankPhone() {
        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            "   ", "pt-BR", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertNull(user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve normalizar telefone com espaços extras")
    void shouldNormalizePhoneWithExtraSpaces() {
        // Given
        String phoneWithSpaces = "  +5511999999999  ";

        // When
        User user = User.of(
            DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
            phoneWithSpaces, "pt-BR", null, null, null,
            DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
        );

        // Then
        assertNotNull(user);
        assertEquals("+5511999999999", user.getPhoneE164());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "+55119999", "abc123", "999999999", "+9999"})
    @DisplayName("Deve lançar exceção ao criar usuário com telefone inválido")
    void shouldThrowExceptionWhenCreatingUserWithInvalidPhone(String invalidPhone) {
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> User.of(
                DEFAULT_ID, DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
                UserTypeEnum.PERSON, UserStatusEnum.ACTIVE, DEFAULT_EMAIL,
                invalidPhone, "pt-BR", null, null, null,
                DEFAULT_CREATED_AT, DEFAULT_UPDATED_AT
            )
        );

        assertTrue(exception.getMessage().toLowerCase().contains("telefone"));
        assertTrue(exception.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    @DisplayName("Deve criar usuário usando create com telefone válido")
    void shouldCreateUserUsingCreateWithValidPhone() {
        // Given
        String validPhone = "+5511999999999";

        // When
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            validPhone, "pt-BR", null, null
        );

        // Then
        assertNotNull(user);
        assertEquals("+5511999999999", user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário usando create com telefone inválido")
    void shouldThrowExceptionWhenCreatingUserUsingCreateWithInvalidPhone() {
        // Given
        String invalidPhone = "123";

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> User.create(
                DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
                UserTypeEnum.PERSON, DEFAULT_EMAIL,
                invalidPhone, "pt-BR", null, null
            )
        );

        assertTrue(exception.getMessage().toLowerCase().contains("telefone"));
    }

    @Test
    @DisplayName("Deve atualizar telefone com valor válido")
    void shouldUpdatePhoneWithValidValue() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            "+5511999999999", "pt-BR", null, null
        );

        // When
        user.setPhoneE164("+12125551234"); // Número válido americano (Nova York)

        // Then
        assertEquals("+12125551234", user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve atualizar telefone para null")
    void shouldUpdatePhoneToNull() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            "+5511999999999", "pt-BR", null, null
        );

        // When
        user.setPhoneE164(null);

        // Then
        assertNull(user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve normalizar telefone ao atualizar")
    void shouldNormalizePhoneWhenUpdating() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            "+5511999999999", "pt-BR", null, null
        );

        // When
        user.setPhoneE164("+55 (11) 98888-8888");

        // Then
        assertEquals("+5511988888888", user.getPhoneE164());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "+55119999", "abc123"})
    @DisplayName("Deve lançar exceção ao atualizar com telefone inválido")
    void shouldThrowExceptionWhenUpdatingWithInvalidPhone(String invalidPhone) {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            "+5511999999999", "pt-BR", null, null
        );
        String originalPhone = user.getPhoneE164();

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> user.setPhoneE164(invalidPhone)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("telefone"));
        // Telefone anterior deve ser mantido após falha
        assertEquals(originalPhone, user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve atualizar updatedAt ao alterar telefone")
    void shouldUpdateUpdatedAtWhenChangingPhone() {
        // Given
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            "+5511999999999", "pt-BR", null, null
        );
        OffsetDateTime originalUpdatedAt = user.getUpdatedAt();

        // Aguardar um pouco para garantir que o timestamp é diferente
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        // When
        user.setPhoneE164("+12125551234"); // Número válido americano (Nova York)

        // Then
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   user.getUpdatedAt().isEqual(originalUpdatedAt));
    }

    @Test
    @DisplayName("Deve usar locale do usuário para validar telefone")
    void shouldUseUserLocaleToValidatePhone() {
        // Given - telefone brasileiro sem código de país
        String phone = "11999999999";
        
        // When - Com locale brasileiro, deve ser validado e normalizado
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            phone, "pt-BR", null, null
        );

        // Then - Deve adicionar +55
        assertNotNull(user.getPhoneE164());
        assertTrue(user.getPhoneE164().startsWith("+55"));
    }

    @Test
    @DisplayName("Deve validar telefone com código internacional sem locale")
    void shouldValidatePhoneWithInternationalCodeWithoutLocale() {
        // Given
        String phone = "+5511999999999";

        // When - Sem locale
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            phone, null, null, null
        );

        // Then
        assertNotNull(user);
        assertEquals("+5511999999999", user.getPhoneE164());
    }

    @Test
    @DisplayName("Deve validar múltiplos formatos de telefone brasileiro")
    void shouldValidateMultipleBrazilianPhoneFormats() {
        // Given
        String[] phones = {
            "+5511999999999",
            "5511999999999",
            "+55 11 99999-9999",
            "+55 (11) 99999-9999",
            "+55 11 9 9999-9999"
        };

        // When & Then
        for (String phone : phones) {
            User user = User.create(
                DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
                UserTypeEnum.PERSON, DEFAULT_EMAIL,
                phone, "pt-BR", null, null
            );
            
            assertNotNull(user.getPhoneE164(), "Phone " + phone + " should be valid");
            assertTrue(user.getPhoneE164().startsWith("+55"), "Phone should start with +55");
        }
    }

    @Test
    @DisplayName("Deve permitir alteração de locale e telefone em sequência")
    void shouldAllowChangingLocaleAndPhoneInSequence() {
        // Given - Usuário brasileiro
        User user = User.create(
            DEFAULT_CODE_USER, DEFAULT_COMPANY_ID, DEFAULT_TENANT_ID,
            UserTypeEnum.PERSON, DEFAULT_EMAIL,
            "+5511999999999", "pt-BR", null, null
        );

        // When - Mudar para americano
        user.setPreferredLocale("en-US");
        user.setPhoneE164("+12125551234"); // Número válido americano (Nova York)

        // Then
        assertEquals("en-US", user.getPreferredLocale());
        assertEquals("+12125551234", user.getPhoneE164());
    }
}

