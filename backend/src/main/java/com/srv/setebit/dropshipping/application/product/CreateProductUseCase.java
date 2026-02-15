package com.srv.setebit.dropshipping.application.product;

import com.srv.setebit.dropshipping.application.product.dto.request.CreateProductImageRequest;
import com.srv.setebit.dropshipping.application.product.dto.request.CreateProductRequest;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductDetailResponse;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.ProductImage;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSkuException;
import com.srv.setebit.dropshipping.domain.product.exception.DuplicateSlugException;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreateProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final ProductImageRepositoryPort productImageRepository;

    public CreateProductUseCase(ProductRepositoryPort productRepository,
                               ProductImageRepositoryPort productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public ProductDetailResponse execute(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        if (productRepository.existsBySlug(request.slug())) {
            throw new DuplicateSlugException(request.slug());
        }

        Instant now = Instant.now();
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setSku(request.sku().trim());
        product.setName(request.name().trim());
        product.setShortDescription(request.shortDescription().trim());
        product.setFullDescription(request.fullDescription());
        product.setSalePrice(request.salePrice());
        product.setCostPrice(request.costPrice());
        product.setCurrency(request.currency() != null ? request.currency().trim() : "BRL");
        product.setStatus(request.status());
        product.setSupplierSku(request.supplierSku());
        product.setSupplierName(request.supplierName());
        product.setSupplierProductUrl(request.supplierProductUrl());
        product.setLeadTimeDays(request.leadTimeDays());
        product.setDropship(request.isDropship() != null ? request.isDropship() : true);
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
        product.setTags(null);
        product.setAttributes(null);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        product = productRepository.save(product);

        if (request.images() != null && !request.images().isEmpty()) {
            boolean hasMain = false;
            for (int i = 0; i < request.images().size(); i++) {
                CreateProductImageRequest imgReq = request.images().get(i);
                boolean isMain = Boolean.TRUE.equals(imgReq.isMain());
                if (isMain) hasMain = true;
                if (!hasMain && i == request.images().size() - 1) {
                    isMain = true;
                }
                if (isMain) {
                    productImageRepository.unsetMainByProductId(product.getId());
                }
                ProductImage img = new ProductImage();
                img.setId(UUID.randomUUID());
                img.setProductId(product.getId());
                img.setUrl(imgReq.url().trim());
                img.setPosition(imgReq.position() != null ? imgReq.position() : 0);
                img.setMain(isMain);
                img.setAltText(imgReq.altText());
                productImageRepository.save(img);
            }
        }

        return toDetailResponse(product);
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        var images = productImageRepository.findByProductIdOrderByPosition(product.getId()).stream()
                .map(img -> new com.srv.setebit.dropshipping.application.product.dto.response.ProductImageResponse(
                        img.getId(), img.getUrl(), img.getPosition(), img.isMain(), img.getAltText()))
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

    private String normalizeSlug(String slug) {
        if (slug == null || slug.isBlank()) return slug;
        return slug.trim().toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
    }
}
