package com.keepguard.ms_user.application.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterInitQueryDTO(
    @NotBlank(message = "email é obrigatório")
    String email,

    @NotNull(message = "tenantId é obrigatório")
    UUID tenantId
) {}

