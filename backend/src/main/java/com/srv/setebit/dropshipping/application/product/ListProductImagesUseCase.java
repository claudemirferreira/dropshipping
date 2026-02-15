package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.response.ProductImageResponse;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListProductImagesUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public ListProductImagesUseCase(ProductRepositoryPort productRepository,
                                   ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public List<ProductImageResponse> execute(UUID productId) {
        if (!productRepository.findById(productId).isPresent()) {
            throw new ProductNotFoundException(productId);
        }
        return productImageRepository.findByProductIdOrderByPosition(productId).stream()
                .map(img -> new ProductImageResponse(img.getId(), img.getUrl(), img.getPosition(), img.isMain(), img.getAltText()))
                .toList();
    }
}
