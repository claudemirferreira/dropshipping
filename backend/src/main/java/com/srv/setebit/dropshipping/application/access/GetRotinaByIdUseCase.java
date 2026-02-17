package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.RotinaNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetRotinaByIdUseCase {

    private final RotinaRepositoryPort rotinaRepository;

    public GetRotinaByIdUseCase(RotinaRepositoryPort rotinaRepository) {
        this.rotinaRepository = rotinaRepository;
    }

    public RotinaResponse execute(UUID id) {
        Rotina rotina = rotinaRepository.findById(id)
                .orElseThrow(() -> new RotinaNotFoundException(id));
        return toResponse(rotina);
    }

    private RotinaResponse toResponse(Rotina r) {
        return new RotinaResponse(
                r.getId(), r.getCode(), r.getName(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
