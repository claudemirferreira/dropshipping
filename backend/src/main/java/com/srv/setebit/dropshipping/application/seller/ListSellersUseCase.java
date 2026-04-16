package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.response.PageSellerResponse;
import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListSellersUseCase {

    private final SellerRepositoryPort sellerRepository;

    public ListSellersUseCase(SellerRepositoryPort sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public PageSellerResponse execute(Long marketplaceId, Pageable pageable) {
        Page<Seller> page = sellerRepository.findAll(marketplaceId, pageable);
        return new PageSellerResponse(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast());
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
