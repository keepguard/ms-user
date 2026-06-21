package com.keepguard.ms_user.adapters.in.rest.user.dto.response;

import com.keepguard.ms_user.domain.enums.UserStatusEnum;
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
@Schema(description = "Dados de resposta de mudança de status de usuário")
public class UserStatusResponseDTO {

    @Schema(description = "ID único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Status anterior do usuário", example = "PENDING")
    private UserStatusEnum previousStatus;

    @Schema(description = "Novo status do usuário", example = "ACTIVE")
    private UserStatusEnum newStatus;

    @Schema(description = "Motivo da mudança", example = "Usuário aprovado após verificação")
    private String reason;

    @Schema(description = "Data da mudança", example = "2024-01-15T10:30:00-03:00")
    private OffsetDateTime changedAt;

    @Schema(description = "Usuário pode realizar operações", example = "true")
    private Boolean canPerformOperations;
}
