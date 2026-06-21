package com.keepguard.ms_user.application.dto.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de visualização das preferências de notificação de um usuário")
public record NotifyViewDTO(
    @Schema(description = "ID único da preferência", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID userId,

    @Schema(description = "Receber notificações por email", example = "true")
    boolean notifyEmail,

    @Schema(description = "Receber notificações por SMS", example = "false")
    boolean notifySms,

    @Schema(description = "Receber notificações por WhatsApp", example = "true")
    boolean notifyWhatsapp,

    @Schema(description = "Receber notificações push", example = "true")
    boolean notifyPush,

    @Schema(description = "Data de criação das preferências", example = "2024-01-15T10:30:00-03:00")
    OffsetDateTime createdAt,

    @Schema(description = "Data da última atualização das preferências", example = "2024-01-15T14:45:00-03:00")
    OffsetDateTime updatedAt,

    @Schema(description = "Versão para controle de concorrência", example = "1")
    Long version
) {}
