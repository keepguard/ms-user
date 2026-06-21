package com.keepguard.ms_user.application.dto.contact;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContactSearchViewDTO(
    UUID id,
    UUID userId,
    String value,
    ContactTypeEnum type,
    boolean primary,
    boolean active,
    OffsetDateTime createdAt
) {}

