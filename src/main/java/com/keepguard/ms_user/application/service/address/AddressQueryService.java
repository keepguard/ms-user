package com.keepguard.ms_user.application.service.address;

import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.mapper.AddressApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.AddressRepositoryPort;
import com.keepguard.ms_user.application.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressQueryService {

    private final AddressRepositoryPort addressRepositoryPort;
    private final AddressApplicationMapper addressApplicationMapper;

    @Transactional(readOnly = true)
    public AddressDetailsViewDTO getById(AddressGetByIdQueryDTO query) {
        log.info("Buscando endereço por ID: {}, companyId: {}", query.id(), query.companyId());

        var address = addressRepositoryPort.findById(query.id())
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado: " + query.id(),
                    "ADDRESS_NOT_FOUND", Map.of("id", query.id().toString())));

        return addressApplicationMapper.toDetailsView(address);
    }

    @Transactional(readOnly = true)
    public List<AddressDetailsViewDTO> getByUserId(AddressGetByUserIdQueryDTO query) {
        log.info("Buscando endereços do usuário: {}, companyId: {}", query.userId(), query.companyId());

        var addresses = addressRepositoryPort.findByUserId(query.userId());

        return addresses.stream()
                .map(addressApplicationMapper::toDetailsView)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResultDTO<AddressSearchViewDTO> search(AddressSearchQueryDTO query) {
        log.info("Buscando endereços com critérios: userId={}, city={}, state={}, type={}, page={}, size={}",
                query.userId(), query.city(), query.state(), query.type(), query.page(), query.size());

        var criteria = addressApplicationMapper.toSearchCriteria(query);
        var pageResult = addressRepositoryPort.search(criteria);

        var addressSearchViews = pageResult.content().stream()
                .map(addressApplicationMapper::toSearchView)
                .toList();

        return new PageResultDTO<>(
                addressSearchViews,
                pageResult.totalElements(),
                pageResult.page(),
                pageResult.size()
        );
    }
}

