package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.domain.access.exception.RotinaNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.RotinaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteRotinaUseCase {

    private final RotinaRepositoryPort rotinaRepository;

    public DeleteRotinaUseCase(RotinaRepositoryPort rotinaRepository) {
        this.rotinaRepository = rotinaRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (!rotinaRepository.findById(id).isPresent()) {
            throw new RotinaNotFoundException(id);
        }
        rotinaRepository.deleteById(id);
    }
}
