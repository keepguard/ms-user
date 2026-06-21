package com.keepguard.ms_user.adapters.in.rest.register.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
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
@Schema(description = "Resposta da confirmação do registro de usuário")
public class RegisterConfirmResponseDTO {

    @Schema(description = "ID da sessão de registro", example = "b9a1f7e8-3c92-42a9-ae76-62b8a8e9f812")
    @JsonProperty("registration_session_id")
    private UUID registrationSessionId;

    @Schema(description = "ID da aplicação", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("x_application")
    private UUID xApplication;

    @Schema(description = "Email do usuário", example = "usuario@exemplo.com")
    private String email;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @JsonProperty("name_full")
    private String nameFull;

    @Schema(description = "Telefone do usuário", example = "+5511999999999")
    private String phone;

    @Schema(description = "Tipo de usuário", example = "PERSON")
    private UserTypeEnum type;

    @Schema(description = "Aceitou termos e privacidade", example = "true")
    @JsonProperty("has_accepted_terms_and_privacy")
    private Boolean hasAcceptedTermsAndPrivacy;

    @Schema(description = "Aceitou marketing", example = "false")
    @JsonProperty("accepted_marketing")
    private Boolean acceptedMarketing;

    @Schema(description = "Endereço IP", example = "192.168.1.1")
    @JsonProperty("ip_address")
    private String ipAddress;

    @Schema(description = "User Agent", example = "Mozilla/5.0...")
    @JsonProperty("user_agent")
    private String userAgent;

    @Schema(description = "Geolocalização", example = "São Paulo, SP, Brasil")
    private String geolocation;

    @Schema(description = "Data de criação da sessão", example = "2024-01-15T10:30:00-03:00")
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @Schema(description = "Número de tentativas", example = "1")
    private Integer attempts;

    @Schema(description = "Mensagem de sucesso", example = "Registro confirmado com sucesso")
    private String message;

    @Schema(description = "password hash", example = "Password Hash")
    private String passwordHash;
}

