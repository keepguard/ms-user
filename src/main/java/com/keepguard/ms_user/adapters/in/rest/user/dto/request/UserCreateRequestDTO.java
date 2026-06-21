package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados para criação de um novo usuário")
public record UserCreateRequestDTO(
    @Schema(description = "ID da empresa à qual o usuário pertence", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "companyId é obrigatório")
    UUID companyId,

    @Schema(description = "Tipo do usuário", example = "PERSON")
    @NotNull(message = "type é obrigatório")
    UserTypeEnum type,

    @Schema(description = "Email do usuário", example = "usuario@exemplo.com")
    @NotBlank(message = "email é obrigatório")
    @Email(message = "email deve ser válido")
    @Size(max = 255, message = "email deve ter no máximo 255 caracteres")
    String email,

    @Schema(description = "Telefone no formato E164", example = "+5511999999999")
    @Size(max = 20, message = "phoneE164 deve ter no máximo 20 caracteres")
    String phoneE164,

    @Schema(description = "Idioma preferido do usuário", example = "pt-BR")
    @Size(max = 10, message = "preferredLocale deve ter no máximo 10 caracteres")
    String preferredLocale,

    @Schema(description = "Fuso horário do usuário", example = "America/Sao_Paulo")
    @Size(max = 64, message = "timezone deve ter no máximo 64 caracteres")
    String timezone,

    @Schema(description = "URL do avatar do usuário", example = "https://exemplo.com/avatar.jpg")
    @Size(max = 512, message = "avatarUrl deve ter no máximo 512 caracteres")
    String avatarUrl,

    @Schema(description = "Dados do perfil de pessoa física (obrigatório para tipo PERSON)")
    @Valid
    PersonRequestDTO personProfile,

    @Schema(description = "Dados do perfil de pessoa jurídica (obrigatório para tipo COMPANY)")
    @Valid
    CompanyRequestDTO companyProfile
) {}
