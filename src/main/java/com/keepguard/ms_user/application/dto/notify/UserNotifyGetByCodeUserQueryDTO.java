package com.keepguard.ms_user.application.dto.notify;

import java.util.UUID;

public record UserNotifyGetByCodeUserQueryDTO(
    UUID codeUser,
    UUID companyId
) {}
