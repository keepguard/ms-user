package com.keepguard.ms_user.adapters.in.rest.register.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados para confirmação do registro de usuário")
public record RegisterConfirmRequestDTO(
    @Schema(description = "ID da sessão de registro", example = "b9a1f7e8-3c92-42a9-ae76-62b8a8e9f812")
    @NotNull(message = "registrationSessionId é obrigatório")
    UUID registrationSessionId,

    @Schema(description = "Email do usuário", example = "rafael@example.com")
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    String email,

    @Schema(description = "Token de verificação geral (ou do e-mail)", example = "123456")
    @Size(min = 6, max = 6, message = "token deve ter exatamente 6 dígitos")
    String token,

    @Schema(description = "Token de verificação do E-mail", example = "123456")
    @Size(min = 6, max = 6, message = "emailToken deve ter exatamente 6 dígitos")
    String emailToken,

    @Schema(description = "Token de verificação do SMS", example = "654321")
    @Size(min = 6, max = 6, message = "smsToken deve ter exatamente 6 dígitos")
    String smsToken,

    @Schema(description = "Token de verificação do WhatsApp", example = "987654")
    @Size(min = 6, max = 6, message = "whatsAppToken deve ter exatamente 6 dígitos")
    String whatsAppToken
) {
    public RegisterConfirmRequestDTO(UUID registrationSessionId, String email, String token) {
        this(registrationSessionId, email, token, token, null, null);
    }
}

