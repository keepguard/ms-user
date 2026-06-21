package com.keepguard.ms_user.application.service.contact;

import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.application.dto.common.PageResultDTO;
import com.keepguard.ms_user.application.mapper.ContactApplicationMapper;
import com.keepguard.ms_user.application.port.out.persistence.ContactRepositoryPort;
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
public class ContactQueryService {

    private final ContactRepositoryPort contactRepositoryPort;
    private final ContactApplicationMapper contactApplicationMapper;

    @Transactional(readOnly = true)
    public ContactDetailsViewDTO getById(ContactGetByIdQueryDTO query) {
        log.info("Buscando contato por ID: {}, xApplication: {}", query.id(), query.xApplication());

        var contact = contactRepositoryPort.findById(query.id())
                .orElseThrow(() -> new NotFoundException("Contato não encontrado: " + query.id(),
                    "CONTACT_NOT_FOUND", Map.of("id", query.id().toString())));

        return contactApplicationMapper.toDetailsView(contact);
    }

    @Transactional(readOnly = true)
    public List<ContactDetailsViewDTO> getByUserId(ContactGetByUserIdQueryDTO query) {
        log.info("Buscando contatos do usuário: {}, xApplication: {}", query.userId(), query.xApplication());

        var contacts = contactRepositoryPort.findByUserId(query.userId());

        return contacts.stream()
                .map(contactApplicationMapper::toDetailsView)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResultDTO<ContactSearchViewDTO> search(ContactSearchQueryDTO query) {
        log.info("Buscando contatos com critérios: userId={}, type={}, value={}, page={}, size={}",
                query.userId(), query.type(), query.value(), query.page(), query.size());

        var criteria = contactApplicationMapper.toSearchCriteria(query);
        var pageResult = contactRepositoryPort.search(criteria);

        var contactSearchViews = pageResult.content().stream()
                .map(contactApplicationMapper::toSearchView)
                .toList();

        return new PageResultDTO<>(
                contactSearchViews,
                pageResult.totalElements(),
                pageResult.page(),
                pageResult.size()
        );
    }
}

