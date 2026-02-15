package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.request.CreateRotinaRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.exception.DuplicateRotinaCodeException;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CreateRotinaUseCase {

    private final RotinaRepositoryPort rotinaRepository;

    public CreateRotinaUseCase(RotinaRepositoryPort rotinaRepository) {
        this.rotinaRepository = rotinaRepository;
    }

    @Transactional
    public RotinaResponse execute(CreateRotinaRequest request) {
        if (rotinaRepository.existsByCode(request.code().trim())) {
            throw new DuplicateRotinaCodeException(request.code());
        }

        Instant now = Instant.now();
        Rotina rotina = new Rotina();
        rotina.setId(UUID.randomUUID());
        rotina.setCode(request.code().trim());
        rotina.setName(request.name().trim());
        rotina.setDescription(request.description() != null ? request.description().trim() : null);
        rotina.setIcon(request.icon() != null ? request.icon().trim() : null);
        rotina.setPath(request.path() != null ? request.path().trim() : null);
        rotina.setActive(request.active() != null ? request.active() : true);
        rotina.setDisplayOrder(0);
        rotina.setCreatedAt(now);
        rotina.setUpdatedAt(now);

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
