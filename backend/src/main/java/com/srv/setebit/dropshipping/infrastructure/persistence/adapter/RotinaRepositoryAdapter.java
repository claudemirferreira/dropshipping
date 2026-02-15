package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.RotinaEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.RotinaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
public class RotinaRepositoryAdapter implements RotinaRepositoryPort {

    private final RotinaJpaRepository jpaRepository;

    public RotinaRepositoryAdapter(RotinaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Rotina save(Rotina rotina) {
        RotinaEntity entity = toEntity(rotina);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Rotina> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Rotina> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public Page<Rotina> findAll(String code, String name, Boolean active, Pageable pageable) {
        return jpaRepository.findAllByFilter(code, name, active, pageable).map(this::toDomain);
    }

    @Override
    public List<Rotina> findAllById(Iterable<UUID> ids) {
        List<UUID> idList = StreamSupport.stream(ids.spliterator(), false).toList();
        return jpaRepository.findAllByIdIn(idList).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, UUID id) {
        return jpaRepository.existsByCodeAndIdNot(code, id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private RotinaEntity toEntity(Rotina r) {
        RotinaEntity e = new RotinaEntity();
        e.setId(r.getId());
        e.setCode(r.getCode());
        e.setName(r.getName());
        e.setDescription(r.getDescription());
        e.setIcon(r.getIcon());
        e.setPath(r.getPath());
        e.setActive(r.isActive());
        e.setDisplayOrder(r.getDisplayOrder());
        e.setCreatedAt(r.getCreatedAt());
        e.setUpdatedAt(r.getUpdatedAt());
        return e;
    }

    private Rotina toDomain(RotinaEntity e) {
        return new Rotina(
                e.getId(), e.getCode(), e.getName(), e.getDescription(),
                e.getIcon(), e.getPath(), e.isActive(),
                e.getDisplayOrder(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
