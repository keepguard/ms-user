package com.keepguard.ms_user.application.dto.contact;

import java.util.UUID;

public record ContactGetByUserIdQueryDTO(
    UUID userId,
    UUID companyId
) {}

