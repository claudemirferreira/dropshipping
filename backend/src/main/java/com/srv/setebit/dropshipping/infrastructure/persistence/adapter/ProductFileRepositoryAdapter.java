package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.product.ProductFile;
import com.srv.setebit.dropshipping.domain.product.port.ProductFileRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.ProductFileEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.ProductFileJpaRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductFileRepositoryAdapter implements ProductFileRepositoryPort {

    private final ProductFileJpaRepository jpaRepository;

    public ProductFileRepositoryAdapter(@Lazy ProductFileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ProductFile save(ProductFile file) {
        ProductFileEntity entity = toEntity(file);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<ProductFile> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ProductFile> findByProductId(UUID productId) {
        return jpaRepository.findByProductIdOrderByPositionAsc(productId).stream()
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

    private ProductFileEntity toEntity(ProductFile file) {
        ProductFileEntity entity = new ProductFileEntity();
        entity.setId(file.getId());
        entity.setProductId(file.getProductId());
        entity.setFileType(file.getFileType());
        entity.setObjectName(file.getObjectName());
        entity.setOriginalName(file.getOriginalName());
        entity.setPosition(file.getPosition());
        entity.setMain(file.isMain());
        entity.setAltText(file.getAltText());
        entity.setCreatedAt(file.getCreatedAt());
        return entity;
    }

    private ProductFile toDomain(ProductFileEntity entity) {
        return new ProductFile(
                entity.getId(),
                entity.getProductId(),
                entity.getFileType(),
                entity.getObjectName(),
                entity.getOriginalName(),
                entity.getPosition(),
                entity.isMain(),
                entity.getAltText(),
                entity.getCreatedAt()
        );
    }
}

