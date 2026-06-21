package com.keepguard.ms_user.application.service.contact;

import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.mapper.ContactApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.ContactRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.domain.entity.Contact;
import com.keepguard.ms_user.test.builder.ContactTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ContactQueryService
 * Segue o padrão profissional usado no ms-user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Query Service Tests")
class ContactQueryServiceTest {

    @InjectMocks
    private ContactQueryService contactQueryService;

    @Mock
    private ContactRepositoryPort contactRepositoryPort;

    @Mock
    private ContactApplicationMapper contactApplicationMapper;

    private ContactTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = ContactTestBuilder.builder();
    }

    // === TESTES DE GET BY ID ===

    @Test
    @DisplayName("Deve buscar contato por ID com sucesso")
    void shouldGetContactByIdSuccessfully() {
        // Given
        var query = builder.buildGetByIdQuery();
        var contact = builder.buildDomain();
        var contactDetailsView = builder.buildDetailsView();
        
        when(contactRepositoryPort.findById(query.id())).thenReturn(Optional.of(contact));
        when(contactApplicationMapper.toDetailsView(contact)).thenReturn(contactDetailsView);

        // When
        var result = contactQueryService.getById(query);

        // Then
        assertNotNull(result);
        assertEquals(query.id(), result.id());

        verify(contactRepositoryPort).findById(query.id());
        verify(contactApplicationMapper).toDetailsView(contact);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando contato não existe por ID")
    void shouldThrowNotFoundExceptionWhenContactDoesNotExistById() {
        // Given
        var query = builder.buildGetByIdQuery();
        when(contactRepositoryPort.findById(query.id())).thenReturn(Optional.empty());

        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> contactQueryService.getById(query));
        assertEquals("Contato não encontrado: " + query.id(), exception.getMessage());
        assertEquals("CONTACT_NOT_FOUND", exception.getErrorCode());

        verify(contactRepositoryPort).findById(query.id());
        verify(contactApplicationMapper, never()).toDetailsView(any());
    }

    // === TESTES DE GET BY USER ID ===

    @Test
    @DisplayName("Deve buscar contatos por userId com sucesso")
    void shouldGetContactsByUserIdSuccessfully() {
        // Given
        var query = builder.buildGetByUserIdQuery();
        var contact = builder.buildDomain();
        var contactDetailsView = builder.buildDetailsView();
        
        when(contactRepositoryPort.findByUserId(query.userId())).thenReturn(List.of(contact));
        when(contactApplicationMapper.toDetailsView(contact)).thenReturn(contactDetailsView);

        // When
        var result = contactQueryService.getByUserId(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(contactRepositoryPort).findByUserId(query.userId());
        verify(contactApplicationMapper).toDetailsView(contact);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem contatos")
    void shouldReturnEmptyListWhenUserHasNoContacts() {
        // Given
        var query = builder.buildGetByUserIdQuery();
        when(contactRepositoryPort.findByUserId(query.userId())).thenReturn(List.of());

        // When
        var result = contactQueryService.getByUserId(query);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(contactRepositoryPort).findByUserId(query.userId());
        verify(contactApplicationMapper, never()).toDetailsView(any());
    }

    // === TESTES DE SEARCH ===

    @Test
    @DisplayName("Deve buscar contatos com critérios com sucesso")
    void shouldSearchContactsWithCriteriaSuccessfully() {
        // Given
        var query = builder.buildSearchQuery();
        var contact = builder.buildDomain();
        var contactSearchView = builder.buildSearchView();
        var criteria = new com.keepguard.ms_user.application.dto.contact.ContactSearchCriteriaDTO(
            query.userId(),
            query.value(),
            query.type(),
            query.primary(),
            query.active(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );

        var pageResult = new PageResultDTO<>(List.of(contact), 1L, 0, 20);

        when(contactApplicationMapper.toSearchCriteria(query)).thenReturn(criteria);
        when(contactRepositoryPort.search(criteria)).thenReturn(pageResult);
        when(contactApplicationMapper.toSearchView(contact)).thenReturn(contactSearchView);

        // When
        var result = contactQueryService.search(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());

        verify(contactApplicationMapper).toSearchCriteria(query);
        verify(contactRepositoryPort).search(criteria);
        verify(contactApplicationMapper).toSearchView(contact);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum contato encontrado")
    void shouldReturnEmptyPageWhenNoContactsFound() {
        // Given
        var query = builder.buildSearchQuery();
        var criteria = new com.keepguard.ms_user.application.dto.contact.ContactSearchCriteriaDTO(
            query.userId(),
            query.value(),
            query.type(),
            query.primary(),
            query.active(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );
        
        var emptyPageResult = new PageResultDTO<Contact>(List.of(), 0L, 0, 20);

        when(contactApplicationMapper.toSearchCriteria(query)).thenReturn(criteria);
        when(contactRepositoryPort.search(criteria)).thenReturn(emptyPageResult);

        // When
        var result = contactQueryService.search(query);

        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());

        verify(contactApplicationMapper).toSearchCriteria(query);
        verify(contactRepositoryPort).search(criteria);
        verify(contactApplicationMapper, never()).toSearchView(any());
    }
}
