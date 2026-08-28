package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.keepguard.ms_user.test.builder.AddressTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para AddressApplicationMapper
 * Testa conversões entre diferentes tipos de objetos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address Application Mapper Tests")
class AddressApplicationMapperTest {

    @InjectMocks
    private AddressApplicationMapper mapper;

    private Address address;
    private AddressTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = AddressTestBuilder.builder();
        address = builder.buildDomain();
    }

    // === TESTES DE CONVERSÃO TO DOMAIN ===

    @Test
    @DisplayName("Deve converter AddressCreateCommandDTO para Address domain")
    void shouldConvertCreateCommandToDomain() {
        // Given
        var command = builder.buildCreateCommand();

        // When
        var result = mapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(command.userId(), result.getUserId());
        assertEquals(command.street(), result.getStreet());
        assertEquals(command.number(), result.getNumber());
        assertEquals(command.city(), result.getCity());
        assertEquals(command.type(), result.getType());
    }

    @Test
    @DisplayName("Deve converter AddressCreateCommandDTO para Address domain com valores padrão")
    void shouldConvertCreateCommandToDomainWithDefaults() {
        // Given
        var command = AddressTestBuilder.builder()
                .withCountry(null)
                .withPrimary(null)
                .withActive(null)
                .buildCreateCommand();

        // When
        var result = mapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertEquals("Brasil", result.getCountry());
        assertFalse(result.isPrimary());
        assertTrue(result.isActive());
    }

    // === TESTES DE CONVERSÃO TO DETAILS VIEW ===

    @Test
    @DisplayName("Deve converter Address domain para AddressDetailsViewDTO")
    void shouldConvertDomainToDetailsView() {
        // When
        var result = mapper.toDetailsView(address);

        // Then
        assertNotNull(result);
        assertEquals(address.getId(), result.id());
        assertEquals(address.getUserId(), result.userId());
        assertEquals(address.getStreet(), result.street());
        assertEquals(address.getCity(), result.city());
        assertEquals(address.getType(), result.type());
        assertNotNull(result.fullAddress());
    }

    // === TESTES DE CONVERSÃO TO SEARCH VIEW ===

    @Test
    @DisplayName("Deve converter Address domain para AddressSearchViewDTO")
    void shouldConvertDomainToSearchView() {
        // When
        var result = mapper.toSearchView(address);

        // Then
        assertNotNull(result);
        assertEquals(address.getId(), result.id());
        assertEquals(address.getUserId(), result.userId());
        assertEquals(address.getStreet(), result.street());
        assertEquals(address.getCity(), result.city());
    }

    // === TESTES DE CONVERSÃO TO SEARCH CRITERIA ===

    @Test
    @DisplayName("Deve converter AddressSearchQueryDTO para AddressSearchCriteriaDTO")
    void shouldConvertSearchQueryToSearchCriteria() {
        // Given
        var query = builder.buildSearchQuery();

        // When
        var result = mapper.toSearchCriteria(query);

        // Then
        assertNotNull(result);
        assertEquals(query.userId(), result.userId());
        assertEquals(query.city(), result.city());
        assertEquals(query.state(), result.state());
        assertEquals(query.type(), result.type());
    }

    // === TESTES DE UPDATE DOMAIN ===

    @Test
    @DisplayName("Deve atualizar Address domain com AddressUpdateCommandDTO")
    void shouldUpdateDomain() {
        // Given
        var command = AddressTestBuilder.builder()
                .withStreet("Nova Rua")
                .withNumber("456")
                .withCity("Rio de Janeiro")
                .withState("RJ")
                .withType(AddressTypeEnum.COMMERCIAL)
                .withPrimary(false)
                .withActive(false)
                .buildUpdateCommand();

        // When
        mapper.updateDomain(address, command);

        // Then
        assertEquals("Nova Rua", address.getStreet());
        assertEquals("456", address.getNumber());
        assertEquals("Rio de Janeiro", address.getCity());
        assertEquals("RJ", address.getState());
        assertEquals(AddressTypeEnum.COMMERCIAL, address.getType());
        assertFalse(address.isPrimary());
        assertFalse(address.isActive());
    }

    @Test
    @DisplayName("Deve atualizar Address domain apenas com campos presentes")
    void shouldUpdateDomainOnlyPresentFields() {
        // Given
        String originalStreet = address.getStreet();
        String originalNumber = address.getNumber();
        
        var command = new com.keepguard.ms_user.application.dto.address.AddressUpdateCommandDTO(
            address.getId(),
            builder.buildCreateCommand().companyId(),
            Optional.empty(),
            Optional.empty(),
            Optional.of("Novo Complemento"),
            Optional.empty(),
            Optional.of("Curitiba"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );

        // When
        mapper.updateDomain(address, command);

        // Then
        assertEquals(originalStreet, address.getStreet());
        assertEquals(originalNumber, address.getNumber());
        assertEquals("Novo Complemento", address.getComplement());
        assertEquals("Curitiba", address.getCity());
    }
}
