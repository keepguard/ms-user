package com.keepguard.ms_user.application.dto.contact;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;

import java.util.List;
import java.util.UUID;

public record ContactSearchQueryDTO(
    UUID tenantId,
    UUID userId,
    String value,
    ContactTypeEnum type,
    Boolean primary,
    Boolean active,
    Integer page,
    Integer size,
    List<String> sortFields,
    String sortDirection
) {}

