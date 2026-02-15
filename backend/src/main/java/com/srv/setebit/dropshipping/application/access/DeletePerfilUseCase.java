package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeletePerfilUseCase {

    private final PerfilRepositoryPort perfilRepository;

    public DeletePerfilUseCase(PerfilRepositoryPort perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (!perfilRepository.findById(id).isPresent()) {
            throw new PerfilNotFoundException(id);
        }
        perfilRepository.deleteById(id);
    }
}
