package com.srv.setebit.dropshipping.infrastructure.config;

import com.srv.setebit.dropshipping.application.access.AssignPerfisToUserUseCase;
import com.srv.setebit.dropshipping.application.access.dto.request.AssignPerfisRequest;
import com.srv.setebit.dropshipping.domain.access.port.PerfilRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.Product;
import com.srv.setebit.dropshipping.domain.product.ProductImage;
import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import com.srv.setebit.dropshipping.domain.product.port.ProductImageRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
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
    ApplicationRunner loadDevUser(UserRepositoryPort userRepository,
                                  PasswordEncoder passwordEncoder,
                                  PerfilRepositoryPort perfilRepository,
                                  AssignPerfisToUserUseCase assignPerfisToUserUseCase) {
        return args -> {
            if (userRepository.findByEmail(TEST_USER_EMAIL).isEmpty()) {
                User user = new User();
                user.setId(UUID.randomUUID());
                user.setEmail(TEST_USER_EMAIL);
                user.setPasswordHash(passwordEncoder.encode(TEST_USER_PASSWORD));
                user.setName("Admin Dropshipping");
                user.setPhone(null);
                user.setActive(true);
                Instant now = Instant.now();
                user.setCreatedAt(now);
                user.setUpdatedAt(now);
                userRepository.save(user);
                perfilRepository.findByCode("ADMIN").ifPresent(perfil ->
                        assignPerfisToUserUseCase.execute(user.getId(), new AssignPerfisRequest(Set.of(perfil.getId()))));
                System.out.println("[DevDataLoader] Usuário de teste criado: " + TEST_USER_EMAIL + " / " + TEST_USER_PASSWORD);
            }
        };
    }

    @Bean
    @Order(2)
    ApplicationRunner loadDevProducts(ProductRepositoryPort productRepository,
                                      ProductImageRepositoryPort productImageRepository) {
        return args -> {
            if (productRepository.findBySku("PROD-001").isEmpty()) {
                Instant now = Instant.now();
                Product p1 = new Product();
                p1.setId(UUID.randomUUID());
                p1.setSku("PROD-001");
                p1.setName("Camiseta Básica Branca");
                p1.setShortDescription("Camiseta de algodão 100%, básica e confortável");
                p1.setFullDescription("Camiseta básica de algodão 100%, corte reto, ideal para o dia a dia.");
                p1.setSalePrice(new BigDecimal("49.90"));
                p1.setCostPrice(new BigDecimal("25.00"));
                p1.setCurrency("BRL");
                p1.setStatus(ProductStatus.ACTIVE);
                p1.setSlug("camiseta-basica-branca");
                p1.setDropship(true);
                p1.setLeadTimeDays(5);
                p1.setCreatedAt(now);
                p1.setUpdatedAt(now);
                p1 = productRepository.save(p1);

                ProductImage img1 = new ProductImage();
                img1.setId(UUID.randomUUID());
                img1.setProductId(p1.getId());
                img1.setUrl("https://placehold.co/400x400?text=Camiseta");
                img1.setPosition(0);
                img1.setMain(true);
                img1.setAltText("Camiseta Básica Branca");
                productImageRepository.save(img1);

                System.out.println("[DevDataLoader] Produto de teste criado: " + p1.getSku());
            }
        };
    }
}
