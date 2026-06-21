package com.keepguard.ms_user.application.service.address;

import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.port.in.AddressPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressUseCaseService implements AddressPort {

    private final AddressCommandService addressCommandService;
    private final AddressQueryService addressQueryService;

    // === Commands ===

    @Override
    public AddressDetailsViewDTO create(AddressCreateCommandDTO command) {
        UUID userId = command.userId();
        log.info("🔍 USECASE - create chamado para userId: {}", userId);
        try {
            var result = addressCommandService.create(command);
            log.info("🔍 USECASE - create sucesso para userId: {}", userId);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no create para userId: {} - {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AddressDetailsViewDTO update(AddressUpdateCommandDTO command) {
        UUID id = command.id();
        log.info("🔍 USECASE - update chamado para endereço: {}", id);
        try {
            var result = addressCommandService.update(command);
            log.info("🔍 USECASE - update sucesso para endereço: {}", id);
            return result;
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no update para endereço: {} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(AddressDeleteCommandDTO command) {
        UUID id = command.id();
        log.info("🔍 USECASE - delete chamado para endereço: {}", id);
        try {
            addressCommandService.delete(command);
            log.info("🔍 USECASE - delete sucesso para endereço: {}", id);
        } catch (Exception e) {
            log.error("🔍 USECASE - ERRO no delete para endereço: {} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // === Queries ===

    @Override
    public AddressDetailsViewDTO getById(AddressGetByIdQueryDTO query) {
        return addressQueryService.getById(query);
    }

    @Override
    public List<AddressDetailsViewDTO> getByUserId(AddressGetByUserIdQueryDTO query) {
        return addressQueryService.getByUserId(query);
    }

    @Override
    public PageResultDTO<AddressSearchViewDTO> search(AddressSearchQueryDTO query) {
        return addressQueryService.search(query);
    }
}

