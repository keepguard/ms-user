package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Dados para operações em lote de status de usuários")
public record UserBatchStatusRequestDTO(
    @Schema(description = "Lista de IDs dos usuários", example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"456e7890-e89b-12d3-a456-426614174000\"]")
    @NotEmpty(message = "Lista de usuários é obrigatória")
    List<UUID> userIds,

    @Schema(description = "Motivo da mudança de status", example = "Aprovação em lote após verificação")
    @NotBlank(message = "Motivo é obrigatório")
    @Size(max = 500, message = "Motivo deve ter no máximo 500 caracteres")
    String reason
) {}
