package com.keepguard.ms_user.application.dto.notify;

import java.util.UUID;

public record NotifySimpleViewDTO(
    UUID userId,
    boolean notifyEmail,
    boolean notifySms,
    boolean notifyWhatsapp,
    boolean notifyPush
) {}
