package com.keepguard.ms_user.domain.entity;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.ContactTypeEnum;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Contact {

    private final UUID id;
    private final UUID userId;
    private String value;
    private ContactTypeEnum type;
    private boolean primary;
    private boolean active;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Contact(UUID id, UUID userId, String value, ContactTypeEnum type, boolean primary,
                   boolean active, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.userId = Objects.requireNonNull(userId, "userId é obrigatório");
        this.value = validateValue(value);
        this.type = Objects.requireNonNull(type, "Tipo de contato é obrigatório");
        this.primary = primary;
        this.active = active;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Contact create(UUID userId, String value, ContactTypeEnum type) {
        return new Contact(null, userId, value, type, false, true, null,
                OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static Contact of(UUID id, UUID userId, String value, ContactTypeEnum type, boolean primary,
                           boolean active, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Contact(id, userId, value, type, primary, active, description, createdAt, updatedAt);
    }

    private String validateValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Valor do contato é obrigatório");
        }
        String trimmed = value.trim();
        if (trimmed.length() < 3) {
            throw new ValidationException("Valor do contato deve ter pelo menos 3 caracteres");
        }
        if (trimmed.length() > 100) {
            throw new ValidationException("Valor do contato deve ter no máximo 100 caracteres");
        }
        return trimmed;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getValue() { return value; }
    public ContactTypeEnum getType() { return type; }
    public boolean isPrimary() { return primary; }
    public boolean isActive() { return active; }
    public String getDescription() { return description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    // Setters para campos mutáveis
    public void setValue(String value) {
        this.value = validateValue(value);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setType(ContactTypeEnum type) {
        this.type = Objects.requireNonNull(type, "Tipo de contato é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public void markAsPrimary() {
        this.primary = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markAsSecondary() {
        this.primary = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean isPhone() {
        return type == ContactTypeEnum.PHONE || type == ContactTypeEnum.MOBILE;
    }

    public boolean isWhatsApp() {
        return type == ContactTypeEnum.WHATSAPP;
    }

    public boolean isMessaging() {
        return type == ContactTypeEnum.WHATSAPP || type == ContactTypeEnum.TELEGRAM || type == ContactTypeEnum.SKYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", userId=" + userId +
                ", value='" + value + '\'' +
                ", type=" + type +
                ", primary=" + primary +
                ", active=" + active +
                '}';
    }
}
