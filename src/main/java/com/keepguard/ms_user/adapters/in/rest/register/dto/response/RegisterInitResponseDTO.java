package com.keepguard.ms_user.adapters.in.rest.register.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta da inicialização do registro de usuário")
public class RegisterInitResponseDTO {

    @Schema(description = "ID da sessão de registro", example = "b9a1f7e8-3c92-42a9-ae76-62b8a8e9f812")
    private UUID registrationSessionId;

    @Schema(description = "Email do usuário", example = "rafael@example.com")
    private String email;

    @Schema(description = "Tempo de expiração em segundos", example = "1200")
    private Integer expiresIn;

    @Schema(description = "Mensagem de resposta", example = "Token de verificação enviado.")
    private String message;

    @Schema(description = "Token de verificação de 6 dígitos", example = "123456")
    private String token;
}

