package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.appconfig.AppConfig;
import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import com.srv.setebit.dropshipping.domain.appconfig.port.AppConfigRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.AppConfigEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.AppConfigJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppConfigRepositoryAdapter implements AppConfigRepositoryPort {

    private final AppConfigJpaRepository jpaRepository;

    public AppConfigRepositoryAdapter(AppConfigJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AppConfig save(AppConfig config) {
        AppConfigEntity entity = toEntity(config);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<AppConfig> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<AppConfig> findAll(TipoConfigEnum tipo, Pageable pageable) {
        String tipoFilter = tipo != null ? tipo.getCodigo() : null;
        return jpaRepository.findAllByFilter(tipoFilter, pageable).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private AppConfigEntity toEntity(AppConfig c) {
        AppConfigEntity e = new AppConfigEntity();
        e.setId(c.getId());
        e.setTipo(c.getTipo() != null ? c.getTipo().getCodigo() : null);
        e.setPayload(c.getPayload() != null ? new HashMap<>(c.getPayload()) : new HashMap<>());
        e.setCreatedAt(c.getCreatedAt());
        e.setUpdatedAt(c.getUpdatedAt());
        return e;
    }

    private AppConfig toDomain(AppConfigEntity e) {
        Map<String, Object> payload = e.getPayload() != null ? new HashMap<>(e.getPayload()) : new HashMap<>();
        return new AppConfig(
                e.getId(),
                TipoConfigEnum.fromCodigo(e.getTipo()),
                payload,
                e.getCreatedAt(),
                e.getUpdatedAt());
    }
}
