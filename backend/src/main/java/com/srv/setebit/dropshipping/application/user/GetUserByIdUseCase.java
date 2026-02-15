package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetUserByIdUseCase {

    private final UserRepositoryPort userRepository;
    private final GetUserPerfisUseCase getUserPerfisUseCase;

    public GetUserByIdUseCase(UserRepositoryPort userRepository, GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    public UserResponse execute(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        List<String> perfilCodes = getUserPerfisUseCase.execute(id).stream()
                .map(p -> p.code())
                .collect(Collectors.toList());
        return toResponse(user, perfilCodes);
    }

    private UserResponse toResponse(User user, List<String> perfilCodes) {
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
