package com.keepguard.ms_user.adapters.in.rest.user.dto.response;

import com.keepguard.ms_user.domain.enums.GenderEnum;
import com.keepguard.ms_user.domain.enums.IncomeRangeEnum;
import com.keepguard.ms_user.domain.enums.KycLevelEnum;
import com.keepguard.ms_user.domain.enums.KycStatusEnum;
import com.keepguard.ms_user.domain.enums.MaritalStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de visualização do perfil de pessoa física")
public record PersonResponseDTO(
    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("user_id")
    UUID userId,

    @Schema(description = "Nome completo", example = "João da Silva")
    @JsonProperty("full_name")
    String fullName,

    @Schema(description = "CPF", example = "123.456.789-09")
    String cpf,

    @Schema(description = "RG", example = "123456789")
    String rg,

    @Schema(description = "Órgão emissor do RG", example = "SSP")
    @JsonProperty("rg_issuer")
    String rgIssuer,

    @Schema(description = "Estado emissor do RG", example = "SP")
    @JsonProperty("rg_state")
    String rgState,

    @Schema(description = "Data de nascimento", example = "1990-05-10")
    @JsonProperty("date_of_birth")
    LocalDate dateOfBirth,

    @Schema(description = "Gênero", example = "MALE")
    GenderEnum gender,

    @Schema(description = "Estado civil", example = "SINGLE")
    @JsonProperty("marital_status")
    MaritalStatusEnum maritalStatus,

    @Schema(description = "Nacionalidade", example = "Brasileira")
    String nationality,

    @Schema(description = "País de nascimento", example = "Brasil")
    @JsonProperty("birth_country")
    String birthCountry,

    @Schema(description = "Estado de nascimento", example = "SP")
    @JsonProperty("birth_state")
    String birthState,

    @Schema(description = "Cidade de nascimento", example = "São Paulo")
    @JsonProperty("birth_city")
    String birthCity,

    @Schema(description = "Nome da mãe", example = "Maria da Silva")
    @JsonProperty("mother_name")
    String motherName,

    @Schema(description = "Nome do pai", example = "José da Silva")
    @JsonProperty("father_name")
    String fatherName,

    @Schema(description = "É pessoa politicamente exposta", example = "false")
    boolean pep,

    @Schema(description = "Status KYC", example = "NOT_STARTED")
    @JsonProperty("kyc_status")
    KycStatusEnum kycStatus,

    @Schema(description = "Nível KYC", example = "BASIC")
    @JsonProperty("kyc_level")
    KycLevelEnum kycLevel,

    @Schema(description = "Ocupação", example = "Engenheiro")
    String occupation,

    @Schema(description = "Faixa de renda", example = "FROM_2_TO_5_MINIMUM_WAGES")
    @JsonProperty("income_range")
    IncomeRangeEnum incomeRange,

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @Schema(description = "Data de atualização", example = "2024-01-15T14:45:00-03:00")
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt
) {}
