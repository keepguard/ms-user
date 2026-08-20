package com.keepguard.ms_user.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UserBatchStatusCommandDTO(
    @NotEmpty(message = "userIds é obrigatório")
    List<UUID> userIds,
    
    @NotNull(message = "tenantId é obrigatório")
    UUID tenantId,
    
    @NotBlank(message = "reason é obrigatório")
    String reason
) {
}
