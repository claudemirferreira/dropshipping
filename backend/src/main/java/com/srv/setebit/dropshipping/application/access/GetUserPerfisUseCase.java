package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.access.port.UserPerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetUserPerfisUseCase {

    private final UserPerfilRepositoryPort userPerfilRepository;
    private final PerfilRepositoryPort perfilRepository;
    private final UserRepositoryPort userRepository;

    public GetUserPerfisUseCase(UserPerfilRepositoryPort userPerfilRepository,
                               PerfilRepositoryPort perfilRepository,
                               UserRepositoryPort userRepository) {
        this.userPerfilRepository = userPerfilRepository;
        this.perfilRepository = perfilRepository;
        this.userRepository = userRepository;
    }

    public List<PerfilResponse> execute(UUID userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }
        Set<UUID> perfilIds = userPerfilRepository.findPerfilIdsByUserId(userId);
        return perfilIds.stream()
                .map(perfilRepository::findByIdWithRotinas)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .map(this::toResponse)
                .sorted((a, b) -> Integer.compare(a.displayOrder(), b.displayOrder()))
                .toList();
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
