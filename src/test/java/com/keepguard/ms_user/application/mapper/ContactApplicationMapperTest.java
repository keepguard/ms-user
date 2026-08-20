package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.domain.entity.Contact;
import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.keepguard.ms_user.test.builder.ContactTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ContactApplicationMapper
 * Testa conversões entre diferentes tipos de objetos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Application Mapper Tests")
class ContactApplicationMapperTest {

    @InjectMocks
    private ContactApplicationMapper mapper;

    private Contact contact;
    private ContactTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = ContactTestBuilder.builder();
        contact = builder.buildDomain();
    }

    // === TESTES DE CONVERSÃO TO DOMAIN ===

    @Test
    @DisplayName("Deve converter ContactCreateCommandDTO para Contact domain")
    void shouldConvertCreateCommandToDomain() {
        // Given
        var command = builder.buildCreateCommand();

        // When
        var result = mapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(command.userId(), result.getUserId());
        assertEquals(command.value(), result.getValue());
        assertEquals(command.type(), result.getType());
    }

    @Test
    @DisplayName("Deve converter ContactCreateCommandDTO para Contact domain com valores padrão")
    void shouldConvertCreateCommandToDomainWithDefaults() {
        // Given
        var command = ContactTestBuilder.builder()
                .withPrimary(null)
                .withActive(null)
                .buildCreateCommand();

        // When
        var result = mapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertFalse(result.isPrimary());
        assertTrue(result.isActive());
    }

    // === TESTES DE CONVERSÃO TO DETAILS VIEW ===

    @Test
    @DisplayName("Deve converter Contact domain para ContactDetailsViewDTO")
    void shouldConvertDomainToDetailsView() {
        // When
        var result = mapper.toDetailsView(contact);

        // Then
        assertNotNull(result);
        assertEquals(contact.getId(), result.id());
        assertEquals(contact.getUserId(), result.userId());
        assertEquals(contact.getValue(), result.value());
        assertEquals(contact.getType(), result.type());
    }

    // === TESTES DE CONVERSÃO TO SEARCH VIEW ===

    @Test
    @DisplayName("Deve converter Contact domain para ContactSearchViewDTO")
    void shouldConvertDomainToSearchView() {
        // When
        var result = mapper.toSearchView(contact);

        // Then
        assertNotNull(result);
        assertEquals(contact.getId(), result.id());
        assertEquals(contact.getUserId(), result.userId());
        assertEquals(contact.getValue(), result.value());
        assertEquals(contact.getType(), result.type());
    }

    // === TESTES DE CONVERSÃO TO SEARCH CRITERIA ===

    @Test
    @DisplayName("Deve converter ContactSearchQueryDTO para ContactSearchCriteriaDTO")
    void shouldConvertSearchQueryToSearchCriteria() {
        // Given
        var query = builder.buildSearchQuery();

        // When
        var result = mapper.toSearchCriteria(query);

        // Then
        assertNotNull(result);
        assertEquals(query.userId(), result.userId());
        assertEquals(query.value(), result.value());
        assertEquals(query.type(), result.type());
    }

    // === TESTES DE UPDATE DOMAIN ===

    @Test
    @DisplayName("Deve atualizar Contact domain com ContactUpdateCommandDTO")
    void shouldUpdateDomain() {
        // Given
        var command = ContactTestBuilder.builder()
                .withValue("11988888888")
                .withType(ContactTypeEnum.WHATSAPP)
                .withDescription("WhatsApp pessoal")
                .withPrimary(false)
                .withActive(false)
                .buildUpdateCommand();

        // When
        mapper.updateDomain(contact, command);

        // Then
        assertEquals("11988888888", contact.getValue());
        assertEquals(ContactTypeEnum.WHATSAPP, contact.getType());
        assertEquals("WhatsApp pessoal", contact.getDescription());
        assertFalse(contact.isPrimary());
        assertFalse(contact.isActive());
    }

    @Test
    @DisplayName("Deve atualizar Contact domain apenas com campos presentes")
    void shouldUpdateDomainOnlyPresentFields() {
        // Given
        String originalValue = contact.getValue();
        ContactTypeEnum originalType = contact.getType();
        
        var command = new com.keepguard.ms_user.application.dto.contact.ContactUpdateCommandDTO(
            contact.getId(),
            builder.buildCreateCommand().tenantId(),
            Optional.empty(),
            Optional.empty(),
            Optional.of("Nova descrição"),
            Optional.empty(),
            Optional.empty()
        );

        // When
        mapper.updateDomain(contact, command);

        // Then
        assertEquals(originalValue, contact.getValue());
        assertEquals(originalType, contact.getType());
        assertEquals("Nova descrição", contact.getDescription());
    }
}
