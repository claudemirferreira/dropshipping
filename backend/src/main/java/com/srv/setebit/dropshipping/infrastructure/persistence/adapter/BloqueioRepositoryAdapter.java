package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.user.Bloqueio;
import com.srv.setebit.dropshipping.domain.user.BloqueioStatus;
import com.srv.setebit.dropshipping.domain.user.port.BloqueioRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.BloqueioEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.BloqueioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BloqueioRepositoryAdapter implements BloqueioRepositoryPort {

    private final BloqueioRepository jpaRepository;

    public BloqueioRepositoryAdapter(@Lazy BloqueioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Bloqueio save(Bloqueio bloqueio) {
        BloqueioEntity entity = toEntity(bloqueio);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Bloqueio> findActiveByUserId(UUID userId) {
        return jpaRepository.findTopByUserIdAndStatusOrderByDataDoBloqueioDesc(userId, BloqueioStatus.ATIVO)
                .map(this::toDomain);
    }

    @Override
    public void closeActiveByUserId(UUID userId, UUID auditorId, boolean selfService) {
        jpaRepository.closeActiveByUserId(userId, auditorId, selfService, BloqueioStatus.ATIVO, BloqueioStatus.INATIVO);
    }

    private BloqueioEntity toEntity(Bloqueio b) {
        BloqueioEntity e = new BloqueioEntity();
        e.setId(b.getId());
        e.setUserId(b.getUserId());
        e.setLogin(b.getLogin());
        e.setMotivo(b.getMotivo());
        e.setStatus(b.getStatus());
        e.setDataDoBloqueio(b.getDataDoBloqueio());
        e.setDataDoDesbloqueio(b.getDataDoDesbloqueio());
        e.setDataDoUsuarioDesbloqueou(b.getDataDoUsuarioDesbloqueou());
        e.setDesbloqueadoPor(b.getDesbloqueadoPor());
        e.setCreatedAt(b.getCreatedAt());
        return e;
    }

    private Bloqueio toDomain(BloqueioEntity e) {
        Bloqueio b = new Bloqueio();
        b.setId(e.getId());
        b.setUserId(e.getUserId());
        b.setLogin(e.getLogin());
        b.setMotivo(e.getMotivo());
        b.setStatus(e.getStatus());
        b.setDataDoBloqueio(e.getDataDoBloqueio());
        b.setDataDoDesbloqueio(e.getDataDoDesbloqueio());
        b.setDataDoUsuarioDesbloqueou(e.getDataDoUsuarioDesbloqueou());
        b.setDesbloqueadoPor(e.getDesbloqueadoPor());
        b.setCreatedAt(e.getCreatedAt());
        return b;
    }
}
