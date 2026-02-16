package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PerfilEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PerfilJpaRepository;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PerfilRotinaEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PerfilRotinaJpaRepository;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.RotinaEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.RotinaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class PerfilRepositoryAdapter implements PerfilRepositoryPort {

    private final PerfilJpaRepository jpaRepository;
    private final PerfilRotinaJpaRepository perfilRotinaRepository;
    private final RotinaJpaRepository rotinaJpaRepository;
    private final RotinaRepositoryPort rotinaRepository;

    public PerfilRepositoryAdapter(PerfilJpaRepository jpaRepository,
                                   PerfilRotinaJpaRepository perfilRotinaRepository,
                                   RotinaJpaRepository rotinaJpaRepository,
                                   RotinaRepositoryPort rotinaRepository) {
        this.jpaRepository = jpaRepository;
        this.perfilRotinaRepository = perfilRotinaRepository;
        this.rotinaJpaRepository = rotinaJpaRepository;
        this.rotinaRepository = rotinaRepository;
    }

    @Override
    @Transactional
    public Perfil save(Perfil perfil) {
        PerfilEntity entity = toEntity(perfil);
        entity = jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Perfil> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Perfil> findByIdWithRotinas(UUID id) {
        Optional<PerfilEntity> opt = jpaRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();
        PerfilEntity entity = opt.get();
        Perfil perfil = toDomain(entity);
        List<UUID> rotinaIds = perfilRotinaRepository.findRotinaIdsByPerfilId(id);
        if (!rotinaIds.isEmpty()) {
            List<Rotina> rotinas = rotinaRepository.findAllById(rotinaIds);
            perfil.setRotinas(new HashSet<>(rotinas));
        } else {
            perfil.setRotinas(new HashSet<>());
        }
        return Optional.of(perfil);
    }

    @Override
    public Optional<Perfil> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Perfil> findAll(String code, String name, Boolean active, Pageable pageable) {
        Page<Perfil> page = jpaRepository.findAllByFilter(code, name, active, pageable).map(this::toDomain);
        page.getContent().forEach(perfil -> {
            List<UUID> rotinaIds = perfilRotinaRepository.findRotinaIdsByPerfilId(perfil.getId());
            if (!rotinaIds.isEmpty()) {
                perfil.setRotinas(new HashSet<>(rotinaRepository.findAllById(rotinaIds)));
            } else {
                perfil.setRotinas(new HashSet<>());
            }
        });
        return page;
    }

    @Override
    public List<Perfil> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void replaceRotinasForPerfil(UUID perfilId, Set<UUID> rotinaIds) {
        perfilRotinaRepository.deleteByPerfilId(perfilId);
        if (rotinaIds != null && !rotinaIds.isEmpty()) {
            PerfilEntity perfil = jpaRepository.findById(perfilId).orElseThrow();
            for (UUID rid : rotinaIds) {
                PerfilRotinaEntity pr = new PerfilRotinaEntity();
                pr.setId(new PerfilRotinaEntity.PerfilRotinaId(perfilId, rid));
                pr.setPerfil(perfil);
                pr.setRotina(rotinaJpaRepository.getReferenceById(rid));
                perfilRotinaRepository.save(pr);
            }
        }
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

    private PerfilEntity toEntity(Perfil p) {
        PerfilEntity e = new PerfilEntity();
        e.setId(p.getId());
        e.setCode(p.getCode());
        e.setName(p.getName());
        e.setIcon(p.getIcon());
        e.setActive(p.isActive());
        e.setDisplayOrder(p.getDisplayOrder());
        e.setCreatedAt(p.getCreatedAt());
        e.setUpdatedAt(p.getUpdatedAt());
        return e;
    }

    private Perfil toDomain(PerfilEntity e) {
        return new Perfil(
                e.getId(), e.getCode(), e.getName(),
                e.getIcon(), e.isActive(), e.getDisplayOrder(),
                e.getCreatedAt(), e.getUpdatedAt(),
                null
        );
    }
}
