package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.response.PageSellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.web.mapper.SellerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListSellersUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final SellerMapper sellerMapper;

    public ListSellersUseCase(SellerRepositoryPort sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    public PageSellerResponse execute(Long marketplaceId, Pageable pageable) {
        Page<Seller> page = sellerRepository.findAll(marketplaceId, pageable);
        return new PageSellerResponse(
                page.getContent().stream().map(sellerMapper::toResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast());
    }
}
