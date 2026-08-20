package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserDeleteCommandDTO(
    @NotNull(message = "id é obrigatório")
    UUID id,
    
    @NotNull(message = "tenantId é obrigatório")
    UUID tenantId
) {
}
