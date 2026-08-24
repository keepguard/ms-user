package com.keepguard.ms_user.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "user_notify")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifyJpaEntity implements Persistable<UUID> {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    public NotifyJpaEntity(UUID userId, boolean notifyEmail, boolean notifySms,
                           boolean notifyWhatsapp, boolean notifyPush,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.userId = userId;
        this.notifyEmail = notifyEmail;
        this.notifySms = notifySms;
        this.notifyWhatsapp = notifyWhatsapp;
        this.notifyPush = notifyPush;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isNew = true;
    }

    public NotifyJpaEntity(UUID userId, boolean notifyEmail, boolean notifySms,
                           boolean notifyWhatsapp, boolean notifyPush,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt, Long version) {
        this(userId, notifyEmail, notifySms, notifyWhatsapp, notifyPush, createdAt, updatedAt);
    }

    @Override
    public UUID getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Column(name = "notify_email", nullable = false)
    @Builder.Default
    private boolean notifyEmail = true;

    @Column(name = "notify_sms", nullable = false)
    @Builder.Default
    private boolean notifySms = true;

    @Column(name = "notify_whatsapp", nullable = false)
    @Builder.Default
    private boolean notifyWhatsapp = true;

    @Column(name = "notify_push", nullable = false)
    @Builder.Default
    private boolean notifyPush = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = OffsetDateTime.now();
        }
        if (this.version == null) {
            this.version = 0L;
        }
    }

    // Removido @PreUpdate - atualização manual no domínio
    // @PreUpdate
    // public void preUpdate() {
    //     this.updatedAt = OffsetDateTime.now();
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotifyJpaEntity that = (NotifyJpaEntity) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
