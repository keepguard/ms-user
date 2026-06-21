package com.keepguard.ms_user.application.dto.address;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AddressDetailsViewDTO(
    UUID id,
    UUID userId,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode,
    String country,
    AddressTypeEnum type,
    boolean primary,
    boolean active,
    String fullAddress,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

