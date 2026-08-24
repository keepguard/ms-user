package com.keepguard.ms_user.infrastructure.persistence.entity;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew || createdAt == null;
    }

    @Column(name = "code_user", nullable = false)
    private UUID codeUser;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "tenant_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatusEnum status;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "phone_e164", length = 20)
    private String phoneE164;

    @Column(name = "preferred_locale", length = 10)
    private String preferredLocale;

    @Column(length = 64)
    private String timezone;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "display_handle", length = 64)
    private String displayHandle;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
