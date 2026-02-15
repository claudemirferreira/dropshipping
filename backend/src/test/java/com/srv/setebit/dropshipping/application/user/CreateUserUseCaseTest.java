package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.AssignPerfisToUserUseCase;
import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.request.CreateUserRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.DuplicateEmailException;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private AssignPerfisToUserUseCase assignPerfisToUserUseCase;

    @Mock
    private GetUserPerfisUseCase getUserPerfisUseCase;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest(
                "user@example.com",
                "Senha@123",
                "Joao Silva",
                null,
                null
        );
    }

    @Test
    void deve_criar_usuario_quando_dados_validos() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(getUserPerfisUseCase.execute(any())).thenReturn(Collections.emptyList());

        UserResponse result = createUserUseCase.execute(request);

        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.name()).isEqualTo("Joao Silva");
        assertThat(result.perfilCodes()).isEmpty();
        assertThat(result.active()).isTrue();
        verify(userRepository).save(argThat(u -> "user@example.com".equals(u.getEmail()) && "hashed".equals(u.getPasswordHash())));
    }

    @Test
    void deve_lancar_excecao_quando_email_ja_existe() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> createUserUseCase.execute(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("user@example.com");

        verify(userRepository, never()).save(any());
    }
}
