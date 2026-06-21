package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserDeleteCommandDTO(
    @NotNull(message = "id é obrigatório")
    UUID id,
    
    @NotNull(message = "xApplication é obrigatório")
    UUID xApplication
) {
}
