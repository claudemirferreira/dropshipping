package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequestEntity, UUID> {

    @Query("SELECT COUNT(r) FROM PasswordResetRequestEntity r WHERE r.email = :email AND r.createdAt > :since")
    long countByEmailSince(@Param("email") String email, @Param("since") Instant since);

    @Query("SELECT COUNT(r) FROM PasswordResetRequestEntity r WHERE r.ip = :ip AND r.createdAt > :since")
    long countByIpSince(@Param("ip") String ip, @Param("since") Instant since);
}
