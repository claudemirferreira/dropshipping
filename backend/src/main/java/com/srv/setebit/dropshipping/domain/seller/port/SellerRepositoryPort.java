package com.srv.setebit.dropshipping.domain.seller.port;

import com.srv.setebit.dropshipping.domain.seller.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SellerRepositoryPort {

    Seller save(Seller seller);

    Optional<Seller> findById(UUID id);

    Page<Seller> findAll(Long marketplaceId, Pageable pageable);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
