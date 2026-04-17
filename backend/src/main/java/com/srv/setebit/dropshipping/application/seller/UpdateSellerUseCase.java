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
    public Seller execute(UUID id, Seller sellet) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(id));
        seller = sellerRepository.save(seller);

        return seller;
    }

}
