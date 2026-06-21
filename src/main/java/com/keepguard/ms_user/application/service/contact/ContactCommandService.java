package com.keepguard.ms_user.application.service.contact;

import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.application.mapper.ContactApplicationMapper;
import com.keepguard.ms_user.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_user.application.port.out.persistence.ContactRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactCommandService {

    private final ContactRepositoryPort contactRepositoryPort;
    private final ContactApplicationMapper contactApplicationMapper;
    private final MetricsPort metricsPort;

    @LogOperation(
        operation = "CREATE_CONTACT",
        description = "Criando novo contato para usuário: {command.userId}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "CONTACT"
    )
    @Transactional
    public ContactDetailsViewDTO create(ContactCreateCommandDTO command) {
        log.info("Criando contato para usuário: {}, tipo: {}, xApplication: {}",
                command.userId(), command.type(), command.xApplication());

        var contact = contactApplicationMapper.toDomain(command);
        var contactSaved = contactRepositoryPort.save(contact);

        metricsPort.incrementCounter("contact_created_total",
            Map.of("entity_id", contactSaved.getId().toString(), "type", contactSaved.getType().name()));

        log.info("Contato criado com sucesso. ID: {}", contactSaved.getId());
        return contactApplicationMapper.toDetailsView(contactSaved);
    }

    @LogOperation(
        operation = "UPDATE_CONTACT",
        description = "Atualizando contato: {command.id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "CONTACT"
    )
    @Transactional
    public ContactDetailsViewDTO update(ContactUpdateCommandDTO command) {
        log.info("Atualizando contato: {}, xApplication: {}", command.id(), command.xApplication());

        var contact = contactRepositoryPort.findById(command.id())
                .orElseThrow(() -> {
                    metricsPort.incrementCounter("contact_business_errors_total",
                        Map.of("error_code", "CONTACT_NOT_FOUND", "operation", "update"));
                    return new NotFoundException("Contato não encontrado: " + command.id(), "CONTACT_NOT_FOUND",
                        Map.of("id", command.id().toString()));
                });

        contactApplicationMapper.updateDomain(contact, command);
        var contactUpdated = contactRepositoryPort.save(contact);

        metricsPort.incrementCounter("contact_updated_total",
            Map.of("entity_id", contactUpdated.getId().toString()));

        log.info("Contato atualizado com sucesso. ID: {}", contactUpdated.getId());
        return contactApplicationMapper.toDetailsView(contactUpdated);
    }

    @LogOperation(
        operation = "DELETE_CONTACT",
        description = "Deletando contato: {command.id}",
        audit = true,
        auditAction = "DELETE",
        auditEntityType = "CONTACT"
    )
    @Transactional
    public void delete(ContactDeleteCommandDTO command) {
        log.info("Deletando contato: {}, xApplication: {}", command.id(), command.xApplication());

        if (!contactRepositoryPort.existsById(command.id())) {
            metricsPort.incrementCounter("contact_business_errors_total",
                Map.of("error_code", "CONTACT_NOT_FOUND", "operation", "delete"));
            throw new NotFoundException("Contato não encontrado: " + command.id(), "CONTACT_NOT_FOUND",
                Map.of("id", command.id().toString()));
        }

        contactRepositoryPort.deleteById(command.id());

        metricsPort.incrementCounter("contact_deleted_total",
            Map.of("entity_id", command.id().toString()));

        log.info("Contato deletado com sucesso. ID: {}", command.id());
    }
}

