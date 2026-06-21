package com.keepguard.ms_user.infrastructure.persistence;

import com.keepguard.ms_user.application.dto.address.AddressSearchCriteriaDTO;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.AddressJpaEntity;
import com.keepguard.ms_user.infrastructure.persistence.mapper.AddressJpaMapper;
import com.keepguard.ms_user.infrastructure.persistence.spring.AddressSpringRepository;
import com.keepguard.ms_user.test.builder.AddressTestBuilder;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AddressRepositoryAdapter
 * Testa operações de persistência com mocks JPA
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address Repository Adapter Tests")
class AddressRepositoryAdapterTest {

    @Mock
    private AddressSpringRepository springRepository;

    @Mock
    private AddressJpaMapper mapper;

    @InjectMocks
    private AddressRepositoryAdapter addressRepositoryAdapter;

    private Address address;
    private AddressJpaEntity addressJpaEntity;
    private AddressSearchCriteriaDTO searchCriteria;
    private Page<AddressJpaEntity> pageResult;
    private UUID addressId;
    private UUID userId;
    private AddressTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = AddressTestBuilder.builder();
        addressId = builder.buildDomain().getId();
        userId = builder.buildDomain().getUserId();

        address = builder.buildDomain();
        addressJpaEntity = builder.buildJpaEntity();
        
        searchCriteria = new AddressSearchCriteriaDTO(
            userId,
            "São Paulo",
            "SP",
            "01234567",
            address.getType(),
            true,
            true,
            0,
            10,
            List.of("createdAt"),
            "DESC"
        );

        pageResult = new PageImpl<>(
            List.of(addressJpaEntity),
            PageRequest.of(0, 10),
            1L
        );
    }

    // === TESTES DE SAVE ===

    @Test
    @DisplayName("Deve salvar endereço com sucesso")
    void shouldSaveAddressSuccessfully() {
        // Given
        when(mapper.toJpa(address)).thenReturn(addressJpaEntity);
        when(springRepository.save(addressJpaEntity)).thenReturn(addressJpaEntity);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        Address result = addressRepositoryAdapter.save(address);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(address.getId());
        assertThat(result.getStreet()).isEqualTo("Rua das Flores");
        assertThat(result.getCity()).isEqualTo("São Paulo");

        verify(mapper).toJpa(address);
        verify(springRepository).save(addressJpaEntity);
        verify(mapper).toDomain(addressJpaEntity);
    }

    @Test
    @DisplayName("Deve salvar endereço com dados diferentes")
    void shouldSaveAddressWithDifferentData() {
        // Given
        Address newAddress = Address.of(
            UUID.randomUUID(),
            userId,
            "Avenida Paulista",
            "1000",
            null,
            "Bela Vista",
            "São Paulo",
            "SP",
            "01310000",
            "Brasil",
            AddressTypeEnum.COMMERCIAL,
            false,
            true,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        AddressJpaEntity newEntity = AddressJpaEntity.builder()
            .id(newAddress.getId())
            .userId(userId)
            .street("Avenida Paulista")
            .number("1000")
            .neighborhood("Bela Vista")
            .city("São Paulo")
            .state("SP")
            .zipCode("01310000")
            .country("Brasil")
            .type(AddressTypeEnum.COMMERCIAL)
            .primary(false)
            .active(true)
            .createdAt(newAddress.getCreatedAt())
            .updatedAt(newAddress.getUpdatedAt())
            .build();

        when(mapper.toJpa(newAddress)).thenReturn(newEntity);
        when(springRepository.save(newEntity)).thenReturn(newEntity);
        when(mapper.toDomain(newEntity)).thenReturn(newAddress);

        // When
        Address result = addressRepositoryAdapter.save(newAddress);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(newAddress.getId());
        assertThat(result.getType()).isEqualTo(AddressTypeEnum.COMMERCIAL);
        assertThat(result.isPrimary()).isFalse();

        verify(mapper).toJpa(newAddress);
        verify(springRepository).save(newEntity);
        verify(mapper).toDomain(newEntity);
    }

    // === TESTES DE FIND BY ID ===

    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void shouldFindAddressByIdSuccessfully() {
        // Given
        when(springRepository.findById(addressId)).thenReturn(Optional.of(addressJpaEntity));
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        Optional<Address> result = addressRepositoryAdapter.findById(addressId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(address.getId());
        assertThat(result.get().getStreet()).isEqualTo("Rua das Flores");

        verify(springRepository).findById(addressId);
        verify(mapper).toDomain(addressJpaEntity);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando endereço não existe por ID")
    void shouldReturnEmptyOptionalWhenAddressNotFoundById() {
        // Given
        when(springRepository.findById(addressId)).thenReturn(Optional.empty());

        // When
        Optional<Address> result = addressRepositoryAdapter.findById(addressId);

        // Then
        assertThat(result).isEmpty();

        verify(springRepository).findById(addressId);
        verify(mapper, never()).toDomain(any(AddressJpaEntity.class));
    }

    // === TESTES DE FIND BY USER ID ===

    @Test
    @DisplayName("Deve buscar endereços por userId com sucesso")
    void shouldFindAddressesByUserIdSuccessfully() {
        // Given
        List<AddressJpaEntity> entities = List.of(addressJpaEntity);
        when(springRepository.findByUserId(userId)).thenReturn(entities);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        List<Address> result = addressRepositoryAdapter.findByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);

        verify(springRepository).findByUserId(userId);
        verify(mapper).toDomain(addressJpaEntity);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem endereços")
    void shouldReturnEmptyListWhenUserHasNoAddresses() {
        // Given
        when(springRepository.findByUserId(userId)).thenReturn(List.of());

        // When
        List<Address> result = addressRepositoryAdapter.findByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(springRepository).findByUserId(userId);
        verify(mapper, never()).toDomain(any(AddressJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar múltiplos endereços por userId")
    void shouldFindMultipleAddressesByUserId() {
        // Given
        AddressJpaEntity secondEntity = AddressJpaEntity.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .street("Avenida Brasil")
            .number("500")
            .neighborhood("Jardim")
            .city("São Paulo")
            .state("SP")
            .zipCode("02000000")
            .country("Brasil")
            .type(AddressTypeEnum.COMMERCIAL)
            .primary(false)
            .active(true)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

        Address secondAddress = Address.of(
            secondEntity.getId(),
            userId,
            "Avenida Brasil",
            "500",
            null,
            "Jardim",
            "São Paulo",
            "SP",
            "02000000",
            "Brasil",
            AddressTypeEnum.COMMERCIAL,
            false,
            true,
            secondEntity.getCreatedAt(),
            secondEntity.getUpdatedAt()
        );

        List<AddressJpaEntity> entities = List.of(addressJpaEntity, secondEntity);
        when(springRepository.findByUserId(userId)).thenReturn(entities);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);
        when(mapper.toDomain(secondEntity)).thenReturn(secondAddress);

        // When
        List<Address> result = addressRepositoryAdapter.findByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(1).getUserId()).isEqualTo(userId);

        verify(springRepository).findByUserId(userId);
        verify(mapper, times(2)).toDomain(any(AddressJpaEntity.class));
    }

    // === TESTES DE DELETE ===

    @Test
    @DisplayName("Deve deletar endereço por ID com sucesso")
    void shouldDeleteAddressByIdSuccessfully() {
        // When
        addressRepositoryAdapter.deleteById(addressId);

        // Then
        verify(springRepository).deleteById(addressId);
    }

    // === TESTES DE EXISTS ===

    @Test
    @DisplayName("Deve verificar se endereço existe por ID")
    void shouldCheckIfAddressExistsById() {
        // Given
        when(springRepository.existsById(addressId)).thenReturn(true);

        // When
        boolean result = addressRepositoryAdapter.existsById(addressId);

        // Then
        assertThat(result).isTrue();
        verify(springRepository).existsById(addressId);
    }

    @Test
    @DisplayName("Deve verificar se endereço não existe por ID")
    void shouldCheckIfAddressNotExistsById() {
        // Given
        when(springRepository.existsById(addressId)).thenReturn(false);

        // When
        boolean result = addressRepositoryAdapter.existsById(addressId);

        // Then
        assertThat(result).isFalse();
        verify(springRepository).existsById(addressId);
    }

    // === TESTES DE SEARCH ===

    @Test
    @DisplayName("Deve buscar endereços com critérios com sucesso")
    void shouldSearchAddressesWithCriteriaSuccessfully() {
        // Given
        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        PageResultDTO<Address> result = addressRepositoryAdapter.search(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper).toDomain(addressJpaEntity);
    }

    @Test
    @DisplayName("Deve buscar endereços com critérios vazios")
    void shouldSearchAddressesWithEmptyCriteria() {
        // Given
        AddressSearchCriteriaDTO emptyCriteria = new AddressSearchCriteriaDTO(
            userId,
            null,
            null,
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
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        PageResultDTO<Address> result = addressRepositoryAdapter.search(emptyCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar endereços com paginação customizada")
    void shouldSearchAddressesWithCustomPagination() {
        // Given
        AddressSearchCriteriaDTO customCriteria = new AddressSearchCriteriaDTO(
            userId,
            "São Paulo",
            "SP",
            null,
            AddressTypeEnum.RESIDENTIAL,
            null,
            true,
            1,
            5,
            List.of("city", "street"),
            "ASC"
        );

        Page<AddressJpaEntity> customPageResult = new PageImpl<>(
            List.of(addressJpaEntity),
            PageRequest.of(1, 5),
            10L
        );

        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(customPageResult);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        PageResultDTO<Address> result = addressRepositoryAdapter.search(customCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(10L);
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(5);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar endereços por tipo específico")
    void shouldSearchAddressesBySpecificType() {
        // Given
        AddressSearchCriteriaDTO typeCriteria = new AddressSearchCriteriaDTO(
            userId,
            null,
            null,
            null,
            AddressTypeEnum.COMMERCIAL,
            null,
            null,
            0,
            20,
            null,
            null
        );

        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        PageResultDTO<Address> result = addressRepositoryAdapter.search(typeCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar endereços primários")
    void shouldSearchPrimaryAddresses() {
        // Given
        AddressSearchCriteriaDTO primaryCriteria = new AddressSearchCriteriaDTO(
            userId,
            null,
            null,
            null,
            null,
            true,
            null,
            0,
            20,
            null,
            null
        );

        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);
        when(mapper.toDomain(addressJpaEntity)).thenReturn(address);

        // When
        PageResultDTO<Address> result = addressRepositoryAdapter.search(primaryCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum endereço encontrado")
    void shouldReturnEmptyPageWhenNoAddressesFound() {
        // Given
        Page<AddressJpaEntity> emptyPage = new PageImpl<>(
            List.of(),
            PageRequest.of(0, 10),
            0L
        );

        when(springRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        // When
        PageResultDTO<Address> result = addressRepositoryAdapter.search(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);

        verify(springRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper, never()).toDomain(any(AddressJpaEntity.class));
    }
}

