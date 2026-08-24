package com.keepguard.ms_user.application.dto.register;

import java.util.UUID;

public record RegisterInitViewDTO(
    UUID registrationSessionId,
    String email,
    Integer expiresIn,
    String message,
    String token,
    String emailToken,
    String smsToken,
    String whatsAppToken
) {
    public RegisterInitViewDTO(UUID registrationSessionId, String email, Integer expiresIn, String message, String token) {
        this(registrationSessionId, email, expiresIn, message, token, token, null, null);
    }
}

