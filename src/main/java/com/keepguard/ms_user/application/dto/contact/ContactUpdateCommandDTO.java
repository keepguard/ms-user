package com.keepguard.ms_user.application.dto.contact;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;

import java.util.Optional;
import java.util.UUID;

public record ContactUpdateCommandDTO(
    UUID id,
    UUID tenantId,
    Optional<String> value,
    Optional<ContactTypeEnum> type,
    Optional<String> description,
    Optional<Boolean> primary,
    Optional<Boolean> active
) {}

