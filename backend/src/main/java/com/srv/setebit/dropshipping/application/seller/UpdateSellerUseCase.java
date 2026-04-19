package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.request.UpdateSellerRequest;
import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.exception.SellerNotFoundException;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.web.mapper.SellerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateSellerUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final SellerMapper sellerMapper;

    public UpdateSellerUseCase(SellerRepositoryPort sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
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
        return sellerMapper.toResponse(seller);
    }
}
