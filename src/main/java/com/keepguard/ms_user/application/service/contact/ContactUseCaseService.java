package com.keepguard.ms_user.application.service.contact;

import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.in.ContactPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactUseCaseService implements ContactPort {

    private final ContactCommandService contactCommandService;
    private final ContactQueryService contactQueryService;

    // === Commands ===

    @Override
    public ContactDetailsViewDTO create(ContactCreateCommandDTO command) {
        UUID userId = command.userId();
        log.info("🔍 USECASE - create chamado para userId: {}", userId);
        try {
            var result = contactCommandService.create(command);
            log.info("🔍 USECASE - create sucesso para userId: {}", userId);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no create para userId: {} - {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ContactDetailsViewDTO update(ContactUpdateCommandDTO command) {
        UUID id = command.id();
        log.info("🔍 USECASE - update chamado para contato: {}", id);
        try {
            var result = contactCommandService.update(command);
            log.info("🔍 USECASE - update sucesso para contato: {}", id);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no update para contato: {} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(ContactDeleteCommandDTO command) {
        UUID id = command.id();
        log.info("🔍 USECASE - delete chamado para contato: {}", id);
        try {
            contactCommandService.delete(command);
            log.info("🔍 USECASE - delete sucesso para contato: {}", id);
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no delete para contato: {} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // === Queries ===

    @Override
    public ContactDetailsViewDTO getById(ContactGetByIdQueryDTO query) {
        return contactQueryService.getById(query);
    }

    @Override
    public List<ContactDetailsViewDTO> getByUserId(ContactGetByUserIdQueryDTO query) {
        return contactQueryService.getByUserId(query);
    }

    @Override
    public PageResultDTO<ContactSearchViewDTO> search(ContactSearchQueryDTO query) {
        return contactQueryService.search(query);
    }
}

