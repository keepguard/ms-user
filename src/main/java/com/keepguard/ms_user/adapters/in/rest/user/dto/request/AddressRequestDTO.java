package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import com.keepguard.ms_user.domain.enums.AddressTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados do endereço")
public record AddressRequestDTO(
    @Schema(description = "Rua", example = "Rua das Flores", required = true)
    @NotBlank(message = "Rua é obrigatória")
    @Size(max = 200, message = "Rua deve ter no máximo 200 caracteres")
    String street,

    @Schema(description = "Número", example = "123", required = true)
    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    String number,

    @Schema(description = "Complemento", example = "Apto 45")
    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    String complement,

    @Schema(description = "Bairro", example = "Centro", required = true)
    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    String neighborhood,

    @Schema(description = "Cidade", example = "São Paulo", required = true)
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    String city,

    @Schema(description = "Estado (UF)", example = "SP", required = true)
    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres")
    String state,

    @Schema(description = "CEP", example = "01234567", required = true)
    @JsonProperty("zip_code")
    @NotBlank(message = "CEP é obrigatório")
    @Size(min = 8, max = 8, message = "CEP deve ter exatamente 8 dígitos")
    String zipCode,

    @Schema(description = "País", example = "Brasil")
    @Size(max = 50, message = "País deve ter no máximo 50 caracteres")
    String country,

    @Schema(description = "Tipo de endereço", example = "RESIDENTIAL", required = true)
    @NotNull(message = "Tipo de endereço é obrigatório")
    AddressTypeEnum type,

    @Schema(description = "É endereço primário", example = "false")
    @JsonProperty("is_primary")
    boolean primary,

    @Schema(description = "Está ativo", example = "true")
    @JsonProperty("is_active")
    boolean active
) {}
