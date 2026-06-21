package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.application.dto.address.*;
import com.keepguard.ms_user.domain.entity.Address;
import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class AddressApplicationMapper {

    public Address toDomain(AddressCreateCommandDTO command) {
        return Address.of(
            null,
            command.userId(),
            command.street(),
            command.number(),
            command.complement(),
            command.neighborhood(),
            command.city(),
            command.state(),
            command.zipCode(),
            command.country() != null ? command.country() : "Brasil",
            command.type(),
            command.primary() != null && command.primary(),
            command.active() != null ? command.active() : true,
            null,
            null
        );
    }

    public AddressDetailsViewDTO toDetailsView(Address address) {
        return new AddressDetailsViewDTO(
            address.getId(),
            address.getUserId(),
            address.getStreet(),
            address.getNumber(),
            address.getComplement(),
            address.getNeighborhood(),
            address.getCity(),
            address.getState(),
            address.getFormattedZipCode(),
            address.getCountry(),
            address.getType(),
            address.isPrimary(),
            address.isActive(),
            address.getFullAddress(),
            address.getCreatedAt(),
            address.getUpdatedAt()
        );
    }

    public AddressSearchViewDTO toSearchView(Address address) {
        return new AddressSearchViewDTO(
            address.getId(),
            address.getUserId(),
            address.getStreet(),
            address.getNumber(),
            address.getCity(),
            address.getState(),
            address.getFormattedZipCode(),
            address.getType(),
            address.isPrimary(),
            address.isActive(),
            address.getCreatedAt()
        );
    }

    public AddressSearchCriteriaDTO toSearchCriteria(AddressSearchQueryDTO query) {
        return new AddressSearchCriteriaDTO(
            query.userId(),
            query.city(),
            query.state(),
            query.zipCode(),
            query.type(),
            query.primary(),
            query.active(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );
    }

    public void updateDomain(Address address, AddressUpdateCommandDTO command) {
        command.street().ifPresent(address::setStreet);
        command.number().ifPresent(address::setNumber);
        command.complement().ifPresent(address::setComplement);
        command.neighborhood().ifPresent(address::setNeighborhood);
        command.city().ifPresent(address::setCity);
        command.state().ifPresent(address::setState);
        command.zipCode().ifPresent(address::setZipCode);
        command.country().ifPresent(address::setCountry);
        command.type().ifPresent(address::setType);
        command.primary().ifPresent(address::setPrimary);
        command.active().ifPresent(address::setActive);
    }
}

