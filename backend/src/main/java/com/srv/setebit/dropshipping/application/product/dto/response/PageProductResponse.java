package com.srv.setebit.dropshipping.application.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de produtos")
public record PageProductResponse(
        @Schema(description = "Lista de produtos")
        List<ProductResponse> content,

        @Schema(description = "Total de elementos")
        long totalElements,

        @Schema(description = "Total de páginas")
        int totalPages,

        @Schema(description = "Tamanho da página")
        int size,

        @Schema(description = "Número da página (0-based)")
        int number,

        @Schema(description = "É a primeira página")
        boolean first,

        @Schema(description = "É a última página")
        boolean last
) {
}
