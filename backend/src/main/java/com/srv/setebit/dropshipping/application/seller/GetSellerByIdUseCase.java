package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.exception.SellerNotFoundException;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSellerByIdUseCase {

    private final SellerRepositoryPort sellerRepository;

    public GetSellerByIdUseCase(SellerRepositoryPort sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public SellerResponse execute(UUID id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(id));
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
