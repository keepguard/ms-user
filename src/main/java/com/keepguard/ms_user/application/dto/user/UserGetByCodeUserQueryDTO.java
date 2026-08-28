package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserGetByCodeUserQueryDTO(
    @NotNull(message = "codeUser é obrigatório")
    UUID codeUser,
    
    @NotNull(message = "companyId é obrigatório")
    UUID companyId
) {
}
