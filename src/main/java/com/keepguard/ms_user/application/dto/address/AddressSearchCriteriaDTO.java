package com.keepguard.ms_user.application.dto.address;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;

import java.util.List;
import java.util.UUID;

public record AddressSearchCriteriaDTO(
    UUID userId,
    String city,
    String state,
    String zipCode,
    AddressTypeEnum type,
    Boolean primary,
    Boolean active,
    Integer page,
    Integer size,
    List<String> sortFields,
    String sortDirection
) {}

