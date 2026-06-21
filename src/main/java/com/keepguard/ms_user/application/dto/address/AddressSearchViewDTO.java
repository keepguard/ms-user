package com.keepguard.ms_user.application.dto.address;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AddressSearchViewDTO(
    UUID id,
    UUID userId,
    String street,
    String number,
    String city,
    String state,
    String zipCode,
    AddressTypeEnum type,
    boolean primary,
    boolean active,
    OffsetDateTime createdAt
) {}

