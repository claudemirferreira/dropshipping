package com.srv.setebit.dropshipping.application.product.dto.response;

import com.srv.setebit.dropshipping.domain.product.ProductFileType;

import java.util.UUID;

public record ProductFileResponse(
        UUID id,
        ProductFileType fileType,
        String objectName,
        String originalName,
        int position,
        boolean main,
        String altText
) {
}

