package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.domain.product.ProductImage;
import com.srv.setebit.dropshipping.domain.product.exception.ProductImageNotFoundException;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RemoveProductImageUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public RemoveProductImageUseCase(ProductRepositoryPort productRepository,
                                    ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public void execute(UUID productId, UUID imageId) {
        if (!productRepository.findById(productId).isPresent()) {
            throw new ProductNotFoundException(productId);
        }
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ProductImageNotFoundException(imageId));
        if (!image.getProductId().equals(productId)) {
            throw new ProductImageNotFoundException(imageId);
        }
        productImageRepository.deleteById(imageId);
        if (image.isMain()) {
            List<ProductImage> remaining = productImageRepository.findByProductIdOrderByPosition(productId);
            if (!remaining.isEmpty()) {
                ProductImage first = remaining.get(0);
                first.setMain(true);
                productImageRepository.save(first);
            }
        }
    }
}
