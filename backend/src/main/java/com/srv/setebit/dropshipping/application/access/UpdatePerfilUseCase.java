package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.domain.access.Perfil;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdatePerfilUseCase {

    private final PerfilRepositoryPort perfilRepository;

    public UpdatePerfilUseCase(PerfilRepositoryPort perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public Perfil execute(UUID id, Perfil request) {
        validate(id);
        request.setId(id);
        request.update();
        return perfilRepository.save(request);
    }

    private void validate(UUID id) {
        perfilRepository.findById(id).orElseThrow(() -> new PerfilNotFoundException(id));
    }

}
