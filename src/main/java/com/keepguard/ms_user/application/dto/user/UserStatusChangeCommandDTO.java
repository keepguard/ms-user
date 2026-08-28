package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserStatusChangeCommandDTO(
    @NotNull(message = "id é obrigatório")
    UUID id,
    
    @NotNull(message = "companyId é obrigatório")
    UUID companyId,
    
    @NotBlank(message = "reason é obrigatório")
    String reason
) {
}
