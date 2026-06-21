package com.keepguard.ms_user.application.dto.register;

import java.util.UUID;

/**
 * DTO de comando para reenvio de token de registro.
 */
public record RegisterResendCommandDTO(
    UUID xApplication,
    String email,
    UUID registrationSessionId
) {}
