package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.user.port.PasswordResetAuditRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PasswordResetRequestEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PasswordResetRequestRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PasswordResetAuditRepositoryAdapter implements PasswordResetAuditRepositoryPort {

    private final PasswordResetRequestRepository jpaRepository;

    public PasswordResetAuditRepositoryAdapter(@Lazy PasswordResetRequestRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public long countByEmailSince(String email, Instant since) {
        return jpaRepository.countByEmailSince(email, since);
    }

    @Override
    public long countByIpSince(String ip, Instant since) {
        return jpaRepository.countByIpSince(ip, since);
    }

    @Override
    public void save(String email, String ip, Instant at) {
        PasswordResetRequestEntity e = new PasswordResetRequestEntity();
        e.setId(UUID.randomUUID());
        e.setEmail(email);
        e.setIp(ip);
        e.setCreatedAt(at);
        jpaRepository.save(e);
    }
}
