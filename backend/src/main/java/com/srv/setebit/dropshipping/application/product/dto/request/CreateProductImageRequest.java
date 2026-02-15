package com.srv.setebit.dropshipping.application.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para adicionar imagem ao produto")
public record CreateProductImageRequest(
        @NotBlank(message = "URL da imagem é obrigatória")
        @Length(max = 1000)
        @Schema(description = "URL da imagem", required = true)
        String url,

        @Min(0)
        @Schema(description = "Posição na galeria", example = "0")
        Integer position,

        @Schema(description = "É a imagem principal", example = "false")
        Boolean isMain,

        @Length(max = 500)
        @Schema(description = "Texto alternativo para acessibilidade/SEO")
        String altText
) {
}
