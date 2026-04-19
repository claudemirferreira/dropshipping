package com.srv.setebit.dropshipping.infrastructure.web.dto.marketplace;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConnectMarketplaceRequest(
        @NotNull(message = "marketplace é obrigatório")
        MarketplaceEnum marketplace,

        @NotBlank(message = "code é obrigatório")
        String code
) {}
