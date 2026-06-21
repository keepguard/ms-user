package com.keepguard.ms_user.application.dto.user;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados básicos de visualização de um usuário")
public record UserSimpleViewDTO(
    @Schema(description = "ID único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "Código único do usuário", example = "456e7890-e89b-12d3-a456-426614174000")
    UUID codeUser,

    @Schema(description = "Email do usuário", example = "usuario@exemplo.com")
    String email,

    @Schema(description = "Status do usuário", example = "ACTIVE")
    UserStatusEnum status,

    @Schema(description = "Tipo do usuário", example = "PERSON")
    UserTypeEnum type,

    @Schema(description = "Data de criação do usuário", example = "2024-01-15T10:30:00-03:00")
    OffsetDateTime createdAt
) {}
