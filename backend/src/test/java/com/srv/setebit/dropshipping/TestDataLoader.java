package com.srv.setebit.dropshipping;

import com.srv.setebit.dropshipping.application.access.AssignPerfisToUserUseCase;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Carrega dados de teste apenas no perfil "test".
 * Usa PasswordEncoder para gerar hashes bcrypt corretos.
 * Atribui um perfil por usuário conforme o código (ADMIN, MANAGER, etc.).
 */
@Configuration
@Profile("test")
public class TestDataLoader {

    private static final String TEST_PASSWORD = "Senha@123";

    @Bean
    CommandLineRunner loadTestData(UserRepositoryPort userRepository,
                                  PasswordEncoder passwordEncoder,
                                  PerfilRepositoryPort perfilRepository,
                                  AssignPerfisToUserUseCase assignPerfisToUserUseCase) {
        return args -> {
            if (userRepository.findByEmail("admin@test.com").isEmpty()) {
                String hash = passwordEncoder.encode(TEST_PASSWORD);
                Instant now = Instant.now();
                saveUser(userRepository, perfilRepository, assignPerfisToUserUseCase,
                        UUID.fromString("11111111-1111-1111-1111-111111111101"), "admin@test.com", hash, "Admin Sistema", "ADMIN", now);
                saveUser(userRepository, perfilRepository, assignPerfisToUserUseCase,
                        UUID.fromString("11111111-1111-1111-1111-111111111102"), "manager@test.com", hash, "Manager Vendas", "MANAGER", now);
                saveUser(userRepository, perfilRepository, assignPerfisToUserUseCase,
                        UUID.fromString("11111111-1111-1111-1111-111111111103"), "seller@test.com", hash, "Vendedor Exemplo", "SELLER", now);
                saveUser(userRepository, perfilRepository, assignPerfisToUserUseCase,
                        UUID.fromString("11111111-1111-1111-1111-111111111104"), "operator@test.com", hash, "Operador Sistema", "OPERATOR", now);
            }
        };
    }

    private void saveUser(UserRepositoryPort repo,
                         PerfilRepositoryPort perfilRepository, AssignPerfisToUserUseCase assignPerfisToUserUseCase,
                         UUID id, String email, String hash, String name, String perfilCode, Instant now) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setName(name);
        user.setPhone(null);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        repo.save(user);
        perfilRepository.findByCode(perfilCode).ifPresent(perfil ->
                assignPerfisToUserUseCase.execute(id, new com.srv.setebit.dropshipping.application.access.dto.request.AssignPerfisRequest(Set.of(perfil.getId()))));
    }
}
