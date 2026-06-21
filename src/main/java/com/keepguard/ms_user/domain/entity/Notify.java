package com.keepguard.ms_user.domain.entity;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Notify {

    private final UUID userId;
    private boolean notifyEmail;
    private boolean notifySms;
    private boolean notifyWhatsapp;
    private boolean notifyPush;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;

    private Notify(UUID userId, boolean notifyEmail, boolean notifySms,
                   boolean notifyWhatsapp, boolean notifyPush,
                   OffsetDateTime createdAt, OffsetDateTime updatedAt, Long version) {
        this.userId = Objects.requireNonNull(userId, "userId é obrigatório");
        this.notifyEmail = notifyEmail;
        this.notifySms = notifySms;
        this.notifyWhatsapp = notifyWhatsapp;
        this.notifyPush = notifyPush;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Notify createDefaults(UUID userId) {
        return new Notify(userId, true, true, true, true,
                         OffsetDateTime.now(), OffsetDateTime.now(), 0L);
    }

    public static Notify of(UUID userId, boolean notifyEmail, boolean notifySms,
                           boolean notifyWhatsapp, boolean notifyPush,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt, Long version) {
        return new Notify(userId, notifyEmail, notifySms, notifyWhatsapp, notifyPush,
                         createdAt, updatedAt, version);
    }

    // Getters
    public UUID getUserId() { return userId; }
    public boolean isNotifyEmail() { return notifyEmail; }
    public boolean isNotifySms() { return notifySms; }
    public boolean isNotifyWhatsapp() { return notifyWhatsapp; }
    public boolean isNotifyPush() { return notifyPush; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    // Setters
    public void setNotifyEmail(boolean notifyEmail) {
        this.notifyEmail = notifyEmail;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setNotifySms(boolean notifySms) {
        this.notifySms = notifySms;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setNotifyWhatsapp(boolean notifyWhatsapp) {
        this.notifyWhatsapp = notifyWhatsapp;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setNotifyPush(boolean notifyPush) {
        this.notifyPush = notifyPush;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Business methods
    public void enableAllNotifications() {
        this.notifyEmail = true;
        this.notifySms = true;
        this.notifyWhatsapp = true;
        this.notifyPush = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void disableAllNotifications() {
        this.notifyEmail = false;
        this.notifySms = false;
        this.notifyWhatsapp = false;
        this.notifyPush = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean hasAnyNotificationEnabled() {
        return notifyEmail || notifySms || notifyWhatsapp || notifyPush;
    }

    public boolean hasAllNotificationsEnabled() {
        return notifyEmail && notifySms && notifyWhatsapp && notifyPush;
    }

    public void updateNotificationPreferences(boolean email, boolean sms, boolean whatsapp, boolean push) {
        this.notifyEmail = email;
        this.notifySms = sms;
        this.notifyWhatsapp = whatsapp;
        this.notifyPush = push;
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notify notify = (Notify) o;
        return Objects.equals(userId, notify.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "Notify{" +
                "userId=" + userId +
                ", notifyEmail=" + notifyEmail +
                ", notifySms=" + notifySms +
                ", notifyWhatsapp=" + notifyWhatsapp +
                ", notifyPush=" + notifyPush +
                '}';
    }
}
