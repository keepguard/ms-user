package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserGetByIdQueryDTO(
    @NotNull(message = "id é obrigatório")
    UUID id,
    
    UUID tenantId,

    @NotNull(message = "companyId é obrigatório")
    UUID companyId
) {
}
