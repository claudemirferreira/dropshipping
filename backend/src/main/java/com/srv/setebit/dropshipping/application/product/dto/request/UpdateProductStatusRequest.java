package com.srv.setebit.dropshipping.application.product.dto.request;

import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para alteração de status do produto")
public record UpdateProductStatusRequest(
        @NotNull(message = "Status é obrigatório")
        @Schema(description = "Novo status", required = true)
        ProductStatus status
) {
}
