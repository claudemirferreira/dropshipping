package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.repository;

import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity.PerfilAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PerfilAuditLogJpaRepository extends JpaRepository<PerfilAuditLogEntity, UUID> {
}
