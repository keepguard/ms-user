package com.keepguard.ms_user.application.dto.address;

import java.util.UUID;

public record AddressGetByUserIdQueryDTO(
    UUID userId,
    UUID xApplication
) {}

