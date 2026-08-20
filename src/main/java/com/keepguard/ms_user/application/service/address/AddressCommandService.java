package com.keepguard.ms_user.application.service.address;

import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.application.mapper.AddressApplicationMapper;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.AddressRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressCommandService {

    private final AddressRepositoryPort addressRepositoryPort;
    private final AddressApplicationMapper addressApplicationMapper;
    private final MetricsPort metricsPort;

    @LogOperation(
        operation = "CREATE_ADDRESS",
        description = "Criando novo endereço para usuário: {command.userId}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "ADDRESS"
    )
    @Transactional
    public AddressDetailsViewDTO create(AddressCreateCommandDTO command) {
        log.info("Criando endereço para usuário: {}, cidade: {}, estado: {}, tenantId: {}",
                command.userId(), command.city(), command.state(), command.tenantId());

        // Validações de campos obrigatórios são feitas pela entidade de domínio Address
        var address = addressApplicationMapper.toDomain(command);

        // Regra de negócio: Se não existir nenhum endereço para o usuário, o novo deve ser principal
        var existingAddresses = addressRepositoryPort.findByUserId(command.userId());
        if (existingAddresses.isEmpty()) {
            log.info("Primeiro endereço do usuário {}, marcando como principal", command.userId());
            address.setPrimary(true);
        } else if (address.isPrimary()) {
            // Regra de negócio: Se o novo endereço for marcado como principal, desmarcar o atual
            handlePrimaryAddressChange(command.userId(), null);
        }

        var addressSaved = addressRepositoryPort.save(address);

        metricsPort.incrementCounter("address_created_total",
            Map.of("entity_id", addressSaved.getId().toString(), "type", addressSaved.getType().name()));

        log.info("Endereço criado com sucesso. ID: {}", addressSaved.getId());
        return addressApplicationMapper.toDetailsView(addressSaved);
    }

    @LogOperation(
        operation = "UPDATE_ADDRESS",
        description = "Atualizando endereço: {command.id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "ADDRESS"
    )
    @Transactional
    public AddressDetailsViewDTO update(AddressUpdateCommandDTO command) {
        log.info("Atualizando endereço: {}, tenantId: {}", command.id(), command.tenantId());

        var address = addressRepositoryPort.findById(command.id())
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("address_business_errors_total",
                        Map.of("error_code", "ADDRESS_NOT_FOUND", "operation", "update"));
                    return new NotFoundException("Endereço não encontrado: " + command.id(), "ADDRESS_NOT_FOUND",
                        Map.of("id", command.id().toString()));
                });

        // Regra de negócio: Se o endereço for marcado como principal, desmarcar o atual
        if (command.primary().isPresent() && command.primary().get() && !address.isPrimary()) {
            handlePrimaryAddressChange(address.getUserId(), address.getId());
        }

        // Validações de campos obrigatórios são feitas pelos setters da entidade de domínio Address
        addressApplicationMapper.updateDomain(address, command);
        var addressUpdated = addressRepositoryPort.save(address);

        metricsPort.incrementCounter("address_updated_total",
            Map.of("entity_id", addressUpdated.getId().toString()));

        log.info("Endereço atualizado com sucesso. ID: {}", addressUpdated.getId());
        return addressApplicationMapper.toDetailsView(addressUpdated);
    }

    @LogOperation(
        operation = "DELETE_ADDRESS",
        description = "Deletando endereço: {command.id}",
        audit = true,
        auditAction = "DELETE",
        auditEntityType = "ADDRESS"
    )
    @Transactional
    public void delete(AddressDeleteCommandDTO command) {
        log.info("Deletando endereço: {}, tenantId: {}", command.id(), command.tenantId());

        if (!addressRepositoryPort.existsById(command.id())) {
            metricsPort.incrementCounter("address_business_errors_total",
                Map.of("error_code", "ADDRESS_NOT_FOUND", "operation", "delete"));
            throw new NotFoundException("Endereço não encontrado: " + command.id(), "ADDRESS_NOT_FOUND",
                Map.of("id", command.id().toString()));
        }

        addressRepositoryPort.deleteById(command.id());

        metricsPort.incrementCounter("address_deleted_total",
            Map.of("entity_id", command.id().toString()));

        log.info("Endereço deletado com sucesso. ID: {}", command.id());
    }

    private void handlePrimaryAddressChange(java.util.UUID userId, java.util.UUID excludeAddressId) {
        var currentPrimaryAddress = addressRepositoryPort.findByUserIdAndPrimaryTrue(userId);
        
        if (currentPrimaryAddress.isPresent()) {
            var primaryAddress = currentPrimaryAddress.get();
            
            // Se tem um ID para excluir e é o mesmo do endereço principal atual, não fazer nada
            if (excludeAddressId != null && primaryAddress.getId().equals(excludeAddressId)) {
                return;
            }
            
            log.info("Desmarcando endereço {} como principal do usuário {}", 
                    primaryAddress.getId(), userId);
            primaryAddress.markAsSecondary();
            addressRepositoryPort.save(primaryAddress);
        }
    }
}

