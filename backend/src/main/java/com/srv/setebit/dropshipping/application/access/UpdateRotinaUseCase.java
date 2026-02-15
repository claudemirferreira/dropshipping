package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.request.UpdateRotinaRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.DuplicateRotinaCodeException;
import com.srv.setebit.dropshipping.domain.access.exception.RotinaNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateRotinaUseCase {

    private final RotinaRepositoryPort rotinaRepository;

    public UpdateRotinaUseCase(RotinaRepositoryPort rotinaRepository) {
        this.rotinaRepository = rotinaRepository;
    }

    @Transactional
    public RotinaResponse execute(UUID id, UpdateRotinaRequest request) {
        Rotina rotina = rotinaRepository.findById(id)
                .orElseThrow(() -> new RotinaNotFoundException(id));

        if (rotinaRepository.existsByCodeAndIdNot(request.code().trim(), id)) {
            throw new DuplicateRotinaCodeException(request.code());
        }

        rotina.setCode(request.code().trim());
        rotina.setName(request.name().trim());
        rotina.setDescription(request.description() != null ? request.description().trim() : null);
        rotina.setIcon(request.icon() != null ? request.icon().trim() : null);
        rotina.setPath(request.path() != null ? request.path().trim() : null);
        if (request.active() != null) {
            rotina.setActive(request.active());
        }

        rotina = rotinaRepository.save(rotina);
        return toResponse(rotina);
    }

    private RotinaResponse toResponse(Rotina r) {
        return new RotinaResponse(
                r.getId(), r.getCode(), r.getName(), r.getDescription(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
