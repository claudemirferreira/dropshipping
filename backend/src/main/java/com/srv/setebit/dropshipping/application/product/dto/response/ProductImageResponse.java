package com.srv.setebit.dropshipping.application.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Imagem do produto")
public record ProductImageResponse(
        @Schema(description = "ID da imagem")
        UUID id,

        @Schema(description = "URL da imagem")
        String url,

        @Schema(description = "Posição na galeria")
        int position,

        @Schema(description = "É a imagem principal")
        boolean main,

        @Schema(description = "Texto alternativo")
        String altText
) {
}
