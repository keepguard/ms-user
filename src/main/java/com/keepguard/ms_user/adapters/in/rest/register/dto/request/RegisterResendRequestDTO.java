package com.keepguard.ms_user.adapters.in.rest.register.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de requisição para reenvio de token de registro.
 */
public record RegisterResendRequestDTO(
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    String email,
    
    @NotNull(message = "registrationSessionId é obrigatório")
    UUID registrationSessionId
) {}
