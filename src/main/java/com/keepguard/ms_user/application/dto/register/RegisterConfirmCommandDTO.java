package com.keepguard.ms_user.application.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterConfirmCommandDTO(
    @NotNull(message = "tenantId é obrigatório")
    UUID tenantId,

    @NotNull(message = "registrationSessionId é obrigatório")
    UUID registrationSessionId,

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    String email,

    String token,
    String emailToken,
    String smsToken,
    String whatsAppToken
) {
    public RegisterConfirmCommandDTO(UUID tenantId, UUID registrationSessionId, String email, String token) {
        this(tenantId, registrationSessionId, email, token, token, null, null);
    }
}
