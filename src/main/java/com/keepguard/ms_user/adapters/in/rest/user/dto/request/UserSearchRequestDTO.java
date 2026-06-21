package com.keepguard.ms_user.adapters.in.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Parâmetros de busca de usuários")
public class UserSearchRequestDTO {

    @Schema(description = "Email para filtrar")
    private String email;

    @Schema(description = "Tipo de usuário para filtrar")
    private String type;

    @Schema(description = "Status do usuário para filtrar")
    private String status;

    @Schema(description = "Número da página", example = "0")
    @Min(value = 0, message = "Página deve ser maior ou igual a 0")
    private Integer page = 0;

    @Schema(description = "Tamanho da página", example = "20")
    @Min(value = 1, message = "Tamanho da página deve ser maior que 0")
    @Max(value = 100, message = "Tamanho da página deve ser menor ou igual a 100")
    private Integer size = 20;

    @Schema(description = "Campos para ordenação")
    private List<String> sort;

    @Schema(description = "Direção da ordenação", example = "ASC")
    private String direction = "ASC";
}
