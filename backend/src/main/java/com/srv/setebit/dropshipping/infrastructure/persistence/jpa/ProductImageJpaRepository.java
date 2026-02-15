package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductImageJpaRepository extends JpaRepository<ProductImageEntity, UUID> {

    List<ProductImageEntity> findByProductIdOrderByPosition(UUID productId);

    void deleteByProductId(UUID productId);

    @Modifying
    @Query("UPDATE ProductImageEntity p SET p.main = false WHERE p.productId = :productId")
    void setMainFalseByProductId(@Param("productId") UUID productId);
}
