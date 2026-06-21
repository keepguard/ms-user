package com.keepguard.ms_user.adapters.in.rest.user.dto.response;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de visualização do contato")
public record ContactResponseDTO(
    @Schema(description = "ID do contato", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_id")
    UUID userId,

    @Schema(description = "Valor do contato", example = "+5511999999999")
    String value,

    @Schema(description = "Tipo de contato", example = "PHONE")
    ContactTypeEnum type,

    @Schema(description = "É contato primário", example = "false")
    @JsonProperty("is_primary")
    boolean primary,

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    boolean active,

    @Schema(description = "Descrição do contato", example = "Telefone principal")
    String description,

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @Schema(description = "Data de atualização", example = "2024-01-15T14:45:00-03:00")
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt
) {}
