package com.keepguard.ms_user.domain.entity;

import com.keepguard.lib_common.exception.ValidationException;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.domain.validator.LocaleValidator;
import com.keepguard.ms_user.domain.validator.PhoneValidator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class User {

    private final UUID id;
    private UUID codeUser;
    private UUID companyId;
    private UUID xApplication;
    private UserTypeEnum type;
    private UserStatusEnum status;
    private String email;
    private String phoneE164;
    private String preferredLocale;
    private String timezone;
    private String avatarUrl;
    private String displayHandle;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private User(UUID id, UUID codeUser, UUID companyId, UUID xApplication, UserTypeEnum type, UserStatusEnum status,
                String email, String phoneE164, String preferredLocale, String timezone,
                String avatarUrl, String displayHandle, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.codeUser = codeUser;
        this.companyId = validateCompanyId(companyId);
        this.xApplication = validateXApplication(xApplication);
        this.type = Objects.requireNonNullElse(type, UserTypeEnum.PERSON);
        this.status = Objects.requireNonNullElse(status, UserStatusEnum.PENDING);
        this.email = validateEmail(email);
        this.preferredLocale = LocaleValidator.validate(preferredLocale);
        this.phoneE164 = PhoneValidator.validate(phoneE164, this.preferredLocale);
        this.timezone = timezone;
        this.avatarUrl = avatarUrl;
        this.displayHandle = validateDisplayHandle(displayHandle);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private static String validateDisplayHandle(String displayHandle) {
        if (displayHandle == null || displayHandle.trim().isEmpty()) {
            return null;
        }
        String normalized = displayHandle.trim().toLowerCase();
        if (!normalized.matches("^[a-z0-9._-]{3,64}$")) {
            throw new ValidationException("display_handle deve conter apenas letras minúsculas, números, pontos, underscores e hífens (3-64 caracteres)");
        }
        return normalized;
    }

    public static User create(UUID codeUser, UUID companyId, UUID xApplication, UserTypeEnum type, String email,
                             String phoneE164, String preferredLocale, String timezone, String avatarUrl) {
        return new User(null, codeUser, companyId, xApplication, type, UserStatusEnum.PENDING,
                       email, phoneE164, preferredLocale, timezone, avatarUrl, null,
                       OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static User of(UUID id, UUID codeUser, UUID companyId, UUID xApplication, UserTypeEnum type, UserStatusEnum status,
                         String email, String phoneE164, String preferredLocale, String timezone,
                         String avatarUrl, String displayHandle, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new User(id, codeUser, companyId, xApplication, type, status, email, phoneE164,
                       preferredLocale, timezone, avatarUrl, displayHandle, createdAt, updatedAt);
    }

    private UUID validateCompanyId(UUID companyId) {
        if (companyId == null) {
            throw new ValidationException("companyId é obrigatório");
        }
        return companyId;
    }

    private UUID validateXApplication(UUID xApplication) {
        if (xApplication == null) {
            throw new ValidationException("xApplication é obrigatório");
        }
        return xApplication;
    }

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email é obrigatório");
        }
        String trimmedEmail = email.trim().toLowerCase();
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new ValidationException("Email inválido");
        }
        if (trimmedEmail.length() > 255) {
            throw new ValidationException("Email deve ter no máximo 255 caracteres");
        }
        return trimmedEmail;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCodeUser() { return codeUser; }
    public UUID getCompanyId() { return companyId; }
    public UUID getXApplication() { return xApplication; }
    public UserTypeEnum getType() { return type; }
    public UserStatusEnum getStatus() { return status; }
    public String getEmail() { return email; }
    public String getPhoneE164() { return phoneE164; }
    public String getPreferredLocale() { return preferredLocale; }
    public String getTimezone() { return timezone; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getDisplayHandle() { return displayHandle; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    // Setters para campos mutáveis
    public void setCodeUser(UUID codeUser) {
        this.codeUser = codeUser;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = validateCompanyId(companyId);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setXApplication(UUID xApplication) {
        this.xApplication = validateXApplication(xApplication);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setType(UserTypeEnum type) {
        this.type = Objects.requireNonNull(type, "Tipo de usuário é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setStatus(UserStatusEnum status) {
        this.status = Objects.requireNonNull(status, "Status é obrigatório");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setEmail(String email) {
        this.email = validateEmail(email);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = PhoneValidator.validate(phoneE164, this.preferredLocale);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setPreferredLocale(String preferredLocale) {
        this.preferredLocale = LocaleValidator.validate(preferredLocale);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setDisplayHandle(String displayHandle) {
        this.displayHandle = validateDisplayHandle(displayHandle);
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public void activate() {
        if (this.status == UserStatusEnum.BLOCKED) {
            throw new ValidationException("Não é possível ativar usuário bloqueado");
        }
        this.status = UserStatusEnum.ACTIVE;
        this.updatedAt = OffsetDateTime.now();
    }

    public void deactivate() {
        if (this.status == UserStatusEnum.BLOCKED) {
            throw new ValidationException("Não é possível desativar usuário bloqueado");
        }
        this.status = UserStatusEnum.INACTIVE;
        this.updatedAt = OffsetDateTime.now();
    }

    public void block() {
        this.status = UserStatusEnum.BLOCKED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void suspend() {
        if (this.status == UserStatusEnum.BLOCKED) {
            throw new ValidationException("Não é possível suspender usuário bloqueado");
        }
        this.status = UserStatusEnum.SUSPENDED;
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return UserStatusEnum.ACTIVE.equals(this.status);
    }

    public boolean isBlocked() {
        return UserStatusEnum.BLOCKED.equals(this.status);
    }

    public boolean isSuspended() {
        return UserStatusEnum.SUSPENDED.equals(this.status);
    }

    public boolean canPerformOperations() {
        return isActive() || UserStatusEnum.PENDING.equals(this.status);
    }

    public void validateForOperations() {
        if (!canPerformOperations()) {
            throw new ValidationException(
                "Não é possível realizar operações com usuário no status: " + this.status.getDescription()
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", codeUser=" + codeUser +
                ", companyId=" + companyId +
                ", xApplication=" + xApplication +
                ", type=" + type +
                ", status=" + status +
                ", email='" + email + '\'' +
                '}';
    }
}
