package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.domain.user.port.PasswordResetAuditRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PasswordResetAuditService {

    private final PasswordResetAuditRepositoryPort auditRepository;

    public PasswordResetAuditService(PasswordResetAuditRepositoryPort auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRequest(String email, String ip) {
        auditRepository.save(email, ip, Instant.now());
    }
}
