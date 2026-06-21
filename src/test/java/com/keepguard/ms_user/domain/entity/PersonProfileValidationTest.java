package com.keepguard.ms_user.domain.entity;

import com.keepguard.lib_common.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PersonProfile - validações de cpf/data de nascimento")
class PersonProfileValidationTest {

    @Test
    @DisplayName("Deve permitir cpf nulo e data de nascimento nula")
    void shouldAllowNullCpfAndNullDob() {
        UUID userId = UUID.randomUUID();

        assertDoesNotThrow(() -> PersonProfile.create(
                userId,
                "John Doe",
                null,
                null
        ));
    }

    @Test
    @DisplayName("Deve limpar cpf mantendo apenas dígitos quando informado")
    void shouldCleanCpfDigitsWhenProvided() {
        UUID userId = UUID.randomUUID();
        var profile = PersonProfile.create(
                userId,
                "John Doe",
                "123.456.789-09", // CPF válido
                LocalDate.of(1990, 1, 1)
        );

        assertEquals("12345678909", profile.getCpf());
    }

    @Test
    @DisplayName("Deve rejeitar data de nascimento futura quando informada")
    void shouldRejectFutureDob() {
        UUID userId = UUID.randomUUID();
        LocalDate future = LocalDate.now().plusDays(1);

        var ex = assertThrows(ValidationException.class, () -> PersonProfile.create(
                userId,
                "John Doe",
                null,
                future
        ));
        assertTrue(ex.getMessage().contains("não pode ser futura"));
    }

    @Test
    @DisplayName("Deve aplicar idade mínima de 13 anos quando data de nascimento informada")
    void shouldApplyMinAge13WhenDobPresent() {
        UUID userId = UUID.randomUUID();
        LocalDate twelveYearsOld = LocalDate.now().minusYears(12);

        var ex = assertThrows(ValidationException.class, () -> PersonProfile.create(
                userId,
                "John Doe",
                null,
                twelveYearsOld
        ));
        assertTrue(ex.getMessage().contains("13 anos"));
    }

    @Test
    @DisplayName("Deve aceitar idade igual ou maior que 13 anos quando data presente")
    void shouldAcceptAgeEqualOrGreaterThan13() {
        UUID userId = UUID.randomUUID();
        LocalDate exactly13 = LocalDate.now().minusYears(13);

        assertDoesNotThrow(() -> PersonProfile.create(
                userId,
                "John Doe",
                null,
                exactly13
        ));
    }

}


