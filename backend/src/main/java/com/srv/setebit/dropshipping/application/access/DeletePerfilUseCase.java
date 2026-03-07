package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.domain.access.exception.PerfilEmUsoException;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.access.port.UserPerfilRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeletePerfilUseCase {

    private final PerfilRepositoryPort perfilRepository;
    private final UserPerfilRepositoryPort userPerfilRepository;

    public DeletePerfilUseCase(PerfilRepositoryPort perfilRepository,
                               UserPerfilRepositoryPort userPerfilRepository) {
        this.perfilRepository = perfilRepository;
        this.userPerfilRepository = userPerfilRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (!perfilRepository.findById(id).isPresent()) {
            throw new PerfilNotFoundException(id);
        }
        if (userPerfilRepository.existsUserWithPerfil(id)) {
            throw new PerfilEmUsoException();
        }
        if (perfilRepository.hasRotinas(id)) {
            throw new PerfilEmUsoException();
        }
        perfilRepository.deleteById(id);
    }
}
