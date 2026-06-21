package com.keepguard.ms_user.application.service.address;

import com.keepguard.ms_user.application.mapper.AddressApplicationMapper;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.AddressRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import com.keepguard.ms_user.test.builder.AddressTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AddressCommandService
 * Segue o padrão profissional usado no ms-user
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address Command Service Tests")
class AddressCommandServiceTest {

    @InjectMocks
    private AddressCommandService addressCommandService;

    @Mock
    private AddressRepositoryPort addressRepositoryPort;

    @Mock
    private AddressApplicationMapper addressApplicationMapper;

    @Mock
    private MetricsPort metricsPort;

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
        var address = builder.buildDomain();
        var existingAddress = builder.buildDomain();
        var addressDetailsView = builder.buildDetailsView();

        when(addressApplicationMapper.toDomain(command)).thenReturn(address);
        when(addressRepositoryPort.findByUserId(command.userId())).thenReturn(List.of(existingAddress));
        when(addressRepositoryPort.save(address)).thenReturn(address);
        when(addressApplicationMapper.toDetailsView(address)).thenReturn(addressDetailsView);

        // When
        var result = addressCommandService.create(command);

        // Then
        assertNotNull(result);
        assertEquals(address.getId(), result.id());

        verify(addressApplicationMapper).toDomain(command);
        verify(addressRepositoryPort).findByUserId(command.userId());
        verify(addressRepositoryPort).save(address);
        verify(addressApplicationMapper).toDetailsView(address);
        verify(metricsPort).incrementCounter(eq("address_created_total"), anyMap());
    }

    @Test
    @DisplayName("Deve marcar primeiro endereço do usuário como principal automaticamente")
    void shouldMarkFirstAddressAsPrimaryAutomatically() {
        // Given
        var userId = java.util.UUID.randomUUID();
        var command = AddressTestBuilder.builder().withUserId(userId).asSecondary().buildCreateCommand();
        var address = AddressTestBuilder.builder().withUserId(userId).asSecondary().buildDomain();
        var addressDetailsView = AddressTestBuilder.builder().withUserId(userId).asPrimary().buildDetailsView();

        when(addressApplicationMapper.toDomain(command)).thenReturn(address);
        when(addressRepositoryPort.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(addressRepositoryPort.save(any())).thenAnswer(invocation -> {
            var savedAddress = (com.keepguard.ms_user.domain.entity.Address) invocation.getArgument(0);
            assertTrue(savedAddress.isPrimary(), "Endereço deve ser marcado como principal");
            return savedAddress;
        });
        when(addressApplicationMapper.toDetailsView(any())).thenReturn(addressDetailsView);

        // When
        var result = addressCommandService.create(command);

        // Then
        assertNotNull(result);
        verify(addressRepositoryPort).findByUserId(userId);
        verify(addressRepositoryPort).save(any());
        verify(metricsPort).incrementCounter(eq("address_created_total"), anyMap());
    }

    @Test
    @DisplayName("Deve desmarcar endereço principal anterior ao criar novo como principal")
    void shouldUnmarkPreviousPrimaryAddressWhenCreatingNewPrimary() {
        // Given
        var userId = java.util.UUID.randomUUID();
        var command = AddressTestBuilder.builder().withUserId(userId).asPrimary().buildCreateCommand();
        var newAddress = AddressTestBuilder.builder().withUserId(userId).asPrimary().buildDomain();
        var existingAddress = AddressTestBuilder.builder().withUserId(userId).asSecondary().buildDomain();
        var currentPrimaryAddress = AddressTestBuilder.builder().withUserId(userId).asPrimary()
                .withId(java.util.UUID.randomUUID()).buildDomain();
        var addressDetailsView = AddressTestBuilder.builder().withUserId(userId).buildDetailsView();

        when(addressApplicationMapper.toDomain(command)).thenReturn(newAddress);
        when(addressRepositoryPort.findByUserId(userId)).thenReturn(List.of(existingAddress));
        when(addressRepositoryPort.findByUserIdAndPrimaryTrue(userId))
                .thenReturn(Optional.of(currentPrimaryAddress));
        when(addressRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressApplicationMapper.toDetailsView(any())).thenReturn(addressDetailsView);

        // When
        var result = addressCommandService.create(command);

        // Then
        assertNotNull(result);
        verify(addressRepositoryPort).findByUserId(userId);
        verify(addressRepositoryPort).findByUserIdAndPrimaryTrue(userId);
        verify(addressRepositoryPort, times(2)).save(any());
        assertFalse(currentPrimaryAddress.isPrimary(), "Endereço anterior deve ter sido desmarcado como principal");
        verify(metricsPort).incrementCounter(eq("address_created_total"), anyMap());
    }

    // === TESTES DE UPDATE ===

    @Test
    @DisplayName("Deve atualizar endereço com sucesso")
    void shouldUpdateAddressSuccessfully() {
        // Given
        var command = builder.buildUpdateCommand();
        var address = builder.buildDomain();
        var addressDetailsView = builder.buildDetailsView();

        when(addressRepositoryPort.findById(command.id())).thenReturn(Optional.of(address));
        when(addressRepositoryPort.save(address)).thenReturn(address);
        when(addressApplicationMapper.toDetailsView(address)).thenReturn(addressDetailsView);

        // When
        var result = addressCommandService.update(command);

        // Then
        assertNotNull(result);
        assertEquals(command.id(), result.id());

        verify(addressRepositoryPort).findById(command.id());
        verify(addressApplicationMapper).updateDomain(address, command);
        verify(addressRepositoryPort).save(address);
        verify(addressApplicationMapper).toDetailsView(address);
        verify(metricsPort).incrementCounter(eq("address_updated_total"), anyMap());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando endereço não existe para update")
    void shouldThrowNotFoundExceptionWhenAddressDoesNotExistForUpdate() {
        // Given
        var command = builder.buildUpdateCommand();
        when(addressRepositoryPort.findById(command.id())).thenReturn(Optional.empty());

        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> addressCommandService.update(command));
        assertEquals("Endereço não encontrado: " + command.id(), exception.getMessage());
        assertEquals("ADDRESS_NOT_FOUND", exception.getErrorCode());

        verify(addressRepositoryPort).findById(command.id());
        verify(addressApplicationMapper, never()).updateDomain(any(), any());
        verify(addressRepositoryPort, never()).save(any());
        verify(metricsPort).incrementCounter(eq("address_business_errors_total"), anyMap());
    }

    @Test
    @DisplayName("Deve desmarcar endereço principal anterior ao atualizar outro como principal")
    void shouldUnmarkPreviousPrimaryAddressWhenUpdatingToPrimary() {
        // Given
        var userId = java.util.UUID.randomUUID();
        var addressId = java.util.UUID.randomUUID();
        var command = AddressTestBuilder.builder().withId(addressId).withUserId(userId)
                .buildUpdateCommandWithPrimary(true);
        var address = AddressTestBuilder.builder().withId(addressId).withUserId(userId)
                .asSecondary().buildDomain();
        var currentPrimaryAddress = AddressTestBuilder.builder().withUserId(userId).asPrimary()
                .withId(java.util.UUID.randomUUID()).buildDomain();
        var addressDetailsView = AddressTestBuilder.builder().withUserId(userId).buildDetailsView();

        when(addressRepositoryPort.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepositoryPort.findByUserIdAndPrimaryTrue(userId))
                .thenReturn(Optional.of(currentPrimaryAddress));
        when(addressRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressApplicationMapper.toDetailsView(any())).thenReturn(addressDetailsView);

        // When
        var result = addressCommandService.update(command);

        // Then
        assertNotNull(result);
        verify(addressRepositoryPort).findById(addressId);
        verify(addressRepositoryPort).findByUserIdAndPrimaryTrue(userId);
        assertFalse(currentPrimaryAddress.isPrimary(), "Endereço anterior deve ter sido desmarcado como principal");
        verify(addressApplicationMapper).updateDomain(address, command);
        verify(addressRepositoryPort, times(2)).save(any());
        verify(metricsPort).incrementCounter(eq("address_updated_total"), anyMap());
    }

    @Test
    @DisplayName("Não deve desmarcar endereço principal quando já é o mesmo sendo atualizado")
    void shouldNotUnmarkPrimaryAddressWhenUpdatingSameAddress() {
        // Given
        var userId = java.util.UUID.randomUUID();
        var addressId = java.util.UUID.randomUUID();
        var command = AddressTestBuilder.builder().withId(addressId).withUserId(userId).asPrimary()
                .buildUpdateCommandWithPrimary(true);
        var address = AddressTestBuilder.builder().withId(addressId).withUserId(userId).asPrimary()
                .buildDomain();
        var addressDetailsView = AddressTestBuilder.builder().withUserId(userId).buildDetailsView();

        when(addressRepositoryPort.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressApplicationMapper.toDetailsView(any())).thenReturn(addressDetailsView);

        // When
        var result = addressCommandService.update(command);

        // Then
        assertNotNull(result);
        assertTrue(address.isPrimary(), "Endereço deve continuar como principal");
        verify(addressRepositoryPort).findById(addressId);
        verify(addressRepositoryPort, never()).findByUserIdAndPrimaryTrue(any());
        verify(addressApplicationMapper).updateDomain(address, command);
        verify(addressRepositoryPort).save(any());
        verify(metricsPort).incrementCounter(eq("address_updated_total"), anyMap());
    }

    // === TESTES DE DELETE ===

    @Test
    @DisplayName("Deve deletar endereço com sucesso")
    void shouldDeleteAddressSuccessfully() {
        // Given
        var command = builder.buildDeleteCommand();
        when(addressRepositoryPort.existsById(command.id())).thenReturn(true);

        // When
        addressCommandService.delete(command);

        // Then
        verify(addressRepositoryPort).existsById(command.id());
        verify(addressRepositoryPort).deleteById(command.id());
        verify(metricsPort).incrementCounter(eq("address_deleted_total"), anyMap());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando endereço não existe para delete")
    void shouldThrowNotFoundExceptionWhenAddressDoesNotExistForDelete() {
        // Given
        var command = builder.buildDeleteCommand();
        when(addressRepositoryPort.existsById(command.id())).thenReturn(false);

        // When & Then
        var exception = assertThrows(NotFoundException.class, () -> addressCommandService.delete(command));
        assertEquals("Endereço não encontrado: " + command.id(), exception.getMessage());
        assertEquals("ADDRESS_NOT_FOUND", exception.getErrorCode());

        verify(addressRepositoryPort).existsById(command.id());
        verify(addressRepositoryPort, never()).deleteById(any());
        verify(metricsPort).incrementCounter(eq("address_business_errors_total"), anyMap());
    }
}
