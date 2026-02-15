package com.srv.setebit.dropshipping.domain.product.port;

import com.srv.setebit.dropshipping.domain.product.ProductImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepositoryPort {

    ProductImage save(ProductImage productImage);

    Optional<ProductImage> findById(UUID id);

    List<ProductImage> findByProductIdOrderByPosition(UUID productId);

    void deleteById(UUID id);

    void deleteByProductId(UUID productId);

    void unsetMainByProductId(UUID productId);
}
