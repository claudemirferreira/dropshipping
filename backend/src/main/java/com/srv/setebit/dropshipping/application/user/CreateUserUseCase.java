package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.AssignPerfisToUserUseCase;
import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.request.CreateUserRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.DuplicateEmailException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final AssignPerfisToUserUseCase assignPerfisToUserUseCase;
    private final GetUserPerfisUseCase getUserPerfisUseCase;

    public CreateUserUseCase(UserRepositoryPort userRepository,
                             PasswordEncoderPort passwordEncoder,
                             AssignPerfisToUserUseCase assignPerfisToUserUseCase,
                             GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.assignPerfisToUserUseCase = assignPerfisToUserUseCase;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        Instant now = Instant.now();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.email().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setName(request.name().trim());
        user.setPhone(request.phone() != null ? request.phone().trim() : null);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        user = userRepository.save(user);

        if (request.perfilIds() != null && !request.perfilIds().isEmpty()) {
            assignPerfisToUserUseCase.execute(user.getId(), new com.srv.setebit.dropshipping.application.access.dto.request.AssignPerfisRequest(Set.copyOf(request.perfilIds())));
        }

        List<String> perfilCodes = getUserPerfisUseCase.execute(user.getId()).stream()
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
                perfilCodes != null ? perfilCodes : Collections.emptyList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
