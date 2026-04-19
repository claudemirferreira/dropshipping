package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.repository;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity.SellerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SellerJpaRepository extends JpaRepository<SellerEntity, UUID> {

    @Query("SELECT s FROM SellerEntity s WHERE "
            + "(:marketplaceId IS NULL OR s.marketplaceId = :marketplaceId)")
    Page<SellerEntity> findAllByFilter(@Param("marketplaceId") Long marketplaceId, Pageable pageable);

    Optional<SellerEntity> findByUserIdAndMarketplace(UUID userId, MarketplaceEnum marketplace);

    @Query("SELECT COUNT(s) > 0 FROM SellerEntity s "
            + "WHERE s.marketplaceUserId = :marketplaceUserId AND s.userId <> :excludeUserId")
    boolean existsByMarketplaceUserIdAndUserIdNot(
            @Param("marketplaceUserId") Long marketplaceUserId,
            @Param("excludeUserId") UUID excludeUserId);
}
