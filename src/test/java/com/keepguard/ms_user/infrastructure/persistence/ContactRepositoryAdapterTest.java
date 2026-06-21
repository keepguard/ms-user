package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.dto.contact.ContactSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.domain.entity.Contact;
import com.keepguard.ms_user.infrastructure.persistence.entity.ContactJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.ContactJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.ContactSpringRepository;
import com.keepguard.ms_user.test.builder.ContactTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ContactRepositoryAdapter
 * Testa operações de persistência com mocks JPA
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Contact Repository Adapter Tests")
class ContactRepositoryAdapterTest {

    @Mock
    private ContactSpringRepository springRepository;

    @Mock
    private ContactJpaMapper mapper;

    @InjectMocks
    private ContactRepositoryAdapter contactRepositoryAdapter;

    private Contact contact;
    private ContactJpaEntity contactJpaEntity;
    private ContactSearchCriteriaDTO searchCriteria;
    private Page<ContactJpaEntity> pageResult;
    private UUID contactId;
    private UUID userId;
    private ContactTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = ContactTestBuilder.builder();
        contactId = builder.buildDomain().getId();
        userId = builder.buildDomain().getUserId();

        contact = builder.buildDomain();
        contactJpaEntity = builder.buildJpaEntity();

        searchCriteria = new ContactSearchCriteriaDTO(
            userId,
            "999",
            contact.getType(),
            true,
            true,
            0,
            10,
            List.of("createdAt"),
            "DESC"
        );

        pageResult = new PageImpl<>(
            List.of(contactJpaEntity),
            PageRequest.of(0, 10),
            1L
        );
    }

    // === TESTES DE SAVE ===

    @Test
    @DisplayName("Deve salvar contato com sucesso")
    void shouldSaveContactSuccessfully() {
        // Given
        when(mapper.toJpa(contact)).thenReturn(contactJpaEntity);
        when(springRepository.save(contactJpaEntity)).thenReturn(contactJpaEntity);
        when(mapper.toDomain(contactJpaEntity)).thenReturn(contact);

        // When
        Contact result = contactRepositoryAdapter.save(contact);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(contact.getId());
        assertThat(result.getValue()).isEqualTo("11999999999");

        verify(mapper).toJpa(contact);
        verify(springRepository).save(contactJpaEntity);
        verify(mapper).toDomain(contactJpaEntity);
    }

    // === TESTES DE FIND BY ID ===

    @Test
    @DisplayName("Deve buscar contato por ID com sucesso")
    void shouldFindContactByIdSuccessfully() {
        // Given
        when(springRepository.findById(contactId)).thenReturn(Optional.of(contactJpaEntity));
        when(mapper.toDomain(contactJpaEntity)).thenReturn(contact);

        // When
        Optional<Contact> result = contactRepositoryAdapter.findById(contactId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(contact.getId());

        verify(springRepository).findById(contactId);
        verify(mapper).toDomain(contactJpaEntity);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando contato não existe por ID")
    void shouldReturnEmptyOptionalWhenContactNotFoundById() {
        // Given
        when(springRepository.findById(contactId)).thenReturn(Optional.empty());

        // When
        Optional<Contact> result = contactRepositoryAdapter.findById(contactId);

        // Then
        assertThat(result).isEmpty();

        verify(springRepository).findById(contactId);
        verify(mapper, never()).toDomain(any(ContactJpaEntity.class));
    }

    // === TESTES DE FIND BY USER ID ===

    @Test
    @DisplayName("Deve buscar contatos por userId com sucesso")
    void shouldFindContactsByUserIdSuccessfully() {
        // Given
        List<ContactJpaEntity> entities = List.of(contactJpaEntity);
        when(springRepository.findByUserId(userId)).thenReturn(entities);
        when(mapper.toDomain(contactJpaEntity)).thenReturn(contact);

        // When
        List<Contact> result = contactRepositoryAdapter.findByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);

        verify(springRepository).findByUserId(userId);
        verify(mapper).toDomain(contactJpaEntity);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem contatos")
    void shouldReturnEmptyListWhenUserHasNoContacts() {
        // Given
        when(springRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        List<Contact> result = contactRepositoryAdapter.findByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(springRepository).findByUserId(userId);
        verify(mapper, never()).toDomain(any(ContactJpaEntity.class));
    }

    // === TESTES DE DELETE ===

    @Test
    @DisplayName("Deve deletar contato por ID com sucesso")
    void shouldDeleteContactByIdSuccessfully() {
        // When
        contactRepositoryAdapter.deleteById(contactId);

        // Then
        verify(springRepository).deleteById(contactId);
    }

    // === TESTES DE EXISTS ===

    @Test
    @DisplayName("Deve verificar se contato existe por ID")
    void shouldCheckIfContactExistsById() {
        // Given
        when(springRepository.existsById(contactId)).thenReturn(true);

        // When
        boolean result = contactRepositoryAdapter.existsById(contactId);

        // Then
        assertThat(result).isTrue();
        verify(springRepository).existsById(contactId);
    }

    @Test
    @DisplayName("Deve verificar se contato não existe por ID")
    void shouldCheckIfContactNotExistsById() {
        // Given
        when(springRepository.existsById(contactId)).thenReturn(false);

        // When
        boolean result = contactRepositoryAdapter.existsById(contactId);

        // Then
        assertThat(result).isFalse();
        verify(springRepository).existsById(contactId);
    }

    // === TESTES DE SEARCH ===

    @Test
    @DisplayName("Deve buscar contatos com critérios com sucesso")
    void shouldSearchContactsWithCriteriaSuccessfully() {
        // Given
        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(contactJpaEntity)).thenReturn(contact);

        // When
        PageResultDTO<Contact> result = contactRepositoryAdapter.search(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper).toDomain(contactJpaEntity);
    }

    @Test
    @DisplayName("Deve buscar contatos com critérios vazios")
    void shouldSearchContactsWithEmptyCriteria() {
        // Given
        ContactSearchCriteriaDTO emptyCriteria = new ContactSearchCriteriaDTO(
            userId,
            null,
            null,
            null,
            null,
            0,
            20,
            null,
            null
        );
        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(contactJpaEntity)).thenReturn(contact);

        // When
        PageResultDTO<Contact> result = contactRepositoryAdapter.search(emptyCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum contato encontrado")
    void shouldReturnEmptyPageWhenNoContactsFound() {
        // Given
        Page<ContactJpaEntity> emptyPage = new PageImpl<>(
            List.of(),
            PageRequest.of(0, 10),
            0L
        );

        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        // When
        PageResultDTO<Contact> result = contactRepositoryAdapter.search(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper, never()).toDomain(any(ContactJpaEntity.class));
    }
}

