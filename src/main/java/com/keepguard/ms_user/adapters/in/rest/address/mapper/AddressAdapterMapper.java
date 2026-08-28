package com.keepguard.ms_user.adapters.in.rest.address.mapper;

import com.keepguard.ms_user.adapters.in.rest.address.dto.request.*;
import com.keepguard.ms_user.adapters.in.rest.address.dto.response.AddressDetailsResponseDTO;
import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class AddressAdapterMapper {

    // === Command Methods ===

    public AddressCreateCommandDTO toCreateCommand(AddressCreateRequestDTO request, UUID companyId) {
        return new AddressCreateCommandDTO(
            request.userId(),
            companyId,
            request.street(),
            request.number(),
            request.complement(),
            request.neighborhood(),
            request.city(),
            request.state(),
            request.zipCode(),
            request.country(),
            request.type(),
            request.primary(),
            request.active()
        );
    }

    public AddressUpdateCommandDTO toUpdateCommand(AddressUpdateRequestDTO request, UUID id, UUID companyId) {
        return new AddressUpdateCommandDTO(
            id,
            companyId,
            Optional.ofNullable(request.street()),
            Optional.ofNullable(request.number()),
            Optional.ofNullable(request.complement()),
            Optional.ofNullable(request.neighborhood()),
            Optional.ofNullable(request.city()),
            Optional.ofNullable(request.state()),
            Optional.ofNullable(request.zipCode()),
            Optional.ofNullable(request.country()),
            Optional.ofNullable(request.type()),
            Optional.ofNullable(request.primary()),
            Optional.ofNullable(request.active())
        );
    }

    public AddressDeleteCommandDTO toDeleteCommand(UUID id, UUID companyId) {
        return new AddressDeleteCommandDTO(id, companyId);
    }

    // === Query Methods ===

    public AddressGetByIdQueryDTO toGetByIdQuery(UUID id, UUID companyId) {
        return new AddressGetByIdQueryDTO(id, companyId);
    }

    public AddressGetByUserIdQueryDTO toGetByUserIdQuery(UUID userId, UUID companyId) {
        return new AddressGetByUserIdQueryDTO(userId, companyId);
    }

    public AddressSearchQueryDTO toSearchQuery(AddressSearchRequestDTO request, UUID companyId, UUID userId) {
        var addressType = request.getType() != null ? AddressTypeEnum.valueOf(request.getType()) : null;

        return new AddressSearchQueryDTO(
            companyId,
            userId,
            request.getCity(),
            request.getState(),
            request.getZipCode(),
            addressType,
            request.getPrimary(),
            request.getActive(),
            request.getPage() != null ? request.getPage() : 0,
            request.getSize() != null ? request.getSize() : 20,
            request.getSort(),
            request.getDirection() != null ? request.getDirection() : "ASC"
        );
    }

    // === Response Methods ===

    public AddressDetailsResponseDTO toResponseDTO(AddressDetailsViewDTO view) {
        if (view == null) {
            return null;
        }

        return AddressDetailsResponseDTO.builder()
            .id(view.id())
            .userId(view.userId())
            .street(view.street())
            .number(view.number())
            .complement(view.complement())
            .neighborhood(view.neighborhood())
            .city(view.city())
            .state(view.state())
            .zipCode(view.zipCode())
            .country(view.country())
            .type(view.type())
            .primary(view.primary())
            .active(view.active())
            .fullAddress(view.fullAddress())
            .createdAt(view.createdAt())
            .updatedAt(view.updatedAt())
            .build();
    }

    public AddressDetailsResponseDTO toSearchResponseDTO(AddressSearchViewDTO view) {
        if (view == null) {
            return null;
        }

        return AddressDetailsResponseDTO.builder()
            .id(view.id())
            .userId(view.userId())
            .street(view.street())
            .number(view.number())
            .city(view.city())
            .state(view.state())
            .zipCode(view.zipCode())
            .type(view.type())
            .primary(view.primary())
            .active(view.active())
            .createdAt(view.createdAt())
            .build();
    }
}

