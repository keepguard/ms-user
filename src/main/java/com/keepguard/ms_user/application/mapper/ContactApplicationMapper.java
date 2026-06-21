package com.keepguard.ms_user.application.mapper;

import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.domain.entity.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactApplicationMapper {

    public Contact toDomain(ContactCreateCommandDTO command) {
        return Contact.of(
            null,
            command.userId(),
            command.value(),
            command.type(),
            command.primary() != null && command.primary(),
            command.active() != null ? command.active() : true,
            command.description(),
            null,
            null
        );
    }

    public ContactDetailsViewDTO toDetailsView(Contact contact) {
        return new ContactDetailsViewDTO(
            contact.getId(),
            contact.getUserId(),
            contact.getValue(),
            contact.getType(),
            contact.getDescription(),
            contact.isPrimary(),
            contact.isActive(),
            contact.getCreatedAt(),
            contact.getUpdatedAt()
        );
    }

    public ContactSearchViewDTO toSearchView(Contact contact) {
        return new ContactSearchViewDTO(
            contact.getId(),
            contact.getUserId(),
            contact.getValue(),
            contact.getType(),
            contact.isPrimary(),
            contact.isActive(),
            contact.getCreatedAt()
        );
    }

    public ContactSearchCriteriaDTO toSearchCriteria(ContactSearchQueryDTO query) {
        return new ContactSearchCriteriaDTO(
            query.userId(),
            query.value(),
            query.type(),
            query.primary(),
            query.active(),
            query.page(),
            query.size(),
            query.sortFields(),
            query.sortDirection()
        );
    }

    public void updateDomain(Contact contact, ContactUpdateCommandDTO command) {
        command.value().ifPresent(contact::setValue);
        command.type().ifPresent(contact::setType);
        command.description().ifPresent(contact::setDescription);
        command.primary().ifPresent(contact::setPrimary);
        command.active().ifPresent(contact::setActive);
    }
}

