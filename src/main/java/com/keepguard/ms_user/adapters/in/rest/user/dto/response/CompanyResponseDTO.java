package com.keepguard.ms_user.adapters.in.rest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de visualização do perfil de pessoa jurídica")
public record CompanyResponseDTO(
    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_id")
    UUID userId,

    @Schema(description = "ID da empresa", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("company_id")
    UUID companyId,

    @Schema(description = "Razão social snapshot", example = "Empresa Exemplo Ltda")
    @JsonProperty("legal_name_snapshot")
    String legalNameSnapshot,

    @Schema(description = "CNPJ snapshot", example = "12.345.678/0001-95")
    @JsonProperty("cnpj_snapshot")
    String cnpjSnapshot,

    @Schema(description = "Inscrição estadual snapshot", example = "123456789")
    @JsonProperty("state_registration_snapshot")
    String stateRegistrationSnapshot,

    @Schema(description = "Inscrição municipal snapshot", example = "987654321")
    @JsonProperty("municipal_registration_snapshot")
    String municipalRegistrationSnapshot,

    @Schema(description = "Nome do representante", example = "João da Silva")
    @JsonProperty("representative_name")
    String representativeName,

    @Schema(description = "CPF do representante", example = "123.456.789-09")
    @JsonProperty("representative_cpf")
    String representativeCpf,

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @Schema(description = "Data de atualização", example = "2024-01-15T14:45:00-03:00")
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt
) {}
