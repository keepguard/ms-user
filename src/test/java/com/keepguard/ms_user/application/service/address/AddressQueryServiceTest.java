package com.keepguard.ms_user.application.service.address;

import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.mapper.AddressApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.AddressRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.test.builder.AddressTestBuilder;
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
 * Testes unitários para AddressQueryService
 * Segue o padrão profissional usado no ms-user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address Query Service Tests")
class AddressQueryServiceTest {

    @InjectMocks
    private AddressQueryService addressQueryService;

    @Mock
    private AddressRepositoryPort addressRepositoryPort;

    @Mock
    private AddressApplicationMapper addressApplicationMapper;

    private AddressTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = AddressTestBuilder.builder();
    }

    // === TESTES DE GET BY ID ===

    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void shouldGetAddressByIdSuccessfully() {
        // Given
        var query = builder.buildGetByIdQuery();
        var address = builder.buildDomain();
        var addressDetailsView = builder.buildDetailsView();
        
        when(addressRepositoryPort.findById(query.id())).thenReturn(Optional.of(address));
        when(addressApplicationMapper.toDetailsView(address)).thenReturn(addressDetailsView);

        // When
        var result = addressQueryService.getById(query);

        // Then
        assertNotNull(result);
        assertEquals(query.id(), result.id());

        verify(addressRepositoryPort).findById(query.id());
        verify(addressApplicationMapper).toDetailsView(address);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando endereço não existe por ID")
    void shouldThrowNotFoundExceptionWhenAddressDoesNotExistById() {
        // Given
        var query = builder.buildGetByIdQuery();
        when(addressRepositoryPort.findById(query.id())).thenReturn(Optional.empty());

        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> addressQueryService.getById(query));
        assertEquals("Endereço não encontrado: " + query.id(), exception.getMessage());
        assertEquals("ADDRESS_NOT_FOUND", exception.getErrorCode());

        verify(addressRepositoryPort).findById(query.id());
        verify(addressApplicationMapper, never()).toDetailsView(any());
    }

    // === TESTES DE GET BY USER ID ===

    @Test
    @DisplayName("Deve buscar endereços por userId com sucesso")
    void shouldGetAddressesByUserIdSuccessfully() {
        // Given
        var query = builder.buildGetByUserIdQuery();
        var address = builder.buildDomain();
        var addressDetailsView = builder.buildDetailsView();
        
        when(addressRepositoryPort.findByUserId(query.userId())).thenReturn(List.of(address));
        when(addressApplicationMapper.toDetailsView(address)).thenReturn(addressDetailsView);

        // When
        var result = addressQueryService.getByUserId(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(addressRepositoryPort).findByUserId(query.userId());
        verify(addressApplicationMapper).toDetailsView(address);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem endereços")
    void shouldReturnEmptyListWhenUserHasNoAddresses() {
        // Given
        var query = builder.buildGetByUserIdQuery();
        when(addressRepositoryPort.findByUserId(query.userId())).thenReturn(List.of());

        // When
        var result = addressQueryService.getByUserId(query);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(addressRepositoryPort).findByUserId(query.userId());
        verify(addressApplicationMapper, never()).toDetailsView(any());
    }

    // === TESTES DE SEARCH ===

    @Test
    @DisplayName("Deve buscar endereços com critérios com sucesso")
    void shouldSearchAddressesWithCriteriaSuccessfully() {
        // Given
        var query = builder.buildSearchQuery();
        var address = builder.buildDomain();
        var addressSearchView = builder.buildSearchView();
        var criteria = new com.keepguard.ms_user.application.dto.address.AddressSearchCriteriaDTO(
            query.userId(),
            query.city(),
            query.state(),
            query.zipCode(),
            query.type(),
            query.primary(),
            query.active(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );

        var pageResult = new PageResultDTO<>(
            List.of(address),
            1L,
            0,
            20
        );

        when(addressApplicationMapper.toSearchCriteria(query)).thenReturn(criteria);
        when(addressRepositoryPort.search(criteria)).thenReturn(pageResult);
        when(addressApplicationMapper.toSearchView(address)).thenReturn(addressSearchView);

        // When
        var result = addressQueryService.search(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());

        verify(addressApplicationMapper).toSearchCriteria(query);
        verify(addressRepositoryPort).search(criteria);
        verify(addressApplicationMapper).toSearchView(address);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum endereço encontrado")
    void shouldReturnEmptyPageWhenNoAddressesFound() {
        // Given
        var query = builder.buildSearchQuery();
        var criteria = new com.keepguard.ms_user.application.dto.address.AddressSearchCriteriaDTO(
            query.userId(),
            query.city(),
            query.state(),
            query.zipCode(),
            query.type(),
            query.primary(),
            query.active(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );
        
        var emptyPageResult = new PageResultDTO<Address>(List.of(), 0L, 0, 20);

        when(addressApplicationMapper.toSearchCriteria(query)).thenReturn(criteria);
        when(addressRepositoryPort.search(criteria)).thenReturn(emptyPageResult);

        // When
        var result = addressQueryService.search(query);

        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());

        verify(addressApplicationMapper).toSearchCriteria(query);
        verify(addressRepositoryPort).search(criteria);
        verify(addressApplicationMapper, never()).toSearchView(any());
    }
}
