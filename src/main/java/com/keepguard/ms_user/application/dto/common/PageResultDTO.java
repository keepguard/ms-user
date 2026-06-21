package com.keepguard.ms_user.application.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resultado paginado de uma consulta")
public record PageResultDTO<T>(
    @Schema(description = "Lista de elementos da página atual")
    List<T> content,

    @Schema(description = "Total de elementos em todas as páginas", example = "150")
    long totalElements,

    @Schema(description = "Número da página atual (baseado em 0)", example = "0")
    int page,

    @Schema(description = "Tamanho da página", example = "20")
    int size
) {
    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }

    public boolean hasNext() {
        return page < getTotalPages() - 1;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
