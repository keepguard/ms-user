package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados do contato")
public record ContactRequestDTO(
    @Schema(description = "Valor do contato", example = "+5511999999999", required = true)
    @NotBlank(message = "Valor do contato é obrigatório")
    @Size(max = 100, message = "Valor do contato deve ter no máximo 100 caracteres")
    String value,

    @Schema(description = "Tipo de contato", example = "PHONE", required = true)
    @NotNull(message = "Tipo de contato é obrigatório")
    ContactTypeEnum type,

    @Schema(description = "É contato primário", example = "false")
    @JsonProperty("is_primary")
    boolean primary,

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    boolean active,

    @Schema(description = "Descrição do contato", example = "Telefone principal")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    String description
) {}
