package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.request.CreateProductImageRequest;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductImageResponse;
import com.srv.setebit.dropshipping.domain.product.ProductImage;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AddProductImageUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public AddProductImageUseCase(ProductRepositoryPort productRepository,
                                 ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public ProductImageResponse execute(UUID productId, CreateProductImageRequest request) {
        if (!productRepository.findById(productId).isPresent()) {
            throw new ProductNotFoundException(productId);
        }
        if (Boolean.TRUE.equals(request.isMain())) {
            productImageRepository.unsetMainByProductId(productId);
        }
        ProductImage image = new ProductImage();
        image.setId(UUID.randomUUID());
        image.setProductId(productId);
        image.setUrl(request.url().trim());
        image.setPosition(request.position() != null ? request.position() : 0);
        image.setMain(Boolean.TRUE.equals(request.isMain()));
        image.setAltText(request.altText());
        image = productImageRepository.save(image);
        return new ProductImageResponse(image.getId(), image.getUrl(), image.getPosition(), image.isMain(), image.getAltText());
    }
}
