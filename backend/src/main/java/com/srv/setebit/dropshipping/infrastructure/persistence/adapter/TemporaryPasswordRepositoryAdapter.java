package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.user.TemporaryPassword;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.TemporaryPasswordEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.TemporaryPasswordRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class TemporaryPasswordRepositoryAdapter implements TemporaryPasswordRepositoryPort {

    private final TemporaryPasswordRepository jpaRepository;

    public TemporaryPasswordRepositoryAdapter(@Lazy TemporaryPasswordRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TemporaryPassword save(TemporaryPassword temp) {
        TemporaryPasswordEntity entity = toEntity(temp);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<TemporaryPassword> findActiveByUserId(UUID userId) {
        return jpaRepository.findActiveByUserId(userId, Instant.now())
                .map(this::toDomain);
    }

    @Override
    public void markUsed(UUID id) {
        jpaRepository.markUsed(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    private TemporaryPasswordEntity toEntity(TemporaryPassword t) {
        TemporaryPasswordEntity e = new TemporaryPasswordEntity();
        e.setId(t.getId());
        e.setUserId(t.getUserId());
        e.setPasswordHash(t.getPasswordHash());
        e.setExpiresAt(t.getExpiresAt());
        e.setUsed(t.isUsed());
        e.setCreatedAt(t.getCreatedAt());
        return e;
    }

    private TemporaryPassword toDomain(TemporaryPasswordEntity e) {
        TemporaryPassword t = new TemporaryPassword();
        t.setId(e.getId());
        t.setUserId(e.getUserId());
        t.setPasswordHash(e.getPasswordHash());
        t.setExpiresAt(e.getExpiresAt());
        t.setUsed(e.isUsed());
        t.setCreatedAt(e.getCreatedAt());
        return t;
    }
}
