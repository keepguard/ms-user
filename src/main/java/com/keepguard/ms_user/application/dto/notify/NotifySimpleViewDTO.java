package com.keepguard.ms_user.application.dto.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Dados básicos de visualização das preferências de notificação")
public record NotifySimpleViewDTO(
    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID userId,

    @Schema(description = "Receber notificações por email", example = "true")
    boolean notifyEmail,

    @Schema(description = "Receber notificações por SMS", example = "false")
    boolean notifySms,

    @Schema(description = "Receber notificações por WhatsApp", example = "true")
    boolean notifyWhatsapp,

    @Schema(description = "Receber notificações push", example = "true")
    boolean notifyPush
) {}
