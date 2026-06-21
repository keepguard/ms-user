package com.keepguard.ms_user.adapters.in.rest.contact.dto.request;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização de um contato existente")
public record ContactUpdateRequestDTO(
    @Schema(description = "Valor do contato", example = "+5511999999999")
    @Size(min = 3, max = 100, message = "Valor do contato deve ter entre 3 e 100 caracteres")
    String value,

    @Schema(description = "Tipo de contato", example = "MOBILE")
    ContactTypeEnum type,

    @Schema(description = "Descrição do contato", example = "Celular pessoal")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    String description,

    @Schema(description = "É contato primário", example = "false")
    @JsonProperty("is_primary")
    Boolean primary,

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    Boolean active
) {}

