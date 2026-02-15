package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.application.access.dto.request.AssignPerfisRequest;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.access.port.UserPerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
public class AssignPerfisToUserUseCase {

    private final UserPerfilRepositoryPort userPerfilRepository;
    private final UserRepositoryPort userRepository;
    private final PerfilRepositoryPort perfilRepository;

    public AssignPerfisToUserUseCase(UserPerfilRepositoryPort userPerfilRepository,
                                    UserRepositoryPort userRepository,
                                    PerfilRepositoryPort perfilRepository) {
        this.userPerfilRepository = userPerfilRepository;
        this.userRepository = userRepository;
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public void execute(UUID userId, AssignPerfisRequest request) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }
        Set<UUID> perfilIds = request.perfilIds() != null ? request.perfilIds() : Collections.emptySet();
        userPerfilRepository.assignPerfisToUser(userId, perfilIds);
    }
}
