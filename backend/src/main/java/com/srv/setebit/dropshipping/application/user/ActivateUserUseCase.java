package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final GetUserPerfisUseCase getUserPerfisUseCase;

    public ActivateUserUseCase(UserRepositoryPort userRepository,
                              GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    @Transactional
    public UserResponse execute(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(true);
        user.setUpdatedAt(Instant.now());
        user = userRepository.save(user);
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        List<String> perfilCodes = getUserPerfisUseCase.execute(user.getId()).stream()
                .map(p -> p.code())
                .collect(Collectors.toList());
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.isActive(),
                perfilCodes,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
