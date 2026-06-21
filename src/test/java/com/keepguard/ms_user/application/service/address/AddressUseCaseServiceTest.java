package com.keepguard.ms_user.application.service.address;

import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.test.builder.AddressTestBuilder;
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
 * Testes unitários para AddressUseCaseService
 * Segue o padrão profissional usado no ms-user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address UseCase Service Tests")
class AddressUseCaseServiceTest {

    @InjectMocks
    private AddressUseCaseService addressUseCaseService;

    @Mock
    private AddressCommandService addressCommandService;

    @Mock
    private AddressQueryService addressQueryService;

    private AddressTestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = AddressTestBuilder.builder();
    }

    // === TESTES DE CREATE ===

    @Test
    @DisplayName("Deve criar endereço com sucesso")
    void shouldCreateAddressSuccessfully() {
        // Given
        var command = builder.buildCreateCommand();
        var addressDetailsView = builder.buildDetailsView();

        when(addressCommandService.create(command)).thenReturn(addressDetailsView);

        // When
        var result = addressUseCaseService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(addressDetailsView.id(), result.id());

        verify(addressCommandService).create(command);
    }

    @Test
    @DisplayName("Deve propagar exceção ao criar endereço")
    void shouldPropagateExceptionWhenCreatingAddress() {
        // Given
        var command = builder.buildCreateCommand();
        RuntimeException exception = new RuntimeException("Erro ao criar endereço");
        when(addressCommandService.create(command)).thenThrow(exception);

        // When & Then
        assertThrows(RuntimeException.class, () -> addressUseCaseService.create(command));
        verify(addressCommandService).create(command);
    }

    // === TESTES DE UPDATE ===

    @Test
    @DisplayName("Deve atualizar endereço com sucesso")
    void shouldUpdateAddressSuccessfully() {
        // Given
        var command = builder.buildUpdateCommand();
        var addressDetailsView = builder.buildDetailsView();

        when(addressCommandService.update(command)).thenReturn(addressDetailsView);

        // When
        var result = addressUseCaseService.update(command);

        // Then
        assertNotNull(result);
        assertEquals(command.id(), result.id());

        verify(addressCommandService).update(command);
    }

    @Test
    @DisplayName("Deve propagar exceção ao atualizar endereço")
    void shouldPropagateExceptionWhenUpdatingAddress() {
        // Given
        var command = builder.buildUpdateCommand();
        RuntimeException exception = new RuntimeException("Erro ao atualizar endereço");
        when(addressCommandService.update(command)).thenThrow(exception);

        // When & Then
        assertThrows(RuntimeException.class, () -> addressUseCaseService.update(command));
        verify(addressCommandService).update(command);
    }

    // === TESTES DE DELETE ===

    @Test
    @DisplayName("Deve deletar endereço com sucesso")
    void shouldDeleteAddressSuccessfully() {
        // Given
        var command = builder.buildDeleteCommand();
        doNothing().when(addressCommandService).delete(command);

        // When
        addressUseCaseService.delete(command);

        // Then
        verify(addressCommandService).delete(command);
    }

    @Test
    @DisplayName("Deve propagar exceção ao deletar endereço")
    void shouldPropagateExceptionWhenDeletingAddress() {
        // Given
        var command = builder.buildDeleteCommand();
        RuntimeException exception = new RuntimeException("Erro ao deletar endereço");
        doThrow(exception).when(addressCommandService).delete(command);

        // When & Then
        assertThrows(RuntimeException.class, () -> addressUseCaseService.delete(command));
        verify(addressCommandService).delete(command);
    }

    // === TESTES DE GET BY ID ===

    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void shouldGetAddressByIdSuccessfully() {
        // Given
        var query = builder.buildGetByIdQuery();
        var addressDetailsView = builder.buildDetailsView();

        when(addressQueryService.getById(query)).thenReturn(addressDetailsView);

        // When
        var result = addressUseCaseService.getById(query);

        // Then
        assertNotNull(result);
        assertEquals(addressDetailsView.id(), result.id());

        verify(addressQueryService).getById(query);
    }

    // === TESTES DE GET BY USER ID ===

    @Test
    @DisplayName("Deve buscar endereços por userId com sucesso")
    void shouldGetAddressesByUserIdSuccessfully() {
        // Given
        var query = builder.buildGetByUserIdQuery();
        var addressDetailsView = builder.buildDetailsView();

        when(addressQueryService.getByUserId(query)).thenReturn(List.of(addressDetailsView));

        // When
        var result = addressUseCaseService.getByUserId(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(addressQueryService).getByUserId(query);
    }

    // === TESTES DE SEARCH ===

    @Test
    @DisplayName("Deve buscar endereços com critérios com sucesso")
    void shouldSearchAddressesWithCriteriaSuccessfully() {
        // Given
        var query = builder.buildSearchQuery();
        var addressSearchView = builder.buildSearchView();
        var pageResult = new PageResultDTO<>(List.of(addressSearchView), 1L, 0, 20);

        when(addressQueryService.search(query)).thenReturn(pageResult);

        // When
        var result = addressUseCaseService.search(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());

        verify(addressQueryService).search(query);
    }
}
