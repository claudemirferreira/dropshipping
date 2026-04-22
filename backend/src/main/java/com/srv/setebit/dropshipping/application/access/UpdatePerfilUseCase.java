package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.request.UpdatePerfilRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.DuplicatePerfilCodeException;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilBloqueadoException;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity.PerfilAuditLogEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.repository.PerfilAuditLogJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UpdatePerfilUseCase {

    private final PerfilRepositoryPort perfilRepository;
    private final PerfilAuditLogJpaRepository auditLogRepository;

    public UpdatePerfilUseCase(PerfilRepositoryPort perfilRepository,
                               PerfilAuditLogJpaRepository auditLogRepository) {
        this.perfilRepository = perfilRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public PerfilResponse execute(UUID id, UpdatePerfilRequest request) {

        Perfil perfil = perfilRepository.findByIdWithRotinas(id)
                .orElseThrow(() -> new PerfilNotFoundException(id));

        // Perfis do sistema só permitem alterar rotinas — campos como código, nome e status são bloqueados
        boolean isChangingFields = perfil.isSystemDefault() && (
                !request.code().trim().equals(perfil.getCode()) ||
                !request.name().trim().equals(perfil.getName()) ||
                (request.active() != null && request.active() != perfil.isActive())
        );
        if (isChangingFields) {
            throw new PerfilBloqueadoException(id);
        }

        if (!perfil.isSystemDefault() &&
                perfilRepository.existsByCodeAndIdNot(request.code().trim(), id)) {
            throw new DuplicatePerfilCodeException(request.code());
        }

        String oldCode = perfil.getCode();
        String oldName = perfil.getName();
        boolean oldActive = perfil.isActive();
        Set<String> oldRotinaIds = perfil.getRotinas() != null
                ? perfil.getRotinas().stream().map(r -> r.getId().toString()).collect(Collectors.toSet())
                : Collections.emptySet();

        if (!perfil.isSystemDefault()) {
            perfil.setCode(request.code().trim());
            perfil.setName(request.name().trim());
            perfil.setIcon(request.icon() != null ? request.icon().trim() : null);
            if (request.active() != null) {
                perfil.setActive(request.active());
            }
            perfil = perfilRepository.save(perfil);
        }

        if (request.rotinaIds() != null) {
            perfilRepository.replaceRotinasForPerfil(perfil.getId(), request.rotinaIds());
        }

        perfil = perfilRepository.findByIdWithRotinas(perfil.getId()).orElse(perfil);

        String editor = resolveEditor();
        Instant now = Instant.now();
        List<PerfilAuditLogEntity> logs = new ArrayList<>();

        if (!Objects.equals(oldCode, perfil.getCode())) {
            logs.add(auditEntry(perfil.getId(), editor, now, "code", oldCode, perfil.getCode()));
        }
        if (!Objects.equals(oldName, perfil.getName())) {
            logs.add(auditEntry(perfil.getId(), editor, now, "name", oldName, perfil.getName()));
        }
        if (oldActive != perfil.isActive()) {
            logs.add(auditEntry(perfil.getId(), editor, now, "active",
                    String.valueOf(oldActive), String.valueOf(perfil.isActive())));
        }
        if (request.rotinaIds() != null) {
            Set<String> newIds = request.rotinaIds().stream().map(UUID::toString).collect(Collectors.toSet());
            if (!oldRotinaIds.equals(newIds)) {
                logs.add(auditEntry(perfil.getId(), editor, now, "rotinas",
                        String.join(",", oldRotinaIds), String.join(",", newIds)));
            }
        }

        if (!logs.isEmpty()) {
            auditLogRepository.saveAll(logs);
        }

        return toResponse(perfil);
    }

    private String resolveEditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "system";
    }

    private PerfilAuditLogEntity auditEntry(UUID perfilId, String editedBy, Instant editedAt,
                                             String fieldName, String before, String after) {
        return new PerfilAuditLogEntity(UUID.randomUUID(), perfilId, editedBy, editedAt, fieldName, before, after);
    }

    private PerfilResponse toResponse(Perfil p) {
        Set<RotinaResponse> rotinas = p.getRotinas() != null
                ? p.getRotinas().stream().map(this::toRotinaResponse).collect(Collectors.toSet())
                : new HashSet<>();
        return new PerfilResponse(p.getId(), p.getCode(), p.getName(),
                p.getIcon(), p.isActive(), p.isSystemDefault(), p.getDisplayOrder(), rotinas,
                p.getCreatedAt(), p.getUpdatedAt());
    }

    private RotinaResponse toRotinaResponse(Rotina r) {
        return new RotinaResponse(r.getId(), r.getCode(), r.getName(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt());
    }
}
