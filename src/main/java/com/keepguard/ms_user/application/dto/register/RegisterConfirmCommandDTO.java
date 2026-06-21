package com.keepguard.ms_user.application.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterConfirmCommandDTO(
    @NotNull(message = "xApplication é obrigatório")
    UUID xApplication,

    @NotNull(message = "registrationSessionId é obrigatório")
    UUID registrationSessionId,

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    String email,

    @NotBlank(message = "token é obrigatório")
    @Size(min = 6, max = 6, message = "token deve ter exatamente 6 dígitos")
    String token
) {}

