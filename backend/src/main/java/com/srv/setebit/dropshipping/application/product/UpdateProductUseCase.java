package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.request.UpdateProductRequest;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductDetailResponse;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSlugException;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateProductUseCase {

    private final ProductRepositoryPort productRepository;

    public UpdateProductUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductDetailResponse execute(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (productRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new DuplicateSlugException(request.slug());
        }

        product.setName(request.name().trim());
        product.setShortDescription(request.shortDescription().trim());
        product.setFullDescription(request.fullDescription());
        product.setSalePrice(request.salePrice());
        product.setCostPrice(request.costPrice());
        product.setCurrency(request.currency().trim());
        product.setStatus(request.status());
        product.setSupplierSku(request.supplierSku());
        product.setSupplierName(request.supplierName());
        product.setSupplierProductUrl(request.supplierProductUrl());
        product.setLeadTimeDays(request.leadTimeDays());
        if (request.isDropship() != null) {
            product.setDropship(request.isDropship());
        }
        product.setWeight(request.weight());
        product.setLength(request.length());
        product.setWidth(request.width());
        product.setHeight(request.height());
        product.setSlug(normalizeSlug(request.slug()));
        product.setCategoryId(request.categoryId());
        product.setBrand(request.brand());
        product.setMetaTitle(request.metaTitle());
        product.setMetaDescription(request.metaDescription());
        product.setCompareAtPrice(request.compareAtPrice());
        product.setStockQuantity(request.stockQuantity());
        product.setUpdatedAt(java.time.Instant.now());

        product = productRepository.save(product);
        return toDetailResponse(product);
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        return new ProductDetailResponse(
                product.getId(), product.getSku(), product.getName(), product.getShortDescription(),
                product.getFullDescription(), product.getSalePrice(), product.getCostPrice(),
                product.getCurrency(), product.getStatus(), product.getSupplierSku(),
                product.getSupplierName(), product.getSupplierProductUrl(), product.getLeadTimeDays(),
                product.isDropship(), product.getWeight(), product.getLength(), product.getWidth(),
                product.getHeight(), product.getSlug(), product.getCategoryId(), product.getBrand(),
                product.getMetaTitle(), product.getMetaDescription(), product.getCompareAtPrice(),
                product.getStockQuantity(), product.getCreatedAt(), product.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        if (slug == null || slug.isBlank()) return slug;
        return slug.trim().toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
    }
}
