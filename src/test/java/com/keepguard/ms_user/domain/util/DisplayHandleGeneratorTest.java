package com.keepguard.ms_user.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DisplayHandleGeneratorTest {

    @Test
    @DisplayName("Usa só o primeiro nome")
    void shouldUseFirstNameOnly() {
        assertEquals("rafael", DisplayHandleGenerator.baseFrom("Rafael Nogueira Soares", "x@y.com"));
    }

    @Test
    @DisplayName("Remove acento do primeiro nome")
    void shouldStripAccents() {
        assertEquals("jose", DisplayHandleGenerator.baseFrom("José Silva", "x@y.com"));
    }

    @Test
    @DisplayName("Nome curto cai no local-part do e-mail")
    void shouldFallbackToEmailWhenNameTooShort() {
        assertEquals("usuario", DisplayHandleGenerator.baseFrom("Al", "usuario@gmail.com"));
    }

    @Test
    @DisplayName("Sem nome e e-mail inválido usa user")
    void shouldFallbackToUser() {
        assertEquals("user", DisplayHandleGenerator.baseFrom(null, "sem-arroba"));
    }

    @Test
    @DisplayName("Sufixo de unicidade")
    void shouldAppendUniquenessSuffix() {
        assertEquals("rafael", DisplayHandleGenerator.withUniquenessSuffix("rafael", 1));
        assertEquals("rafael2", DisplayHandleGenerator.withUniquenessSuffix("rafael", 2));
    }

    @Test
    @DisplayName("slug rejeita string menor que 3")
    void slugRejectsShort() {
        assertNull(DisplayHandleGenerator.slug("ab"));
    }
}
