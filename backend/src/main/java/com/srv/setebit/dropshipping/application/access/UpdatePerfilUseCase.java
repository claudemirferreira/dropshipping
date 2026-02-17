package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.request.UpdatePerfilRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.DuplicatePerfilCodeException;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UpdatePerfilUseCase {

    private final PerfilRepositoryPort perfilRepository;

    public UpdatePerfilUseCase(PerfilRepositoryPort perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public PerfilResponse execute(UUID id, UpdatePerfilRequest request) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new PerfilNotFoundException(id));

        if (perfilRepository.existsByCodeAndIdNot(request.code().trim(), id)) {
            throw new DuplicatePerfilCodeException(request.code());
        }

        perfil.setCode(request.code().trim());
        perfil.setName(request.name().trim());
        perfil.setIcon(request.icon() != null ? request.icon().trim() : null);
        if (request.active() != null) {
            perfil.setActive(request.active());
        }

        perfil = perfilRepository.save(perfil);

        if (request.rotinaIds() != null) {
            perfilRepository.replaceRotinasForPerfil(perfil.getId(), request.rotinaIds());
        }

        perfil = perfilRepository.findByIdWithRotinas(perfil.getId()).orElse(perfil);
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

    private RotinaResponse toRotinaResponse(Rotina r) {
        return new RotinaResponse(
                r.getId(), r.getCode(), r.getName(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
