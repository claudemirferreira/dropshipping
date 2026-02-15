package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TemporaryPasswordRepository extends JpaRepository<TemporaryPasswordEntity, UUID> {

    @Query("SELECT t FROM TemporaryPasswordEntity t WHERE t.userId = :userId AND t.used = false AND t.expiresAt > :now ORDER BY t.createdAt DESC")
    Optional<TemporaryPasswordEntity> findActiveByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE TemporaryPasswordEntity t SET t.used = true WHERE t.id = :id")
    void markUsed(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM TemporaryPasswordEntity t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
