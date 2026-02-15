package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.product.ProductImage;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.ProductImageEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.ProductImageJpaRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductImageRepositoryAdapter implements ProductImageRepositoryPort {

    private final ProductImageJpaRepository jpaRepository;

    public ProductImageRepositoryAdapter(@Lazy ProductImageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ProductImage save(ProductImage productImage) {
        ProductImageEntity entity = toEntity(productImage);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<ProductImage> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ProductImage> findByProductIdOrderByPosition(UUID productId) {
        return jpaRepository.findByProductIdOrderByPosition(productId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(UUID productId) {
        jpaRepository.deleteByProductId(productId);
    }

    @Override
    public void unsetMainByProductId(UUID productId) {
        jpaRepository.setMainFalseByProductId(productId);
    }

    private ProductImageEntity toEntity(ProductImage productImage) {
        ProductImageEntity entity = new ProductImageEntity();
        entity.setId(productImage.getId());
        entity.setProductId(productImage.getProductId());
        entity.setUrl(productImage.getUrl());
        entity.setPosition(productImage.getPosition());
        entity.setMain(productImage.isMain());
        entity.setAltText(productImage.getAltText());
        return entity;
    }

    private ProductImage toDomain(ProductImageEntity entity) {
        return new ProductImage(
                entity.getId(),
                entity.getProductId(),
                entity.getUrl(),
                entity.getPosition(),
                entity.isMain(),
                entity.getAltText()
        );
    }
}
