package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import com.keepguard.ms_user.domain.enums.GenderEnum;
import com.keepguard.ms_user.domain.enums.IncomeRangeEnum;
import com.keepguard.ms_user.domain.enums.KycLevelEnum;
import com.keepguard.ms_user.domain.enums.KycStatusEnum;
import com.keepguard.ms_user.domain.enums.MaritalStatusEnum;
import com.keepguard.lib_validation.moderation.application.validator.ModeratedContent;
import static com.keepguard.lib_validation.moderation.domain.model.ModerationCategory.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Dados do perfil de pessoa física")
public record PersonRequestDTO(
    @Schema(description = "Nome completo", example = "João da Silva")
    @JsonProperty("full_name")
    @JsonAlias({"fullName", "full_name"})
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 200, message = "Nome completo deve ter no máximo 200 caracteres")
    @ModeratedContent(categories = {HATE, HARASSMENT, SEXUAL}, threshold = 0.15)
    String fullName,

    @Schema(description = "Apelido (display_handle) do usuário, gravado em users", example = "joao.silva")
    @JsonProperty("display_handle")
    @Size(min = 3, max = 64, message = "display_handle deve ter entre 3 e 64 caracteres")
    String displayHandle,

    @Schema(description = "CPF (opcional, mas se informado será validado)", example = "12345678909")
    @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres")
    String cpf,

    @Schema(description = "RG", example = "123456789")
    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    String rg,

    @Schema(description = "Órgão emissor do RG", example = "SSP")
    @Size(max = 20, message = "Órgão emissor deve ter no máximo 20 caracteres")
    @JsonProperty("rg_issuer")
    String rgIssuer,

    @Schema(description = "Estado emissor do RG", example = "SP")
    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres")
    @JsonProperty("rg_state")
    String rgState,

    @Schema(description = "Data de nascimento", example = "1990-05-10")
    @JsonProperty("date_of_birth")
    @Past(message = "Data de nascimento deve ser no passado")
    LocalDate dateOfBirth,

    @Schema(description = "Gênero", example = "MALE")
    GenderEnum gender,

    @Schema(description = "Estado civil", example = "SINGLE")
    @JsonProperty("marital_status")
    MaritalStatusEnum maritalStatus,

    @Schema(description = "Nacionalidade", example = "Brasileira")
    @Size(max = 50, message = "Nacionalidade deve ter no máximo 50 caracteres")
    String nationality,

    @Schema(description = "País de nascimento", example = "Brasil")
    @Size(max = 50, message = "País de nascimento deve ter no máximo 50 caracteres")
    @JsonProperty("birth_country")
    String birthCountry,

    @Schema(description = "Estado de nascimento", example = "SP")
    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres")
    @JsonProperty("birth_state")
    String birthState,

    @Schema(description = "Cidade de nascimento", example = "São Paulo")
    @Size(max = 100, message = "Cidade de nascimento deve ter no máximo 100 caracteres")
    @JsonProperty("birth_city")
    String birthCity,

    @Schema(description = "Nome da mãe", example = "Maria da Silva")
    @Size(max = 200, message = "Nome da mãe deve ter no máximo 200 caracteres")
    @JsonProperty("mother_name")
    String motherName,

    @Schema(description = "Nome do pai", example = "José da Silva")
    @Size(max = 200, message = "Nome do pai deve ter no máximo 200 caracteres")
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
    @Size(max = 100, message = "Ocupação deve ter no máximo 100 caracteres")
    String occupation,

    @Schema(description = "Faixa de renda", example = "FROM_2_TO_5_MINIMUM_WAGES")
    @JsonProperty("income_range")
    IncomeRangeEnum incomeRange
) {}
