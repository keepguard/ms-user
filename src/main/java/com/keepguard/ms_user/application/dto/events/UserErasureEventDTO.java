package com.keepguard.ms_user.application.dto.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserErasureEventDTO(
    UUID userId,
    UUID companyId,
    OffsetDateTime requestedAt
) {}
