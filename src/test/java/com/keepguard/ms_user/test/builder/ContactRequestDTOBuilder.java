package com.keepguard.ms_user.test.builder;

import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.ContactCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.ContactSearchRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.request.ContactUpdateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.contact.dto.response.ContactDetailsResponseDTO;
import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Builder para DTOs REST de Contact
 */
@Setter
@Accessors(chain = true, fluent = true)
public class ContactRequestDTOBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID userId = UUID.randomUUID();
    private String value = "11999999999";
    private ContactTypeEnum type = ContactTypeEnum.MOBILE;
    private String description = "Celular pessoal";
    private Boolean primary = true;
    private Boolean active = true;
    private Integer page = 0;
    private Integer size = 20;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    
    public static ContactRequestDTOBuilder builder() {
        return new ContactRequestDTOBuilder();
    }
    
    public ContactCreateRequestDTO buildCreateRequest() {
        return new ContactCreateRequestDTO(
                userId,
                value,
                type,
                description,
                primary,
                active
        );
    }
    
    public ContactUpdateRequestDTO buildUpdateRequest() {
        return new ContactUpdateRequestDTO(
                value,
                type,
                description,
                primary,
                active
        );
    }
    
    public ContactSearchRequestDTO buildSearchRequest() {
        ContactSearchRequestDTO dto = new ContactSearchRequestDTO();
        dto.setValue(value);
        dto.setType(type.name());
        dto.setPage(page);
        dto.setSize(size);
        return dto;
    }
    
    public ContactDetailsResponseDTO buildResponse() {
        return new ContactDetailsResponseDTO(
                id,
                userId,
                value,
                type,
                description,
                primary,
                active,
                createdAt,
                updatedAt
        );
    }
}

