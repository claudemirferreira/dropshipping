package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.AssignPerfisToUserUseCase;
import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.access.dto.request.AssignPerfisRequest;
import com.srv.setebit.dropshipping.application.user.dto.request.CreateUserRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.TemporaryPassword;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.DuplicateEmailException;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
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
    private final TemporaryPasswordRepositoryPort tempPasswordRepository;
    private final AssignPerfisToUserUseCase assignPerfisToUserUseCase;
    private final GetUserPerfisUseCase getUserPerfisUseCase;

    /** Expiração da senha provisória criada junto com o usuário (padrão 72h). */
    @Value("${auth.new-user-temp-password-expiration-hours:72}")
    private int newUserTempPasswordExpirationHours;

    public CreateUserUseCase(UserRepositoryPort userRepository,
                             PasswordEncoderPort passwordEncoder,
                             TemporaryPasswordRepositoryPort tempPasswordRepository,
                             AssignPerfisToUserUseCase assignPerfisToUserUseCase,
                             GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tempPasswordRepository = tempPasswordRepository;
        this.assignPerfisToUserUseCase = assignPerfisToUserUseCase;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        Instant now = Instant.now();
        User user = buildNewUser(request, now);
        user = userRepository.save(user);

        saveProvisionalPassword(user.getId(), request.password(), now);

        if (request.perfilIds() != null && !request.perfilIds().isEmpty()) {
            assignPerfisToUserUseCase.execute(user.getId(),
                    new AssignPerfisRequest(Set.copyOf(request.perfilIds())));
        }

        List<String> perfilCodes = getUserPerfisUseCase.execute(user.getId()).stream()
                .map(p -> p.code())
                .collect(Collectors.toList());

        return toResponse(user, perfilCodes);
    }

    /**
     * Constrói o usuário com um hash inutilizável em password_hash.
     * A senha real fica em senhas_temporarias, forçando a troca no primeiro login.
     */
    private User buildNewUser(CreateUserRequest request, Instant now) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.email().toLowerCase().trim());
        // Hash aleatório — impossível de autenticar pela rota normal.
        // O usuário só consegue logar via senha provisória (senhas_temporarias).
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setName(request.name().trim());
        user.setPhone(request.phone() != null ? request.phone().trim() : null);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    /** Persiste a senha provisória fornecida pelo admin em senhas_temporarias. */
    private void saveProvisionalPassword(UUID userId, String rawPassword, Instant now) {
        TemporaryPassword temp = new TemporaryPassword();
        temp.setId(UUID.randomUUID());
        temp.setUserId(userId);
        temp.setPasswordHash(passwordEncoder.encode(rawPassword));
        temp.setExpiresAt(now.plusSeconds(newUserTempPasswordExpirationHours * 3600L));
        temp.setUsed(false);
        temp.setCreatedAt(now);
        tempPasswordRepository.save(temp);
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
