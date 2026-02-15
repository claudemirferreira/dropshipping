package com.srv.setebit.dropshipping.application.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para atualização de imagem do produto")
public record UpdateProductImageRequest(
        @Min(0)
        @Schema(description = "Posição na galeria")
        Integer position,

        @Schema(description = "É a imagem principal")
        Boolean isMain,

        @Length(max = 500)
        @Schema(description = "Texto alternativo para acessibilidade/SEO")
        String altText
) {
}
