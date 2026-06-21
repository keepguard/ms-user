package com.keepguard.ms_user.adapters.in.rest.usernotify.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO para criação de preferências de notificação")
public record UserNotifyCreateRequestDTO(
    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "userId é obrigatório")
    UUID userId,
    
    @Schema(description = "Receber notificações por email", example = "true")
    Boolean notifyEmail,
    
    @Schema(description = "Receber notificações por SMS", example = "true")
    Boolean notifySms,
    
    @Schema(description = "Receber notificações por WhatsApp", example = "false")
    Boolean notifyWhatsapp,
    
    @Schema(description = "Receber notificações push", example = "true")
    Boolean notifyPush
) {
}
