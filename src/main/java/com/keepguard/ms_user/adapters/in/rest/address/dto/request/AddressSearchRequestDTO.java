package com.keepguard.ms_user.adapters.in.rest.address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Parâmetros de busca de endereços")
public class AddressSearchRequestDTO {

    @Schema(description = "Cidade para filtrar", example = "São Paulo")
    private String city;

    @Schema(description = "Estado para filtrar", example = "SP")
    private String state;

    @Schema(description = "CEP para filtrar", example = "01234567")
    private String zipCode;

    @Schema(description = "Tipo de endereço para filtrar", example = "RESIDENTIAL")
    private String type;

    @Schema(description = "Filtrar apenas endereços primários", example = "false")
    private Boolean primary;

    @Schema(description = "Filtrar apenas endereços ativos", example = "true")
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

