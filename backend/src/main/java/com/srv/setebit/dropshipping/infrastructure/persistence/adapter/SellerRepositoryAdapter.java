package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.seller.Seller;
import com.srv.setebit.dropshipping.domain.seller.port.SellerRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity.SellerEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.repository.SellerJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SellerRepositoryAdapter implements SellerRepositoryPort {

    private final SellerJpaRepository jpaRepository;

    public SellerRepositoryAdapter(SellerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Seller save(Seller seller) {
        SellerEntity entity = toEntity(seller);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Seller> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<Seller> findAll(Long marketplaceId, Pageable pageable) {
        return jpaRepository.findAllByFilter(marketplaceId, pageable).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private SellerEntity toEntity(Seller s) {
        SellerEntity e = new SellerEntity();
        e.setId(s.getId());
        e.setUserId(s.getUserId());
        e.setAccessToken(s.getAccessToken());
        e.setTokenType(s.getTokenType());
        e.setExpiresIn(s.getExpiresIn());
        e.setScope(s.getScope());
        e.setMarketplaceId(s.getMarketplaceId());
        e.setMarketplace(s.getMarketplace());
        e.setRefreshToken(s.getRefreshToken());
        e.setCreatedAt(s.getCreatedAt());
        e.setUpdatedAt(s.getUpdatedAt());
        return e;
    }

    private Seller toDomain(SellerEntity e) {
        return new Seller(
                e.getId(),
                e.getUserId(),
                e.getAccessToken(),
                e.getTokenType(),
                e.getExpiresIn(),
                e.getScope(),
                e.getMarketplaceId(),
                e.getMarketplace(),
                e.getRefreshToken(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}
