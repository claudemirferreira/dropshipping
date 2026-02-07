package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.request.UpdateProductImageRequest;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductImageResponse;
import com.srv.setebit.dropshipping.domain.product.ProductImage;
import com.srv.setebit.dropshipping.domain.product.exception.ProductImageNotFoundException;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateProductImageUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public UpdateProductImageUseCase(ProductRepositoryPort productRepository,
                                    ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public ProductImageResponse execute(UUID productId, UUID imageId, UpdateProductImageRequest request) {
        if (!productRepository.findById(productId).isPresent()) {
            throw new ProductNotFoundException(productId);
        }
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ProductImageNotFoundException(imageId));
        if (!image.getProductId().equals(productId)) {
            throw new ProductImageNotFoundException(imageId);
        }
        if (request.position() != null) {
            image.setPosition(request.position());
        }
        if (Boolean.TRUE.equals(request.isMain())) {
            productImageRepository.unsetMainByProductId(productId);
            image.setMain(true);
        } else if (Boolean.FALSE.equals(request.isMain())) {
            image.setMain(false);
        }
        if (request.altText() != null) {
            image.setAltText(request.altText());
        }
        image = productImageRepository.save(image);
        return new ProductImageResponse(image.getId(), image.getUrl(), image.getPosition(), image.isMain(), image.getAltText());
    }
}
