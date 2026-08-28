package com.keepguard.ms_user.application.dto.notify;

import java.util.UUID;

public record UserNotifyGetByUserIdQueryDTO(
    UUID userId,
    UUID companyId
) {}
