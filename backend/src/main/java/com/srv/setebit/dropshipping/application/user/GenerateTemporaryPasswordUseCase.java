package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.user.dto.request.ForgotPasswordRequest;
import com.srv.setebit.dropshipping.application.notification.EmailSenderPort;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.TemporaryPassword;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.RateLimitExceededException;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.PasswordResetAuditRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class GenerateTemporaryPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final TemporaryPasswordRepositoryPort tempPasswordRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailSenderPort emailSender;
    private final PasswordResetAuditRepositoryPort auditRepository;
    private final PasswordResetAuditService auditService;

    @Value("${auth.temp-password-expiration-minutes:15}")
    private int expirationMinutes;

    public GenerateTemporaryPasswordUseCase(UserRepositoryPort userRepository,
                                           TemporaryPasswordRepositoryPort tempPasswordRepository,
                                           PasswordEncoderPort passwordEncoder,
                                           EmailSenderPort emailSender,
                                           PasswordResetAuditRepositoryPort auditRepository,
                                           PasswordResetAuditService auditService) {
        this.userRepository = userRepository;
        this.tempPasswordRepository = tempPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.auditRepository = auditRepository;
        this.auditService = auditService;
    }

    @Value("${auth.forgot-password-rate-limit-window-minutes:15}")
    private int rateLimitWindowMinutes;

    @Value("${auth.forgot-password-rate-limit-per-email:5}")
    private int rateLimitPerEmail;

    @Value("${auth.forgot-password-rate-limit-per-ip:20}")
    private int rateLimitPerIp;

    @Transactional
    public void execute(ForgotPasswordRequest request) {
        execute(request, null);
    }

    @Transactional
    public void execute(ForgotPasswordRequest request, String clientIp) {
        String email = request.email();
        String ip = clientIp != null && !clientIp.isBlank() ? clientIp : "unknown";

        // Log em transação própria para garantir persistência mesmo se ocorrer 429
        auditService.logRequest(email, ip);

        Instant since = Instant.now().minusSeconds(rateLimitWindowMinutes * 60L);
        long byEmail = auditRepository.countByEmailSince(email, since);
        long byIp = auditRepository.countByIpSince(ip, since);
        // Após logar a requisição, aplicamos o limite estrito (>), permitindo exatamente o número configurado
        if (byEmail > rateLimitPerEmail || byIp > rateLimitPerIp) {
            throw new RateLimitExceededException();
        }

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }
        User user = userOpt.get();

        tempPasswordRepository.deleteByUserId(user.getId());

        String tempPassword = generateSecurePassword();
        String hash = passwordEncoder.encode(tempPassword);

        TemporaryPassword temp = new TemporaryPassword();
        temp.setId(UUID.randomUUID());
        temp.setUserId(user.getId());
        temp.setPasswordHash(hash);
        temp.setExpiresAt(Instant.now().plusSeconds(expirationMinutes * 60L));
        temp.setUsed(false);
        temp.setCreatedAt(Instant.now());
        tempPasswordRepository.save(temp);

        System.out.println("Temporary password: " + tempPassword);
        String resetLink = "http://localhost:8080/login?email=" + user.getEmail();
        emailSender.sendTemporaryPassword(user.getEmail(), user.getName(), tempPassword, resetLink);
    }

    private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[9]; // 12 chars Base64 URL-safe approx
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
