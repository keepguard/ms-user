package com.keepguard.ms_user.application.dto.contact;

import java.util.UUID;

public record ContactDeleteCommandDTO(
    UUID id,
    UUID tenantId
) {}

