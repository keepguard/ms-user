package com.keepguard.ms_user.adapters.in.rest.usernotify.dto.response;

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
@Schema(description = "Dados de resposta de preferências de notificação")
public class UserNotifyResponseDTO {

    @Schema(description = "ID único da preferência", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Notificar por email", example = "true")
    private Boolean notifyEmail;

    @Schema(description = "Notificar por SMS", example = "false")
    private Boolean notifySms;

    @Schema(description = "Notificar por WhatsApp", example = "true")
    private Boolean notifyWhatsapp;

    @Schema(description = "Notificar por push", example = "true")
    private Boolean notifyPush;

    @Schema(description = "Data de criação")
    private OffsetDateTime createdAt;

    @Schema(description = "Data de atualização")
    private OffsetDateTime updatedAt;
}
