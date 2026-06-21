package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import com.keepguard.ms_user.domain.entity.PersonProfile;
import com.keepguard.ms_user.domain.entity.CompanyProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de visualização de um usuário")
public record UserViewDTO(
    @Schema(description = "ID único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "Código único do usuário", example = "456e7890-e89b-12d3-a456-426614174000")
    UUID codeUser,

    @Schema(description = "ID da empresa à qual o usuário pertence", example = "789e0123-e89b-12d3-a456-426614174000")
    UUID companyId,

    @Schema(description = "Tipo do usuário", example = "PERSON")
    UserTypeEnum type,

    @Schema(description = "Status do usuário", example = "ACTIVE")
    UserStatusEnum status,

    @Schema(description = "Email do usuário", example = "usuario@exemplo.com")
    String email,

    @Schema(description = "Telefone no formato E164", example = "+5511999999999")
    String phoneE164,

    @Schema(description = "Idioma preferido do usuário", example = "pt-BR")
    String preferredLocale,

    @Schema(description = "Fuso horário do usuário", example = "America/Sao_Paulo")
    String timezone,

    @Schema(description = "URL do avatar do usuário", example = "https://exemplo.com/avatar.jpg")
    String avatarUrl,

    @Schema(description = "Apelido público do usuário para exibição", example = "rafael.soares")
    String displayHandle,

    @Schema(description = "Perfil de pessoa física")
    PersonProfile personProfile,

    @Schema(description = "Perfil de pessoa jurídica")
    CompanyProfile companyProfile,

    @Schema(description = "Data de criação do usuário", example = "2024-01-15T10:30:00-03:00")
    OffsetDateTime createdAt,

    @Schema(description = "Data da última atualização do usuário", example = "2024-01-15T14:45:00-03:00")
    OffsetDateTime updatedAt
) {}
