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
 * Testes unitários para {@link ValidPhoneValidator}.
 * Garante 100% de cobertura de código.
 */
@DisplayName("ValidPhoneValidator - Testes")
class ValidPhoneValidatorTest {

    private ValidPhoneValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ValidPhone annotation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ValidPhoneValidator();
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
    @ValueSource(strings = {
        "+5511999999999",
        "+12125551234",  // Número válido americano (Nova York)
        "+442071234567",
        "+33123456789",
        "+4930123456",
        "+81312345678"
    })
    @DisplayName("Deve validar telefones válidos")
    void shouldValidateValidPhones(String phone) {
        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar telefone brasileiro válido")
    void shouldValidateBrazilianPhone() {
        // Given
        String phone = "+5511999999999";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar telefone americano válido")
    void shouldValidateAmericanPhone() {
        // Given
        String phone = "+12125551234"; // Número válido americano (Nova York)

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar telefone com espaços extras")
    void shouldValidatePhoneWithExtraSpaces() {
        // Given
        String phone = "  +5511999999999  ";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar telefone com formatação")
    void shouldValidateFormattedPhone() {
        // Given
        String phone = "+55 (11) 99999-9999";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "+55119999", "abc123", "999999999", "+9999"})
    @DisplayName("Deve invalidar telefones inválidos")
    void shouldInvalidateInvalidPhones(String phone) {
        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar telefone muito curto")
    void shouldInvalidateTooShortPhone() {
        // Given
        String phone = "+5511";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar telefone sem código de país")
    void shouldInvalidatePhoneWithoutCountryCode() {
        // Given
        String phone = "11999999999";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar telefone com código de país inválido")
    void shouldInvalidatePhoneWithInvalidCountryCode() {
        // Given
        String phone = "+99999999999";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar texto não numérico")
    void shouldInvalidateNonNumericText() {
        // Given
        String phone = "abcdefghijk";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve validar telefones de múltiplos países")
    void shouldValidatePhonesFromMultipleCountries() {
        // Given
        String[] validPhones = {
            "+5511999999999",  // Brasil
            "+12125551234",    // EUA (Nova York)
            "+442071234567",   // Reino Unido
            "+33123456789",    // França
            "+4930123456",     // Alemanha
            "+81312345678",    // Japão
            "+8613812345678",  // China (número válido)
            "+917012345678",   // Índia
            "+61412345678"     // Austrália (número válido)
        };

        // When & Then
        for (String phone : validPhones) {
            assertTrue(
                validator.isValid(phone, context),
                "Phone " + phone + " should be valid"
            );
        }
    }

    @Test
    @DisplayName("Deve chamar initialize sem erros")
    void shouldCallInitializeWithoutErrors() {
        // When & Then
        assertDoesNotThrow(() -> validator.initialize(annotation));
    }

    @Test
    @DisplayName("Deve validar com diferentes formatos brasileiros")
    void shouldValidateWithDifferentBrazilianFormats() {
        // Given
        String[] phones = {
            "+5511999999999",
            "+55 11 99999-9999",
            "+55 (11) 99999-9999",
            "+55 11 9 9999-9999"
        };

        // When & Then
        for (String phone : phones) {
            assertTrue(
                validator.isValid(phone, context),
                "Phone " + phone + " should be valid"
            );
        }
    }

    @Test
    @DisplayName("Deve invalidar número parcial")
    void shouldInvalidatePartialNumber() {
        // Given
        String phone = "+55119999";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar apenas símbolos")
    void shouldInvalidateOnlySymbols() {
        // Given
        String phone = "+()- ";

        // When
        boolean isValid = validator.isValid(phone, context);

        // Then
        assertFalse(isValid);
    }
}

