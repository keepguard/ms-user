package com.keepguard.ms_user.adapters.in.rest.contact.mapper;

import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.response.ContactDetailsResponseDTO;
import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ContactAdapterMapper {

    // === Command Methods ===

    public ContactCreateCommandDTO toCreateCommand(ContactCreateRequestDTO request, UUID companyId) {
        return new ContactCreateCommandDTO(
            request.userId(),
            companyId,
            request.value(),
            request.type(),
            request.description(),
            request.primary(),
            request.active()
        );
    }

    public ContactUpdateCommandDTO toUpdateCommand(ContactUpdateRequestDTO request, UUID id, UUID companyId) {
        return new ContactUpdateCommandDTO(
            id,
            companyId,
            Optional.ofNullable(request.value()),
            Optional.ofNullable(request.type()),
            Optional.ofNullable(request.description()),
            Optional.ofNullable(request.primary()),
            Optional.ofNullable(request.active())
        );
    }

    public ContactDeleteCommandDTO toDeleteCommand(UUID id, UUID companyId) {
        return new ContactDeleteCommandDTO(id, companyId);
    }

    // === Query Methods ===

    public ContactGetByIdQueryDTO toGetByIdQuery(UUID id, UUID companyId) {
        return new ContactGetByIdQueryDTO(id, companyId);
    }

    public ContactGetByUserIdQueryDTO toGetByUserIdQuery(UUID userId, UUID companyId) {
        return new ContactGetByUserIdQueryDTO(userId, companyId);
    }

    public ContactSearchQueryDTO toSearchQuery(ContactSearchRequestDTO request, UUID companyId, UUID userId) {
        var contactType = request.getType() != null ? ContactTypeEnum.valueOf(request.getType()) : null;

        return new ContactSearchQueryDTO(
            companyId,
            userId,
            request.getValue(),
            contactType,
            request.getPrimary(),
            request.getActive(),
            request.getPage() != null ? request.getPage() : 0,
            request.getSize() != null ? request.getSize() : 20,
            request.getSort(),
            request.getDirection() != null ? request.getDirection() : "ASC"
        );
    }

    // === Response Methods ===

    public ContactDetailsResponseDTO toResponseDTO(ContactDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        return ContactDetailsResponseDTO.builder()
            .id(view.id())
            .userId(view.userId())
            .value(view.value())
            .type(view.type())
            .description(view.description())
            .primary(view.primary())
            .active(view.active())
            .createdAt(view.createdAt())
            .updatedAt(view.updatedAt())
            .build();
    }

    public ContactDetailsResponseDTO toSearchResponseDTO(ContactSearchViewDTO view) {
        if (view == null) {
            return null;
        }

        return ContactDetailsResponseDTO.builder()
            .id(view.id())
            .userId(view.userId())
            .value(view.value())
            .type(view.type())
            .primary(view.primary())
            .active(view.active())
            .createdAt(view.createdAt())
            .build();
    }
}

