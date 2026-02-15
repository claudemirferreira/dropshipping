package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.ProductEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.ProductJpaRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryAdapter(@Lazy ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, UUID id) {
        return jpaRepository.existsBySkuAndIdNot(sku, id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsBySlugAndIdNot(String slug, UUID id) {
        return jpaRepository.existsBySlugAndIdNot(slug, id);
    }

    @Override
    public Page<Product> findAllByFilter(String name, String status, UUID categoryId, Pageable pageable) {
        ProductStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = ProductStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return jpaRepository.findAllByFilter(name, statusEnum, categoryId, pageable).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setSku(product.getSku());
        entity.setName(product.getName());
        entity.setShortDescription(product.getShortDescription());
        entity.setFullDescription(product.getFullDescription());
        entity.setSalePrice(product.getSalePrice());
        entity.setCostPrice(product.getCostPrice());
        entity.setCurrency(product.getCurrency());
        entity.setStatus(product.getStatus());
        entity.setSupplierSku(product.getSupplierSku());
        entity.setSupplierName(product.getSupplierName());
        entity.setSupplierProductUrl(product.getSupplierProductUrl());
        entity.setLeadTimeDays(product.getLeadTimeDays());
        entity.setDropship(product.isDropship());
        entity.setWeight(product.getWeight());
        entity.setLength(product.getLength());
        entity.setWidth(product.getWidth());
        entity.setHeight(product.getHeight());
        entity.setSlug(product.getSlug());
        entity.setCategoryId(product.getCategoryId());
        entity.setBrand(product.getBrand());
        entity.setMetaTitle(product.getMetaTitle());
        entity.setMetaDescription(product.getMetaDescription());
        entity.setCompareAtPrice(product.getCompareAtPrice());
        entity.setStockQuantity(product.getStockQuantity());
        entity.setTags(product.getTags());
        entity.setAttributes(product.getAttributes());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());
        return entity;
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getShortDescription(),
                entity.getFullDescription(),
                entity.getSalePrice(),
                entity.getCostPrice(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getSupplierSku(),
                entity.getSupplierName(),
                entity.getSupplierProductUrl(),
                entity.getLeadTimeDays(),
                entity.isDropship(),
                entity.getWeight(),
                entity.getLength(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getSlug(),
                entity.getCategoryId(),
                entity.getBrand(),
                entity.getMetaTitle(),
                entity.getMetaDescription(),
                entity.getCompareAtPrice(),
                entity.getStockQuantity(),
                entity.getTags(),
                entity.getAttributes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
