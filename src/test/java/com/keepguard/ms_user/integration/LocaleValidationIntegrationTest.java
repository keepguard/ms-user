package com.keepguard.ms_user.integration;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para validação de locale.
 * Demonstra o fluxo completo de validação do campo preferredLocale.
 */
@DisplayName("Validação de Locale - Teste de Integração")
class LocaleValidationIntegrationTest {

    @Test
    @DisplayName("Cenário 1: Cadastro com locale válido deve funcionar")
    void scenario1_CreateUserWithValidLocale() {
        // Given - Dados de um novo usuário com locale brasileiro
        UUID companyId = UUID.randomUUID();
        UUID xApplication = UUID.randomUUID();
        String email = "usuario@example.com";
        String locale = "pt-BR";

        // When - Criar usuário
        User user = User.create(
            UUID.randomUUID(),
            companyId,
            xApplication,
            UserTypeEnum.PERSON,
            email,
            null,
            locale,
            "America/Sao_Paulo",
            null
        );

        // Then - Usuário criado com sucesso e locale normalizado
        assertNotNull(user);
        assertEquals("pt-BR", user.getPreferredLocale());
        assertEquals(UserStatusEnum.PENDING, user.getStatus());
    }

    @Test
    @DisplayName("Cenário 2: Cadastro com locale case-insensitive deve normalizar")
    void scenario2_CreateUserWithMixedCaseLocale() {
        // Given - Locale em formato misto
        UUID companyId = UUID.randomUUID();
        UUID xApplication = UUID.randomUUID();
        String email = "usuario@example.com";
        String locale = "PT-br";  // Case incorreto

        // When - Criar usuário
        User user = User.create(
            UUID.randomUUID(),
            companyId,
            xApplication,
            UserTypeEnum.PERSON,
            email,
            null,
            locale,
            null,
            null
        );

        // Then - Locale normalizado para formato correto
        assertNotNull(user);
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Cenário 3: Cadastro sem locale deve permitir null")
    void scenario3_CreateUserWithoutLocale() {
        // Given - Usuário sem locale
        UUID companyId = UUID.randomUUID();
        UUID xApplication = UUID.randomUUID();
        String email = "usuario@example.com";

        // When - Criar usuário sem locale
        User user = User.create(
            UUID.randomUUID(),
            companyId,
            xApplication,
            UserTypeEnum.PERSON,
            email,
            null,
            null,  // Sem locale
            null,
            null
        );

        // Then - Usuário criado com locale null
        assertNotNull(user);
        assertNull(user.getPreferredLocale());
    }

    @Test
    @DisplayName("Cenário 4: Cadastro com locale inválido deve falhar")
    void scenario4_CreateUserWithInvalidLocale() {
        // Given - Locale inválido
        UUID companyId = UUID.randomUUID();
        UUID xApplication = UUID.randomUUID();
        String email = "usuario@example.com";
        String invalidLocale = "xx-XX";

        // When & Then - Deve lançar exceção
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> User.create(
                UUID.randomUUID(),
                companyId,
                xApplication,
                UserTypeEnum.PERSON,
                email,
                null,
                invalidLocale,
                null,
                null
            )
        );

        assertTrue(exception.getMessage().contains("Locale inválido"));
        assertTrue(exception.getMessage().contains(invalidLocale));
    }

    @Test
    @DisplayName("Cenário 5: Atualizar locale válido deve funcionar")
    void scenario5_UpdateUserWithValidLocale() {
        // Given - Usuário existente com locale brasileiro
        User user = User.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UserTypeEnum.PERSON,
            "usuario@example.com",
            null,
            "pt-BR",
            null,
            null
        );

        // When - Atualizar para locale americano
        user.setPreferredLocale("en-US");

        // Then - Locale atualizado com sucesso
        assertEquals("en-US", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Cenário 6: Atualizar para locale inválido deve falhar e manter valor anterior")
    void scenario6_UpdateUserWithInvalidLocale() {
        // Given - Usuário existente com locale brasileiro
        User user = User.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UserTypeEnum.PERSON,
            "usuario@example.com",
            null,
            "pt-BR",
            null,
            null
        );
        String originalLocale = user.getPreferredLocale();

        // When & Then - Tentar atualizar para locale inválido
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> user.setPreferredLocale("invalid-locale")
        );

        // Then - Exceção lançada e locale anterior mantido
        assertTrue(exception.getMessage().contains("Locale inválido"));
        assertEquals(originalLocale, user.getPreferredLocale());
        assertEquals("pt-BR", user.getPreferredLocale());
    }

    @Test
    @DisplayName("Cenário 7: Testar múltiplos locales de diferentes regiões")
    void scenario7_TestMultipleRegionalLocales() {
        // Given - Lista de locales válidos de diferentes regiões
        String[] validLocales = {
            "pt-BR",  // América do Sul
            "en-US",  // América do Norte
            "es-ES",  // Europa
            "fr-FR",  // Europa
            "ja-JP",  // Ásia
            "ar-SA",  // Oriente Médio
            "en-ZA",  // África
            "en-AU"   // Oceania
        };

        // When & Then - Todos devem ser aceitos
        for (String locale : validLocales) {
            User user = User.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UserTypeEnum.PERSON,
                "user" + locale + "@example.com",
                null,
                locale,
                null,
                null
            );

            assertNotNull(user, "Usuário com locale " + locale + " deve ser criado");
            assertEquals(locale, user.getPreferredLocale(), 
                "Locale " + locale + " deve ser preservado");
        }
    }

    @Test
    @DisplayName("Cenário 8: Remover locale (setar como null) deve funcionar")
    void scenario8_RemoveLocaleBySettingNull() {
        // Given - Usuário com locale
        User user = User.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UserTypeEnum.PERSON,
            "usuario@example.com",
            null,
            "pt-BR",
            null,
            null
        );

        // When - Remover locale
        user.setPreferredLocale(null);

        // Then - Locale deve ser null
        assertNull(user.getPreferredLocale());
    }

    @Test
    @DisplayName("Cenário 9: Locale com espaços extras deve ser normalizado")
    void scenario9_LocaleWithExtraSpacesShouldBeTrimmed() {
        // Given - Locale com espaços
        String localeWithSpaces = "  pt-BR  ";

        // When - Criar usuário
        User user = User.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UserTypeEnum.PERSON,
            "usuario@example.com",
            null,
            localeWithSpaces,
            null,
            null
        );

        // Then - Espaços devem ser removidos
        assertEquals("pt-BR", user.getPreferredLocale());
        assertFalse(user.getPreferredLocale().startsWith(" "));
        assertFalse(user.getPreferredLocale().endsWith(" "));
    }

    @Test
    @DisplayName("Cenário 10: Fluxo completo de alteração de locale múltiplas vezes")
    void scenario10_CompleteFlowMultipleLocaleChanges() {
        // Given - Criar usuário brasileiro
        User user = User.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UserTypeEnum.PERSON,
            "usuario@example.com",
            "+5511999999999",
            "pt-BR",
            "America/Sao_Paulo",
            null
        );

        // When/Then - Simular mudanças de idioma ao longo do tempo
        assertEquals("pt-BR", user.getPreferredLocale());

        // Mudou para inglês americano
        user.setPreferredLocale("en-US");
        assertEquals("en-US", user.getPreferredLocale());

        // Mudou para espanhol
        user.setPreferredLocale("es-ES");
        assertEquals("es-ES", user.getPreferredLocale());

        // Removeu preferência
        user.setPreferredLocale(null);
        assertNull(user.getPreferredLocale());

        // Voltou para português
        user.setPreferredLocale("pt-BR");
        assertEquals("pt-BR", user.getPreferredLocale());
    }
}

