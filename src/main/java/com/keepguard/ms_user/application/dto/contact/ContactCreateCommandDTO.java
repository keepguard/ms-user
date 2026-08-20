package com.keepguard.ms_user.application.dto.contact;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;

import java.util.UUID;

public record ContactCreateCommandDTO(
    UUID userId,
    UUID tenantId,
    String value,
    ContactTypeEnum type,
    String description,
    Boolean primary,
    Boolean active
) {}

