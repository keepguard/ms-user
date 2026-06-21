package com.keepguard.ms_user.application.dto.register;

import java.util.UUID;

public record RegisterInitViewDTO(
    UUID registrationSessionId,
    String email,
    Integer expiresIn,
    String message,
    String token
) {}

