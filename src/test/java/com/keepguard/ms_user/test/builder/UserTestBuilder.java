package com.keepguard.ms_user.test.builder;

import com.keepguard.ms_user.adapters.in.rest.user.dto.request.UserCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.user.dto.request.UserUpdateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.user.dto.response.UserResponseDTO;
import com.keepguard.ms_user.application.dto.notify.NotifyViewDTO;
import com.keepguard.ms_user.application.dto.user.*;
import com.keepguard.ms_user.domain.entity.User;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.infrastructure.persistence.entity.UserJpaEntity;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Builder para testes de User
 */
public class UserTestBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID codeUser = UUID.randomUUID();
    private UUID companyId = UUID.randomUUID();
    private UUID tenantId = UUID.randomUUID();
    private UserTypeEnum type = UserTypeEnum.PERSON;
    private UserStatusEnum status = UserStatusEnum.ACTIVE;
    private String email = "test@example.com";
    private String phoneE164 = "+5511999999999";
    private String preferredLocale = "pt-BR";
    private String timezone = "America/Sao_Paulo";
    private String avatarUrl = "https://example.com/avatar.jpg";
    private PersonProfile personProfile = null;
    private CompanyProfile companyProfile = null;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    
    private UserTestBuilder() {}
    
    public static UserTestBuilder builder() {
        return new UserTestBuilder();
    }
    
    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }
    
    public UserTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    
    public UserTestBuilder withCodeUser(UUID codeUser) {
        this.codeUser = codeUser;
        return this;
    }
    
    public UserTestBuilder withCompanyId(UUID companyId) {
        this.companyId = companyId;
        return this;
    }
    
    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestBuilder withPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
        return this;
    }
    
    public UserTestBuilder withPreferredLocale(String preferredLocale) {
        this.preferredLocale = preferredLocale;
        return this;
    }
    
    public UserTestBuilder withTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }
    
    public UserTestBuilder withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }
    
    public UserTestBuilder withStatus(UserStatusEnum status) {
        this.status = status;
        return this;
    }
    
    public UserTestBuilder asPerson() {
        this.type = UserTypeEnum.PERSON;
        return this;
    }
    
    public UserTestBuilder asCompany() {
        this.type = UserTypeEnum.COMPANY;
        return this;
    }
    
    public UserTestBuilder asActive() {
        this.status = UserStatusEnum.ACTIVE;
        return this;
    }
    
    public UserTestBuilder asPending() {
        this.status = UserStatusEnum.PENDING;
        return this;
    }
    
    public UserTestBuilder asInactive() {
        this.status = UserStatusEnum.INACTIVE;
        return this;
    }
    
    public UserTestBuilder asBlocked() {
        this.status = UserStatusEnum.BLOCKED;
        return this;
    }
    
    public UserTestBuilder asSuspended() {
        this.status = UserStatusEnum.SUSPENDED;
        return this;
    }
    
    public User buildDomain() {
        return User.create(
                codeUser,
                companyId,
                tenantId,
                type,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl
        );
    }
    
    public User buildDomainWithId() {
        return User.of(
                id,
                codeUser,
                companyId,
                tenantId,
                type,
                status,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl,
                null, // displayHandle
                createdAt,
                updatedAt
        );
    }
    
    public UserViewDTO buildView() {
        return new UserViewDTO(
                id,
                codeUser,
                companyId,
                type,
                status,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl,
                null, // displayHandle
                null, // personProfile
                null, // companyProfile
                createdAt,
                updatedAt
        );
    }

    public UserDetailsViewDTO buildDetailsView() {
        return new UserDetailsViewDTO(
                id,
                codeUser,
                companyId,
                type,
                status,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl,
                null, // displayHandle
                null, // personProfile
                null, // companyProfile
                createdAt,
                updatedAt
        );
    }

    public UserSimpleViewDTO buildSimpleView() {
        return new UserSimpleViewDTO(
                id,
                codeUser,
                email,
                status,
                type,
                createdAt
        );
    }

    public UserSearchViewDTO buildSearchView() {
        return new UserSearchViewDTO(
                id,
                codeUser,
                companyId,
                email,
                status,
                type,
                avatarUrl,
                null, // displayHandle
                personProfile,
                companyProfile,
                createdAt
        );
    }
    
    public UserCreateCommandDTO buildCreateCommand() {
        return new UserCreateCommandDTO(
                companyId,
                UUID.randomUUID(), // tenantId
                type,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl,
                null, // displayHandle
                null, // personProfile
                null  // companyProfile
        );
    }
    
    public UserUpdateCommandDTO buildUpdateCommand() {
        return new UserUpdateCommandDTO(
                UUID.randomUUID(), // id
                UUID.randomUUID(), // tenantId
                Optional.ofNullable(companyId),
                Optional.ofNullable(codeUser),
                Optional.ofNullable(type),
                Optional.ofNullable(status),
                Optional.ofNullable(email),
                Optional.ofNullable(phoneE164),
                Optional.ofNullable(preferredLocale),
                Optional.ofNullable(timezone),
                Optional.ofNullable(avatarUrl),
                Optional.empty(), // displayHandle
                Optional.empty(), // personProfile
                Optional.empty()  // companyProfile
        );
    }
    
    public UserCreateRequestDTO buildCreateRequest() {
        return new UserCreateRequestDTO(
                companyId,
                type,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl,
                null, // personProfile
                null  // companyProfile
        );
    }
    
    public UserUpdateRequestDTO buildUpdateRequest() {
        return new UserUpdateRequestDTO(
                companyId,
                codeUser,
                type,
                status,
                email,
                phoneE164,
                preferredLocale,
                timezone,
                avatarUrl,
                null, // personProfile
                null  // companyProfile
        );
    }
    
    public UserResponseDTO buildResponseDTO() {
        return UserResponseDTO.builder()
                .id(id)
                .codeUser(codeUser)
                .companyId(companyId)
                .type(type)
                .status(status)
                .email(email)
                .phoneE164(phoneE164)
                .preferredLocale(preferredLocale)
                .timezone(timezone)
                .avatarUrl(avatarUrl)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
    
    public NotifyViewDTO buildNotifyView() {
        return new NotifyViewDTO(
                UUID.randomUUID(),
                id,
                true,
                false,
                true,
                true,
                createdAt,
                updatedAt,
                1L
        );
    }
    
    public UserJpaEntity buildJpaEntity() {
        return UserJpaEntity.builder()
                .id(id)
                .codeUser(codeUser)
                .companyId(companyId)
                .type(type)
                .status(status)
                .email(email)
                .phoneE164(phoneE164)
                .preferredLocale(preferredLocale)
                .timezone(timezone)
                .avatarUrl(avatarUrl)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
    
    public com.keepguard.ms_user.application.dto.user.UserGetByIdQueryDTO buildGetByIdQuery() {
        return new com.keepguard.ms_user.application.dto.user.UserGetByIdQueryDTO(id, tenantId);
    }
    
    public com.keepguard.ms_user.application.dto.user.UserGetByCodeUserQueryDTO buildGetByCodeUserQuery() {
        return new com.keepguard.ms_user.application.dto.user.UserGetByCodeUserQueryDTO(codeUser, tenantId);
    }
    
    public com.keepguard.ms_user.application.dto.user.UserGetByEmailQueryDTO buildGetByEmailQuery() {
        return new com.keepguard.ms_user.application.dto.user.UserGetByEmailQueryDTO(email, tenantId);
    }
    
    public com.keepguard.ms_user.application.dto.user.UserSearchQueryDTO buildSearchQuery() {
        return new com.keepguard.ms_user.application.dto.user.UserSearchQueryDTO(
                tenantId,
                email,
                companyId,
                type,
                status,
                0,
                20,
                null,
                "ASC"
        );
    }
    
    public com.keepguard.ms_user.application.dto.user.UserStatusChangeCommandDTO buildStatusChangeCommand() {
        return new com.keepguard.ms_user.application.dto.user.UserStatusChangeCommandDTO(id, tenantId, "Test reason");
    }
    
    public com.keepguard.ms_user.application.dto.user.UserBatchStatusCommandDTO buildBatchStatusCommand() {
        return new com.keepguard.ms_user.application.dto.user.UserBatchStatusCommandDTO(
                java.util.List.of(id),
                tenantId,
                "Test batch reason"
        );
    }
    
    public com.keepguard.ms_user.application.dto.user.UserDeleteCommandDTO buildDeleteCommand() {
        return new com.keepguard.ms_user.application.dto.user.UserDeleteCommandDTO(id, tenantId);
    }
}