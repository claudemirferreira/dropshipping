package com.srv.setebit.dropshipping.domain.user.port;

import java.time.Instant;

public interface PasswordResetAuditRepositoryPort {
    long countByEmailSince(String email, Instant since);
    long countByIpSince(String ip, Instant since);
    void save(String email, String ip, Instant at);
}
