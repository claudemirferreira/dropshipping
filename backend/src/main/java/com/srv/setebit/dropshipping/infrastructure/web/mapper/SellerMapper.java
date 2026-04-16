package com.srv.setebit.dropshipping.infrastructure.web.mapper;

import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.infrastructure.web.dto.seller.CreateSellerRequest;
import org.springframework.stereotype.Component;

@Component
public class SellerMapper {

    public Seller toCreate(CreateSellerRequest request) {
        return Seller
                .builder()
                .userId(request.userId())
                .marketplace(request.marketplace())
                .marketplaceId(request.marketplaceId())
                .accessToken(request.accessToken())
                .tokenType(request.tokenType())
                .expiresIn(request.expiresIn())
                .refreshToken(request.refreshToken())
                .scope(request.scope())
                .build();
    }

    public SellerResponse toResponse(Seller s) {
        return new SellerResponse(
                s.getId(),
                s.getUserId(),
                s.getMarketplace(),
                s.getAccessToken(),
                s.getTokenType(),
                s.getExpiresIn(),
                s.getScope(),
                s.getMarketplaceId(),
                s.getRefreshToken(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
