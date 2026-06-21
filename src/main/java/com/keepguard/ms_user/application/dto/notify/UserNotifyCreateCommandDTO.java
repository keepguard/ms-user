package com.keepguard.ms_user.application.dto.notify;

import java.util.UUID;

public record UserNotifyCreateCommandDTO(
    UUID userId,
    UUID xApplication,
    Boolean notifyEmail,
    Boolean notifySms,
    Boolean notifyWhatsapp,
    Boolean notifyPush
) {
}
