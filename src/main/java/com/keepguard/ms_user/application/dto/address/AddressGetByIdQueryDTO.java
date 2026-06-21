package com.keepguard.ms_user.application.dto.address;

import java.util.UUID;

public record AddressGetByIdQueryDTO(
    UUID id,
    UUID xApplication
) {}

