package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "First-write de CPF no perfil de pessoa física")
public record PersonDocumentPatchRequestDTO(
    @Schema(description = "CPF com 11 dígitos", example = "52998224725")
    @NotBlank(message = "cpf é obrigatório")
    String cpf
) {}
