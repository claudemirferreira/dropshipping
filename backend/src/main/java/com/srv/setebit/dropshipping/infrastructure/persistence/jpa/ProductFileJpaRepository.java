package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductFileJpaRepository extends JpaRepository<ProductFileEntity, UUID> {

    List<ProductFileEntity> findByProductIdOrderByPositionAsc(UUID productId);

    void deleteByProductId(UUID productId);
}

