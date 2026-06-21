package com.keepguard.ms_user.application.dto.address;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;

import java.util.Optional;
import java.util.UUID;

public record AddressUpdateCommandDTO(
    UUID id,
    UUID xApplication,
    Optional<String> street,
    Optional<String> number,
    Optional<String> complement,
    Optional<String> neighborhood,
    Optional<String> city,
    Optional<String> state,
    Optional<String> zipCode,
    Optional<String> country,
    Optional<AddressTypeEnum> type,
    Optional<Boolean> primary,
    Optional<Boolean> active
) {}

