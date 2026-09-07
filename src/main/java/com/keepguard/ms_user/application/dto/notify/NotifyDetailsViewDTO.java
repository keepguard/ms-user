package com.keepguard.ms_user.application.dto.notify;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotifyDetailsViewDTO(
    UUID id,
    UUID userId,
    boolean notifyEmail,
    boolean notifySms,
    boolean notifyWhatsapp,
    boolean notifyPush,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Long version
) {}
