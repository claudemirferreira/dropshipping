package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.user.dto.request.LoginRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.TokenResponse;
import com.srv.setebit.dropshipping.application.user.port.JwtProviderPort;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.RefreshToken;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.InvalidCredentialsException;
import com.srv.setebit.dropshipping.domain.user.port.BloqueioRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.RefreshTokenRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private JwtProviderPort jwtProvider;

    @Mock
    private GetUserPerfisUseCase getUserPerfisUseCase;

    @Mock
    private BloqueioRepositoryPort bloqueioRepository;

    @Mock
    private TemporaryPasswordRepositoryPort tempPasswordRepository;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User user;
    private LoginRequest request;
    private static final PerfilResponse ADMIN_PERFIL = new PerfilResponse(
            UUID.randomUUID(), "ADMIN", "Administrador", null, true, 0, Collections.emptySet(), Instant.now(), Instant.now());

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@test.com");
        user.setPasswordHash("hashed");
        user.setName("Admin");
        user.setActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        request = new LoginRequest("admin@test.com", "Senha@123");
    }

    @Test
    void deve_retornar_tokens_quando_credenciais_validas() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(getUserPerfisUseCase.execute(user.getId())).thenReturn(List.of(ADMIN_PERFIL));
        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("accessToken");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        TokenResponse result = loginUseCase.execute(request);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_existe() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_senha_invalida() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(tempPasswordRepository.findActiveByUserId(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_inativo() {
        user.setActive(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
