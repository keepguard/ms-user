package com.keepguard.ms_user.application.service.contact;

import com.keepguard.ms_user.application.mapper.ContactApplicationMapper;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.ContactRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.test.builder.ContactTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ContactCommandService
 * Segue o padrão profissional usado no ms-user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Command Service Tests")
class ContactCommandServiceTest {

    @InjectMocks
    private ContactCommandService contactCommandService;

    @Mock
    private ContactRepositoryPort contactRepositoryPort;

    @Mock
    private ContactApplicationMapper contactApplicationMapper;

    @Mock
    private MetricsPort metricsPort;

    private ContactTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = ContactTestBuilder.builder();
    }

    // === TESTES DE CREATE ===

    @Test
    @DisplayName("Deve criar contato com sucesso")
    void shouldCreateContactSuccessfully() {
        // Given
        var command = builder.buildCreateCommand();
        var contact = builder.buildDomain();
        var contactDetailsView = builder.buildDetailsView();

        when(contactApplicationMapper.toDomain(command)).thenReturn(contact);
        when(contactRepositoryPort.save(contact)).thenReturn(contact);
        when(contactApplicationMapper.toDetailsView(contact)).thenReturn(contactDetailsView);

        // When
        var result = contactCommandService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(contact.getId(), result.id());

        verify(contactApplicationMapper).toDomain(command);
        verify(contactRepositoryPort).save(contact);
        verify(contactApplicationMapper).toDetailsView(contact);
        verify(metricsPort).incrementCounter(eq("contact_created_total"), anyMap());
    }

    // === TESTES DE UPDATE ===

    @Test
    @DisplayName("Deve atualizar contato com sucesso")
    void shouldUpdateContactSuccessfully() {
        // Given
        var command = builder.buildUpdateCommand();
        var contact = builder.buildDomain();
        var contactDetailsView = builder.buildDetailsView();

        when(contactRepositoryPort.findById(command.id())).thenReturn(Optional.of(contact));
        when(contactRepositoryPort.save(contact)).thenReturn(contact);
        when(contactApplicationMapper.toDetailsView(contact)).thenReturn(contactDetailsView);

        // When
        var result = contactCommandService.update(command);

        // Then
        assertNotNull(result);
        assertEquals(command.id(), result.id());

        verify(contactRepositoryPort).findById(command.id());
        verify(contactApplicationMapper).updateDomain(contact, command);
        verify(contactRepositoryPort).save(contact);
        verify(contactApplicationMapper).toDetailsView(contact);
        verify(metricsPort).incrementCounter(eq("contact_updated_total"), anyMap());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando contato não existe para update")
    void shouldThrowNotFoundExceptionWhenContactDoesNotExistForUpdate() {
        // Given
        var command = builder.buildUpdateCommand();
        when(contactRepositoryPort.findById(command.id())).thenReturn(Optional.empty());

        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> contactCommandService.update(command));
        assertEquals("Contato não encontrado: " + command.id(), exception.getMessage());
        assertEquals("CONTACT_NOT_FOUND", exception.getErrorCode());

        verify(contactRepositoryPort).findById(command.id());
        verify(contactApplicationMapper, never()).updateDomain(any(), any());
        verify(contactRepositoryPort, never()).save(any());
        verify(metricsPort).incrementCounter(eq("contact_business_errors_total"), anyMap());
    }

    // === TESTES DE DELETE ===

    @Test
    @DisplayName("Deve deletar contato com sucesso")
    void shouldDeleteContactSuccessfully() {
        // Given
        var command = builder.buildDeleteCommand();
        when(contactRepositoryPort.existsById(command.id())).thenReturn(true);

        // When
        contactCommandService.delete(command);

        // Then
        verify(contactRepositoryPort).existsById(command.id());
        verify(contactRepositoryPort).deleteById(command.id());
        verify(metricsPort).incrementCounter(eq("contact_deleted_total"), anyMap());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando contato não existe para delete")
    void shouldThrowNotFoundExceptionWhenContactDoesNotExistForDelete() {
        // Given
        var command = builder.buildDeleteCommand();
        when(contactRepositoryPort.existsById(command.id())).thenReturn(false);

        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> contactCommandService.delete(command));
        assertEquals("Contato não encontrado: " + command.id(), exception.getMessage());
        assertEquals("CONTACT_NOT_FOUND", exception.getErrorCode());

        verify(contactRepositoryPort).existsById(command.id());
        verify(contactRepositoryPort, never()).deleteById(any());
        verify(metricsPort).incrementCounter(eq("contact_business_errors_total"), anyMap());
    }
}
