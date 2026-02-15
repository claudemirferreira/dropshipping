package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.response.ProductDetailResponse;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductImageResponse;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetProductByIdUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public GetProductByIdUseCase(ProductRepositoryPort productRepository,
                                ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public ProductDetailResponse execute(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDetailResponse(product);
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        List<ProductImageResponse> images = productImageRepository.findByProductIdOrderByPosition(product.getId())
                .stream()
                .map(img -> new ProductImageResponse(img.getId(), img.getUrl(), img.getPosition(), img.isMain(), img.getAltText()))
                .toList();
        return new ProductDetailResponse(
                product.getId(), product.getSku(), product.getName(), product.getShortDescription(),
                product.getFullDescription(), product.getSalePrice(), product.getCostPrice(),
                product.getCurrency(), product.getStatus(), product.getSupplierSku(),
                product.getSupplierName(), product.getSupplierProductUrl(), product.getLeadTimeDays(),
                product.isDropship(), product.getWeight(), product.getLength(), product.getWidth(),
                product.getHeight(), product.getSlug(), product.getCategoryId(), product.getBrand(),
                product.getMetaTitle(), product.getMetaDescription(), product.getCompareAtPrice(),
                product.getStockQuantity(), images, product.getCreatedAt(), product.getUpdatedAt()
        );
    }
}
