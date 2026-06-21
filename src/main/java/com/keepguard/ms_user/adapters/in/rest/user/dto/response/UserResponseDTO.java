package com.keepguard.ms_user.adapters.in.rest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.ms_user.domain.enums.UserStatusEnum;
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
@Schema(description = "Dados de resposta de usuário")
public class UserResponseDTO {

    @Schema(description = "ID único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Código único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID codeUser;

    @Schema(description = "ID da empresa", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID companyId;

    @Schema(description = "Tipo do usuário", example = "ADMIN")
    private UserTypeEnum type;

    @Schema(description = "Email do usuário", example = "usuario@exemplo.com")
    private String email;

    @Schema(description = "Telefone no formato E164", example = "+5511999999999")
    private String phoneE164;

    @Schema(description = "Localização preferida", example = "pt-BR")
    private String preferredLocale;

    @Schema(description = "Fuso horário", example = "America/Sao_Paulo")
    private String timezone;

    @Schema(description = "URL do avatar", example = "https://exemplo.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "Apelido público do usuário para exibição", example = "rafael.soares")
    @JsonProperty("display_handle")
    private String displayHandle;

    @Schema(description = "Status do usuário", example = "ACTIVE")
    private UserStatusEnum status;

    @Schema(description = "Dados do perfil de pessoa física")
    private PersonResponseDTO personProfile;

    @Schema(description = "Dados do perfil de pessoa jurídica")
    private CompanyResponseDTO companyProfile;

    @Schema(description = "Data de criação")
    private OffsetDateTime createdAt;

    @Schema(description = "Data de atualização")
    private OffsetDateTime updatedAt;
}
