package com.keepguard.ms_user.adapters.in.rest.contact.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Parâmetros de busca de contatos")
public class ContactSearchRequestDTO {

    @Schema(description = "Valor do contato para filtrar", example = "+5511999999999")
    private String value;

    @Schema(description = "Tipo de contato para filtrar", example = "MOBILE")
    private String type;

    @Schema(description = "Filtrar apenas contatos primários", example = "false")
    private Boolean primary;

    @Schema(description = "Filtrar apenas contatos ativos", example = "true")
    private Boolean active;

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

