package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.request.UpdateSellerRequest;
import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.exception.SellerNotFoundException;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateSellerUseCase {

    private final SellerRepositoryPort sellerRepository;

    public UpdateSellerUseCase(SellerRepositoryPort sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public SellerResponse execute(UUID id, UpdateSellerRequest request) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(id));

        seller.setMarketplace(request.marketplace());
        seller.setAccessToken(request.accessToken());
        seller.setTokenType(request.tokenType());
        seller.setExpiresIn(request.expiresIn());
        seller.setScope(request.scope());
        seller.setMarketplaceId(request.marketplaceId());
        seller.setRefreshToken(request.refreshToken());

        seller = sellerRepository.save(seller);
        return toResponse(seller);
    }

    private SellerResponse toResponse(Seller s) {
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
