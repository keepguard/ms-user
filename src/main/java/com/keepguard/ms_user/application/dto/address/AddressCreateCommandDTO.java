package com.keepguard.ms_user.application.dto.address;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;

import java.util.UUID;

public record AddressCreateCommandDTO(
    UUID userId,
    UUID tenantId,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode,
    String country,
    AddressTypeEnum type,
    Boolean primary,
    Boolean active
) {}

