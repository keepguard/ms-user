package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserGetByEmailQueryDTO(
    @NotBlank(message = "email é obrigatório")
    String email,
    
    @NotNull(message = "xApplication é obrigatório")
    UUID xApplication
) {
}
