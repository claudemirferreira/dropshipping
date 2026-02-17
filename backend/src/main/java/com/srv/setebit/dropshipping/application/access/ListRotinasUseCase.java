package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.response.PageRotinaResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import com.srv.setebit.dropshipping.domain.access.Rotina;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListRotinasUseCase {

    private final RotinaRepositoryPort rotinaRepository;

    public ListRotinasUseCase(RotinaRepositoryPort rotinaRepository) {
        this.rotinaRepository = rotinaRepository;
    }

    public PageRotinaResponse execute(String code, String name, Boolean active, Pageable pageable) {
        Page<Rotina> page = rotinaRepository.findAll(code, name, active, pageable);
        return new PageRotinaResponse(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
    }

    private RotinaResponse toResponse(Rotina r) {
        return new RotinaResponse(
                r.getId(), r.getCode(), r.getName(),
                r.getIcon(), r.getPath(), r.isActive(), r.getDisplayOrder(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
