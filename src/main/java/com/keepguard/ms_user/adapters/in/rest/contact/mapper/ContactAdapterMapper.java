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

    public ContactCreateCommandDTO toCreateCommand(ContactCreateRequestDTO request, UUID tenantId) {
        return new ContactCreateCommandDTO(
            request.userId(),
            tenantId,
            request.value(),
            request.type(),
            request.description(),
            request.primary(),
            request.active()
        );
    }

    public ContactUpdateCommandDTO toUpdateCommand(ContactUpdateRequestDTO request, UUID id, UUID tenantId) {
        return new ContactUpdateCommandDTO(
            id,
            tenantId,
            Optional.ofNullable(request.value()),
            Optional.ofNullable(request.type()),
            Optional.ofNullable(request.description()),
            Optional.ofNullable(request.primary()),
            Optional.ofNullable(request.active())
        );
    }

    public ContactDeleteCommandDTO toDeleteCommand(UUID id, UUID tenantId) {
        return new ContactDeleteCommandDTO(id, tenantId);
    }

    // === Query Methods ===

    public ContactGetByIdQueryDTO toGetByIdQuery(UUID id, UUID tenantId) {
        return new ContactGetByIdQueryDTO(id, tenantId);
    }

    public ContactGetByUserIdQueryDTO toGetByUserIdQuery(UUID userId, UUID tenantId) {
        return new ContactGetByUserIdQueryDTO(userId, tenantId);
    }

    public ContactSearchQueryDTO toSearchQuery(ContactSearchRequestDTO request, UUID tenantId, UUID userId) {
        var contactType = request.getType() != null ? ContactTypeEnum.valueOf(request.getType()) : null;

        return new ContactSearchQueryDTO(
            tenantId,
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

