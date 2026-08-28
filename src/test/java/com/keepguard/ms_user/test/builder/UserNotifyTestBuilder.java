package com.keepguard.ms_user.test.builder;

import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyCreateRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request.UserNotifyPatchRequestDTO;
import com.keepguard.ms_user.adapters.in.rest.usernotify.dto.response.UserNotifyResponseDTO;
import com.keepguard.ms_user.application.dto.notify.*;
import com.keepguard.ms_user.domain.entity.Notify;
import com.keepguard.ms_user.infrastructure.persistence.entity.NotifyJpaEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Builder para testes de UserNotify
 */
public class UserNotifyTestBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID userId = UUID.randomUUID();
    private UUID codeUser = UUID.randomUUID();
    private UUID companyId = UUID.randomUUID();
    private Boolean notifyEmail = true;
    private Boolean notifySms = false;
    private Boolean notifyWhatsapp = true;
    private Boolean notifyPush = true;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    private Long version = 1L;
    
    private UserNotifyTestBuilder() {}
    
    public static UserNotifyTestBuilder builder() {
        return new UserNotifyTestBuilder();
    }
    
    public static UserNotifyTestBuilder aUserNotify() {
        return new UserNotifyTestBuilder();
    }
    
    public UserNotifyTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    
    public UserNotifyTestBuilder withUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
    
    public UserNotifyTestBuilder withCodeUser(UUID codeUser) {
        this.codeUser = codeUser;
        return this;
    }
    
    public UserNotifyTestBuilder withTenantId(UUID companyId) {
        this.companyId = companyId;
        return this;
    }
    
    public UserNotifyTestBuilder withNotifyEmail(Boolean notifyEmail) {
        this.notifyEmail = notifyEmail;
        return this;
    }
    
    public UserNotifyTestBuilder withNotifySms(Boolean notifySms) {
        this.notifySms = notifySms;
        return this;
    }
    
    public UserNotifyTestBuilder withNotifyWhatsapp(Boolean notifyWhatsapp) {
        this.notifyWhatsapp = notifyWhatsapp;
        return this;
    }
    
    public UserNotifyTestBuilder withNotifyPush(Boolean notifyPush) {
        this.notifyPush = notifyPush;
        return this;
    }
    
    public UserNotifyTestBuilder withAllEnabled() {
        this.notifyEmail = true;
        this.notifySms = true;
        this.notifyWhatsapp = true;
        this.notifyPush = true;
        return this;
    }
    
    public UserNotifyTestBuilder withAllDisabled() {
        this.notifyEmail = false;
        this.notifySms = false;
        this.notifyWhatsapp = false;
        this.notifyPush = false;
        return this;
    }
    
    public Notify buildDomain() {
        return Notify.of(
                userId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush,
                createdAt,
                updatedAt,
                version
        );
    }
    
    public UserNotifyCreateCommandDTO buildCreateCommand() {
        return new UserNotifyCreateCommandDTO(
                companyId,
                userId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush
        );
    }
    
    public UserNotifyPatchCommandDTO buildPatchCommand() {
        return new UserNotifyPatchCommandDTO(
                userId,
                codeUser,
                companyId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush
        );
    }
    
    public UserNotifyGetByUserIdQueryDTO buildGetByUserIdQuery() {
        return new UserNotifyGetByUserIdQueryDTO(userId, companyId);
    }
    
    public UserNotifyGetByCodeUserQueryDTO buildGetByCodeUserQuery() {
        return new UserNotifyGetByCodeUserQueryDTO(codeUser, companyId);
    }
    
    public NotifyViewDTO buildView() {
        return new NotifyViewDTO(
                id,
                userId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush,
                createdAt,
                updatedAt,
                version
        );
    }
    
    public NotifyDetailsViewDTO buildDetailsView() {
        return new NotifyDetailsViewDTO(
                id,
                userId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush,
                createdAt,
                updatedAt,
                version
        );
    }
    
    public NotifySimpleViewDTO buildSimpleView() {
        return new NotifySimpleViewDTO(
                userId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush
        );
    }
    
    public UserNotifyCreateRequestDTO buildCreateRequest() {
        return new UserNotifyCreateRequestDTO(
                userId,
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush
        );
    }
    
    public UserNotifyPatchRequestDTO buildPatchRequest() {
        return new UserNotifyPatchRequestDTO(
                notifyEmail,
                notifySms,
                notifyWhatsapp,
                notifyPush
        );
    }
    
    public UserNotifyResponseDTO buildResponseDTO() {
        return UserNotifyResponseDTO.builder()
                .id(id)
                .userId(userId)
                .notifyEmail(notifyEmail)
                .notifySms(notifySms)
                .notifyWhatsapp(notifyWhatsapp)
                .notifyPush(notifyPush)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
    
    public NotifyJpaEntity buildJpaEntity() {
        return NotifyJpaEntity.builder()
                .userId(userId)
                .notifyEmail(notifyEmail)
                .notifySms(notifySms)
                .notifyWhatsapp(notifyWhatsapp)
                .notifyPush(notifyPush)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .build();
    }
}
