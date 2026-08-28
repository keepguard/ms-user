package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserGetByEmailQueryDTO(
    @NotBlank(message = "email é obrigatório")
    String email,
    
    @NotNull(message = "tenantId é obrigatório")
    UUID tenantId,

    @NotNull(message = "companyId é obrigatório")
    UUID companyId
) {
}
