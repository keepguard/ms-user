package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para mudança de status de um usuário")
public record UserStatusChangeRequestDTO(
    @Schema(description = "Motivo da mudança de status", example = "Usuário aprovado após verificação")
    @NotBlank(message = "Motivo é obrigatório")
    @Size(max = 500, message = "Motivo deve ter no máximo 500 caracteres")
    String reason
) {}
