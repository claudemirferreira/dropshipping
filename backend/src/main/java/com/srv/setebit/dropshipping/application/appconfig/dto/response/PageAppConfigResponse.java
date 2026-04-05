package com.srv.setebit.dropshipping.application.appconfig.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de configurações")
public record PageAppConfigResponse(
        List<AppConfigResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number,
        boolean first,
        boolean last
) {
}
