package com.keepguard.ms_user.test.builder;

import com.keepguard.ms_user.application.dto.contact.*;
import com.keepguard.ms_user.domain.entity.Contact;
import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.ContactJpaEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Builder para testes de Contact
 */
public class ContactTestBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID userId = UUID.randomUUID();
    private UUID tenantId = UUID.randomUUID();
    private String value = "11999999999";
    private ContactTypeEnum type = ContactTypeEnum.MOBILE;
    private String description = "Celular pessoal";
    private Boolean primary = true;
    private Boolean active = true;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    
    private ContactTestBuilder() {}
    
    public static ContactTestBuilder builder() {
        return new ContactTestBuilder();
    }
    
    public static ContactTestBuilder aContact() {
        return new ContactTestBuilder();
    }
    
    public ContactTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    
    public ContactTestBuilder withUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
    
    public ContactTestBuilder withTenantId(UUID tenantId) {
        this.tenantId = tenantId;
        return this;
    }
    
    public ContactTestBuilder withValue(String value) {
        this.value = value;
        return this;
    }
    
    public ContactTestBuilder withType(ContactTypeEnum type) {
        this.type = type;
        return this;
    }
    
    public ContactTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ContactTestBuilder withPrimary(Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public ContactTestBuilder withActive(Boolean active) {
        this.active = active;
        return this;
    }
    
    public ContactTestBuilder asMobile() {
        this.type = ContactTypeEnum.MOBILE;
        this.value = "11999999999";
        return this;
    }
    
    public ContactTestBuilder asPhone() {
        this.type = ContactTypeEnum.PHONE;
        this.value = "1133334444";
        return this;
    }
    
    public ContactTestBuilder asWhatsApp() {
        this.type = ContactTypeEnum.WHATSAPP;
        this.value = "11999999999";
        return this;
    }
    
    public ContactTestBuilder asTelegram() {
        this.type = ContactTypeEnum.TELEGRAM;
        this.value = "@user";
        return this;
    }
    
    public ContactTestBuilder asSkype() {
        this.type = ContactTypeEnum.SKYPE;
        this.value = "user.skype";
        return this;
    }
    
    public ContactTestBuilder asPrimary() {
        this.primary = true;
        return this;
    }
    
    public ContactTestBuilder asSecondary() {
        this.primary = false;
        return this;
    }
    
    public ContactTestBuilder asActive() {
        this.active = true;
        return this;
    }
    
    public ContactTestBuilder asInactive() {
        this.active = false;
        return this;
    }
    
    public Contact buildDomain() {
        return Contact.of(
                id,
                userId,
                value,
                type,
                primary,
                active,
                description,
                createdAt,
                updatedAt
        );
    }
    
    public ContactCreateCommandDTO buildCreateCommand() {
        return new ContactCreateCommandDTO(
                userId,
                tenantId,
                value,
                type,
                description,
                primary,
                active
        );
    }
    
    public ContactUpdateCommandDTO buildUpdateCommand() {
        return new ContactUpdateCommandDTO(
                id,
                tenantId,
                Optional.of(value),
                Optional.of(type),
                Optional.of(description),
                Optional.of(primary),
                Optional.of(active)
        );
    }
    
    public ContactDeleteCommandDTO buildDeleteCommand() {
        return new ContactDeleteCommandDTO(id, tenantId);
    }
    
    public ContactGetByIdQueryDTO buildGetByIdQuery() {
        return new ContactGetByIdQueryDTO(id, tenantId);
    }
    
    public ContactGetByUserIdQueryDTO buildGetByUserIdQuery() {
        return new ContactGetByUserIdQueryDTO(userId, tenantId);
    }
    
    public ContactSearchQueryDTO buildSearchQuery() {
        return new ContactSearchQueryDTO(
                tenantId,
                userId,
                value,
                type,
                primary,
                active,
                0,
                20,
                List.of("createdAt"),
                "DESC"
        );
    }
    
    public ContactDetailsViewDTO buildDetailsView() {
        return new ContactDetailsViewDTO(
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
    
    public ContactSearchViewDTO buildSearchView() {
        return new ContactSearchViewDTO(
                id,
                userId,
                value,
                type,
                primary,
                active,
                createdAt
        );
    }
    
    public ContactJpaEntity buildJpaEntity() {
        return ContactJpaEntity.builder()
                .id(id)
                .userId(userId)
                .value(value)
                .type(type)
                .description(description)
                .primary(primary)
                .active(active)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}

