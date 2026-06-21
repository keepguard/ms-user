package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.lib_validation.moderation.application.validator.ModeratedContent;
import static com.keepguard.lib_validation.moderation.domain.model.ModerationCategory.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados do perfil de pessoa jurídica")
public record CompanyRequestDTO(
    @Schema(description = "ID da empresa", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @JsonProperty("company_id")
    @NotNull(message = "ID da empresa é obrigatório")
    UUID companyId,

    @Schema(description = "Razão social snapshot", example = "Empresa Exemplo Ltda")
    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    @JsonProperty("legal_name_snapshot")
    @ModeratedContent(categories = {HATE, HARASSMENT, SEXUAL}, threshold = 0.15)
    String legalNameSnapshot,

    @Schema(description = "CNPJ snapshot", example = "12345678000195")
    @Size(min = 14, max = 18, message = "CNPJ deve ter entre 14 e 18 caracteres")
    @JsonProperty("cnpj_snapshot")
    String cnpjSnapshot,

    @Schema(description = "Inscrição estadual snapshot", example = "123456789")
    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @JsonProperty("state_registration_snapshot")
    String stateRegistrationSnapshot,

    @Schema(description = "Inscrição municipal snapshot", example = "987654321")
    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    @JsonProperty("municipal_registration_snapshot")
    String municipalRegistrationSnapshot,

    @Schema(description = "Nome do representante", example = "João da Silva")
    @Size(max = 200, message = "Nome do representante deve ter no máximo 200 caracteres")
    @JsonProperty("representative_name")
    @ModeratedContent(categories = {HATE, HARASSMENT, SEXUAL}, threshold = 0.15)
    String representativeName,

    @Schema(description = "CPF do representante", example = "12345678909")
    @Size(min = 11, max = 14, message = "CPF do representante deve ter entre 11 e 14 caracteres")
    @JsonProperty("representative_cpf")
    String representativeCpf
) {}
