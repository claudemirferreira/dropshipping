package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserPerfilJpaRepository extends JpaRepository<UserPerfilEntity, UserPerfilEntity.UserPerfilId> {

    @Query("SELECT up.perfil.id FROM UserPerfilEntity up WHERE up.id.userId = :userId")
    List<UUID> findPerfilIdsByUserId(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT r.code FROM UserPerfilEntity up " +
            "JOIN PerfilRotinaEntity pr ON pr.id.perfilId = up.perfil.id " +
            "JOIN RotinaEntity r ON r.id = pr.rotina.id " +
            "WHERE up.id.userId = :userId AND r.active = true")
    List<String> findRotinaCodesByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM UserPerfilEntity up WHERE up.id.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
