package com.keepguard.ms_user.application.dto.notify;

import java.util.UUID;

public record UserNotifyCreateCommandDTO(
    UUID userId,
    UUID tenantId,
    Boolean notifyEmail,
    Boolean notifySms,
    Boolean notifyWhatsapp,
    Boolean notifyPush
) {
}
