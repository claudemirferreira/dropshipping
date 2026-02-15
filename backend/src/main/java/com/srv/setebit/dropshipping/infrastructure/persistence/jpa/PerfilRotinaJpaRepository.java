package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PerfilRotinaJpaRepository extends JpaRepository<PerfilRotinaEntity, PerfilRotinaEntity.PerfilRotinaId> {

    @Modifying
    @Query("DELETE FROM PerfilRotinaEntity pr WHERE pr.id.perfilId = :perfilId")
    void deleteByPerfilId(@Param("perfilId") UUID perfilId);

    @Query("SELECT pr.rotina.id FROM PerfilRotinaEntity pr WHERE pr.id.perfilId = :perfilId")
    List<UUID> findRotinaIdsByPerfilId(@Param("perfilId") UUID perfilId);
}
