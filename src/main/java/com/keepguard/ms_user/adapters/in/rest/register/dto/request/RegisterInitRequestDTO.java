package com.keepguard.ms_user.adapters.in.rest.register.dto.request;

import com.keepguard.lib_validation.moderation.application.validator.ModeratedContent;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.keepguard.lib_validation.moderation.domain.model.ModerationCategory.*;

@Schema(description = "Dados para inicialização do registro de usuário")
public record RegisterInitRequestDTO(
    @Schema(description = "Email do usuário", example = "rafael@example.com")
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    String email,

    @Schema(description = "Nome completo do usuário", example = "Rafael")
    @NotBlank(message = "nameFull é obrigatório")
    @Size(max = 255, message = "nameFull deve ter no máximo 255 caracteres")
    @ModeratedContent(categories = {HATE, HARASSMENT, SEXUAL}, threshold = 0.15)
    String nameFull,

    @Schema(description = "Senha do usuário", example = "MinhaSenha@123")
    @NotBlank(message = "password é obrigatório")
    @Size(min = 8, max = 100, message = "password deve ter entre 8 e 100 caracteres")
    String password,

    @Schema(description = "Telefone no formato E164", example = "+5511987098004")
    @NotBlank(message = "phone é obrigatório")
    @Size(max = 20, message = "phone deve ter no máximo 20 caracteres")
    String phone,

    @Schema(description = "Indica se o usuário aceitou os termos e privacidade", example = "true")
    @NotNull(message = "hasAcceptedTermsAndPrivacy é obrigatório")
    Boolean hasAcceptedTermsAndPrivacy,

    @Schema(description = "Indica se o usuário aceitou receber marketing", example = "true")
    Boolean acceptedMarketing,

    @Schema(description = "Endereço IP do usuário", example = "192.168.1.1")
    @Size(max = 45, message = "ipAddress deve ter no máximo 45 caracteres")
    String ipAddress,

    @Schema(description = "User agent do navegador", example = "Mozilla/5.0...")
    @Size(max = 500, message = "userAgent deve ter no máximo 500 caracteres")
    String userAgent,

    @Schema(description = "Geolocalização do usuário", example = "São Paulo, SP, Brasil")
    @Size(max = 255, message = "geolocation deve ter no máximo 255 caracteres")
    String geolocation,

    @Schema(description = "Tipo do usuário", example = "PERSON")
    @NotNull(message = "type é obrigatório")
    UserTypeEnum type
) {}

