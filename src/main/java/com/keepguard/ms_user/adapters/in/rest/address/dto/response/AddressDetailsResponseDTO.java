package com.keepguard.ms_user.adapters.in.rest.address.dto.response;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
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
@Schema(description = "Detalhes completos do endereço")
public class AddressDetailsResponseDTO {

    @Schema(description = "ID do endereço", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_id")
    private UUID userId;

    @Schema(description = "Rua", example = "Rua das Flores")
    private String street;

    @Schema(description = "Número", example = "123")
    private String number;

    @Schema(description = "Complemento", example = "Apto 45")
    private String complement;

    @Schema(description = "Bairro", example = "Centro")
    private String neighborhood;

    @Schema(description = "Cidade", example = "São Paulo")
    private String city;

    @Schema(description = "Estado (UF)", example = "SP")
    private String state;

    @Schema(description = "CEP formatado", example = "01234-567")
    @JsonProperty("zip_code")
    private String zipCode;

    @Schema(description = "País", example = "Brasil")
    private String country;

    @Schema(description = "Tipo de endereço", example = "RESIDENTIAL")
    private AddressTypeEnum type;

    @Schema(description = "É endereço primário", example = "false")
    @JsonProperty("is_primary")
    private boolean primary;

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    private boolean active;

    @Schema(description = "Endereço completo formatado", example = "Rua das Flores, 123, Apto 45 - Centro - São Paulo/SP - 01234-567")
    @JsonProperty("full_address")
    private String fullAddress;

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @Schema(description = "Data de atualização", example = "2024-01-15T14:45:00-03:00")
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}

