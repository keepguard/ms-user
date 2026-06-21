package com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualização das preferências de notificação de um usuário")
public record UserNotifyPatchRequestDTO(
    @Schema(description = "Receber notificações por email", example = "true")
    Boolean notifyEmail,

    @Schema(description = "Receber notificações por SMS", example = "false")
    Boolean notifySms,

    @Schema(description = "Receber notificações por WhatsApp", example = "true")
    Boolean notifyWhatsapp,

    @Schema(description = "Receber notificações push", example = "true")
    Boolean notifyPush
) {}
