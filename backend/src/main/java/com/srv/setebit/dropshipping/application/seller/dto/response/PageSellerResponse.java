package com.srv.setebit.dropshipping.application.seller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de sellers Mercado Livre")
public record PageSellerResponse(
        List<SellerResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number,
        boolean first,
        boolean last
) {
}
