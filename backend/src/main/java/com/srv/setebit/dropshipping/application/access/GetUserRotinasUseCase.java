package com.srv.setebit.dropshipping.application.access;

import com.srv.setebit.dropshipping.domain.access.port.UserPerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserRotinasUseCase {

    private final UserPerfilRepositoryPort userPerfilRepository;
    private final UserRepositoryPort userRepository;

    public GetUserRotinasUseCase(UserPerfilRepositoryPort userPerfilRepository,
                                UserRepositoryPort userRepository) {
        this.userPerfilRepository = userPerfilRepository;
        this.userRepository = userRepository;
    }

    public List<String> execute(UUID userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }
        return userPerfilRepository.findRotinaCodesByUserId(userId);
    }
}
