package com.keepguard.ms_user.adapters.in.rest.contact.dto.response;

import com.keepguard.ms_user.domain.enums.ContactTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalhes completos do contato")
public class ContactDetailsResponseDTO {

    @Schema(description = "ID do contato", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_id")
    private UUID userId;

    @Schema(description = "Valor do contato", example = "+5511999999999")
    private String value;

    @Schema(description = "Tipo de contato", example = "MOBILE")
    private ContactTypeEnum type;

    @Schema(description = "Descrição do contato", example = "Celular pessoal")
    private String description;

    @Schema(description = "É contato primário", example = "false")
    @JsonProperty("is_primary")
    private boolean primary;

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    private boolean active;

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @Schema(description = "Data de atualização", example = "2024-01-15T14:45:00-03:00")
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}

