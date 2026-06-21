package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados para atualização de um usuário existente")
public record UserUpdateRequestDTO(
    @Schema(description = "ID da empresa (imutável)", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID companyId,

    @Schema(description = "Código único do usuário (imutável)", example = "456e7890-e89b-12d3-a456-426614174000")
    UUID codeUser,

    @Schema(description = "Tipo do usuário (imutável)", example = "PERSON")
    UserTypeEnum type,

    @Schema(description = "Status do usuário (imutável)", example = "ACTIVE")
    UserStatusEnum status,

    @Schema(description = "Email do usuário", example = "usuario@exemplo.com")
    @Email @Size(max = 255)
    String email,

    @Schema(description = "Telefone no formato E164", example = "+5511999999999")
    @Size(max = 20)
    String phoneE164,

    @Schema(description = "Idioma preferido do usuário", example = "pt-BR")
    @Size(max = 10)
    String preferredLocale,

    @Schema(description = "Fuso horário do usuário", example = "America/Sao_Paulo")
    @Size(max = 64)
    String timezone,

    @Schema(description = "URL do avatar do usuário", example = "https://exemplo.com/avatar.jpg")
    @Size(max = 512)
    String avatarUrl,

    @Schema(description = "Dados do perfil de pessoa física")
    @Valid
    PersonRequestDTO personProfile,

    @Schema(description = "Dados do perfil de pessoa jurídica")
    @Valid
    CompanyRequestDTO companyProfile
) {}
