package com.srv.setebit.dropshipping.application.access.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de rotinas")
public record PageRotinaResponse(
        List<RotinaResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number,
        boolean first,
        boolean last
) {
}
