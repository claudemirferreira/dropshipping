package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.application.seller.dto.response.SellerResponse;
import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.exception.SellerNotFoundException;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.web.mapper.SellerMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSellerByIdUseCase {

    private final SellerRepositoryPort sellerRepository;
    private final SellerMapper sellerMapper;

    public GetSellerByIdUseCase(SellerRepositoryPort sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    public SellerResponse execute(UUID id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(id));
        return sellerMapper.toResponse(seller);
    }
}
