package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.request.CreatePerfilRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.DuplicatePerfilCodeException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreatePerfilUseCase {

    private final PerfilRepositoryPort perfilRepository;

    public CreatePerfilUseCase(PerfilRepositoryPort perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public PerfilResponse execute(CreatePerfilRequest request) {
        if (perfilRepository.existsByCode(request.code().trim())) {
            throw new DuplicatePerfilCodeException(request.code());
        }

        Instant now = Instant.now();
        Perfil perfil = new Perfil();
        perfil.setId(UUID.randomUUID());
        perfil.setCode(request.code().trim());
        perfil.setName(request.name().trim());
        perfil.setIcon(request.icon() != null ? request.icon().trim() : null);
        perfil.setActive(request.active() != null ? request.active() : true);
        perfil.setDisplayOrder(0);
        perfil.setCreatedAt(now);
        perfil.setUpdatedAt(now);
        perfil.setRotinas(new HashSet<>());

        perfil = perfilRepository.save(perfil);

        if (request.rotinaIds() != null && !request.rotinaIds().isEmpty()) {
            perfilRepository.replaceRotinasForPerfil(perfil.getId(), request.rotinaIds());
            perfil = perfilRepository.findByIdWithRotinas(perfil.getId()).orElse(perfil);
        }

        return toResponse(perfil);
    }

    private PerfilResponse toResponse(Perfil p) {
        Set<RotinaResponse> rotinas = p.getRotinas() != null
                ? p.getRotinas().stream().map(this::toRotinaResponse).collect(Collectors.toSet())
                : new HashSet<>();
        return new PerfilResponse(
                p.getId(), p.getCode(), p.getName(),
                p.getIcon(), p.isActive(), p.getDisplayOrder(), rotinas,
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }

    private com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse toRotinaResponse(Rotina r) {
        return new com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse(
                r.getId(), r.getCode(), r.getName(), r.getDescription(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
