package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import com.srv.setebit.dropshipping.domain.user.BloqueioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BloqueioRepository extends JpaRepository<BloqueioEntity, UUID> {
    @Query("SELECT b FROM BloqueioEntity b WHERE b.userId = :userId AND b.status = :status ORDER BY b.dataDoBloqueio DESC")
    Optional<BloqueioEntity> findTopByUserIdAndStatusOrderByDataDoBloqueioDesc(@Param("userId") UUID userId, @Param("status") BloqueioStatus status);

    @Modifying
    @Query("UPDATE BloqueioEntity b SET b.status = :status, b.dataDoDesbloqueio = CURRENT_TIMESTAMP, b.dataDoUsuarioDesbloqueou = CASE WHEN :selfService = true THEN CURRENT_TIMESTAMP ELSE b.dataDoUsuarioDesbloqueou END, b.desbloqueadoPor = :auditorId WHERE b.userId = :userId AND b.status = :fromStatus")
    void closeActiveByUserId(@Param("userId") UUID userId,
                             @Param("auditorId") UUID auditorId,
                             @Param("selfService") boolean selfService,
                             @Param("fromStatus") BloqueioStatus fromStatus,
                             @Param("status") BloqueioStatus status);
}
