package com.keepguard.ms_user.adapters.in.rest.register.dto.response;

/**
 * DTO de resposta para reenvio de token de registro.
 */
public record RegisterResendResponseDTO(
    String message,
    String email,
    String nameFull,
    String token,
    String registrationSessionId,
    int resendAttemptsRemaining,
    int expiresIn
) {}
