package com.keepguard.ms_user.adapters.in.rest.user.dto.response;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de visualização do endereço")
public record AddressResponseDTO(
    @Schema(description = "ID do endereço", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_id")
    UUID userId,

    @Schema(description = "Rua", example = "Rua das Flores")
    String street,

    @Schema(description = "Número", example = "123")
    String number,

    @Schema(description = "Complemento", example = "Apto 45")
    String complement,

    @Schema(description = "Bairro", example = "Centro")
    String neighborhood,

    @Schema(description = "Cidade", example = "São Paulo")
    String city,

    @Schema(description = "Estado (UF)", example = "SP")
    String state,

    @Schema(description = "CEP", example = "01234-567")
    @JsonProperty("zip_code")
    String zipCode,

    @Schema(description = "País", example = "Brasil")
    String country,

    @Schema(description = "Tipo de endereço", example = "RESIDENTIAL")
    AddressTypeEnum type,

    @Schema(description = "É endereço primário", example = "false")
    @JsonProperty("is_primary")
    boolean primary,

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    boolean active,

    @Schema(description = "Endereço completo formatado", example = "Rua das Flores, 123, Apto 45 - Centro - São Paulo/SP - 01234-567")
    @JsonProperty("full_address")
    String fullAddress,

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @Schema(description = "Data de atualização", example = "2024-01-15T14:45:00-03:00")
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt
) {}
