package com.keepguard.ms_user.application.dto.contact;

import java.util.UUID;

public record ContactGetByIdQueryDTO(
    UUID id,
    UUID tenantId
) {}

