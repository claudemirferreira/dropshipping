package com.srv.setebit.dropshipping.domain.product.port;

import com.srv.setebit.dropshipping.domain.product.ProductFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductFileRepositoryPort {

    ProductFile save(ProductFile file);

    Optional<ProductFile> findById(UUID id);

    List<ProductFile> findByProductId(UUID productId);

    void deleteById(UUID id);

    void deleteByProductId(UUID productId);
}

