package com.srv.setebit.dropshipping.infrastructure.config;

import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.UserProfile;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

/**
 * Carga de usuário de teste para desenvolvimento/local.
 * Ative com: spring.profiles.active=dev
 * Usuário: admin@dropshipping.com / Senha@123
 */
@Configuration
@Profile("dev")
public class DevDataLoader {

    public static final String TEST_USER_EMAIL = "admin@dropshipping.com";
    public static final String TEST_USER_PASSWORD = "Senha@123";

    @Bean
    @Order(1)
    ApplicationRunner loadDevUser(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(TEST_USER_EMAIL).isEmpty()) {
                User user = new User();
                user.setId(UUID.randomUUID());
                user.setEmail(TEST_USER_EMAIL);
                user.setPasswordHash(passwordEncoder.encode(TEST_USER_PASSWORD));
                user.setName("Admin Dropshipping");
                user.setPhone(null);
                user.setActive(true);
                user.setProfile(UserProfile.ADMIN);
                Instant now = Instant.now();
                user.setCreatedAt(now);
                user.setUpdatedAt(now);
                userRepository.save(user);
                System.out.println("[DevDataLoader] Usuário de teste criado: " + TEST_USER_EMAIL + " / " + TEST_USER_PASSWORD);
            }
        };
    }
}
