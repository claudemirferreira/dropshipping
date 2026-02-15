package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetPerfilByIdUseCase {

    private final PerfilRepositoryPort perfilRepository;

    public GetPerfilByIdUseCase(PerfilRepositoryPort perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    public PerfilResponse execute(UUID id) {
        Perfil perfil = perfilRepository.findByIdWithRotinas(id)
                .orElseThrow(() -> new PerfilNotFoundException(id));
        return toResponse(perfil);
    }

    private PerfilResponse toResponse(Perfil p) {
        Set<RotinaResponse> rotinas = p.getRotinas() != null
                ? p.getRotinas().stream().map(this::toRotinaResponse).collect(Collectors.toSet())
                : new HashSet<>();
        return new PerfilResponse(
                p.getId(), p.getCode(), p.getName(), p.getDescription(),
                p.getIcon(), p.isActive(), p.getDisplayOrder(), rotinas,
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }

    private RotinaResponse toRotinaResponse(Rotina r) {
        return new RotinaResponse(
                r.getId(), r.getCode(), r.getName(), r.getDescription(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
