package com.keepguard.ms_user.application.service.contact;

import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.test.builder.ContactTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ContactUseCaseService
 * Segue o padrão profissional usado no ms-user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Contact UseCase Service Tests")
class ContactUseCaseServiceTest {

    @InjectMocks
    private ContactUseCaseService contactUseCaseService;

    @Mock
    private ContactCommandService contactCommandService;

    @Mock
    private ContactQueryService contactQueryService;

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
        var contactDetailsView = builder.buildDetailsView();

        when(contactCommandService.create(command)).thenReturn(contactDetailsView);

        // When
        var result = contactUseCaseService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(contactDetailsView.id(), result.id());

        verify(contactCommandService).create(command);
    }

    @Test
    @DisplayName("Deve propagar exceção ao criar contato")
    void shouldPropagateExceptionWhenCreatingContact() {
        // Given
        var command = builder.buildCreateCommand();
        RuntimeException exception = new RuntimeException("Erro ao criar contato");
        when(contactCommandService.create(command)).thenThrow(exception);

        // When & Then
        assertThrows(RuntimeException.class, () -> contactUseCaseService.create(command));
        verify(contactCommandService).create(command);
    }

    // === TESTES DE UPDATE ===

    @Test
    @DisplayName("Deve atualizar contato com sucesso")
    void shouldUpdateContactSuccessfully() {
        // Given
        var command = builder.buildUpdateCommand();
        var contactDetailsView = builder.buildDetailsView();

        when(contactCommandService.update(command)).thenReturn(contactDetailsView);

        // When
        var result = contactUseCaseService.update(command);

        // Then
        assertNotNull(result);
        assertEquals(command.id(), result.id());

        verify(contactCommandService).update(command);
    }

    @Test
    @DisplayName("Deve propagar exceção ao atualizar contato")
    void shouldPropagateExceptionWhenUpdatingContact() {
        // Given
        var command = builder.buildUpdateCommand();
        RuntimeException exception = new RuntimeException("Erro ao atualizar contato");
        when(contactCommandService.update(command)).thenThrow(exception);

        // When & Then
        assertThrows(RuntimeException.class, () -> contactUseCaseService.update(command));
        verify(contactCommandService).update(command);
    }

    // === TESTES DE DELETE ===

    @Test
    @DisplayName("Deve deletar contato com sucesso")
    void shouldDeleteContactSuccessfully() {
        // Given
        var command = builder.buildDeleteCommand();
        doNothing().when(contactCommandService).delete(command);

        // When
        contactUseCaseService.delete(command);

        // Then
        verify(contactCommandService).delete(command);
    }

    @Test
    @DisplayName("Deve propagar exceção ao deletar contato")
    void shouldPropagateExceptionWhenDeletingContact() {
        // Given
        var command = builder.buildDeleteCommand();
        RuntimeException exception = new RuntimeException("Erro ao deletar contato");
        doThrow(exception).when(contactCommandService).delete(command);

        // When & Then
        assertThrows(RuntimeException.class, () -> contactUseCaseService.delete(command));
        verify(contactCommandService).delete(command);
    }

    // === TESTES DE GET BY ID ===

    @Test
    @DisplayName("Deve buscar contato por ID com sucesso")
    void shouldGetContactByIdSuccessfully() {
        // Given
        var query = builder.buildGetByIdQuery();
        var contactDetailsView = builder.buildDetailsView();

        when(contactQueryService.getById(query)).thenReturn(contactDetailsView);

        // When
        var result = contactUseCaseService.getById(query);

        // Then
        assertNotNull(result);
        assertEquals(contactDetailsView.id(), result.id());

        verify(contactQueryService).getById(query);
    }

    // === TESTES DE GET BY USER ID ===

    @Test
    @DisplayName("Deve buscar contatos por userId com sucesso")
    void shouldGetContactsByUserIdSuccessfully() {
        // Given
        var query = builder.buildGetByUserIdQuery();
        var contactDetailsView = builder.buildDetailsView();

        when(contactQueryService.getByUserId(query)).thenReturn(List.of(contactDetailsView));

        // When
        var result = contactUseCaseService.getByUserId(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(contactQueryService).getByUserId(query);
    }

    // === TESTES DE SEARCH ===

    @Test
    @DisplayName("Deve buscar contatos com critérios com sucesso")
    void shouldSearchContactsWithCriteriaSuccessfully() {
        // Given
        var query = builder.buildSearchQuery();
        var contactSearchView = builder.buildSearchView();
        var pageResult = new PageResultDTO<>(List.of(contactSearchView), 1L, 0, 20);

        when(contactQueryService.search(query)).thenReturn(pageResult);

        // When
        var result = contactUseCaseService.search(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());

        verify(contactQueryService).search(query);
    }
}
