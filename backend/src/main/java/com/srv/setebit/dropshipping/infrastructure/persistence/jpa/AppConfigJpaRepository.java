package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AppConfigJpaRepository extends JpaRepository<AppConfigEntity, UUID> {

    @Query("SELECT c FROM AppConfigEntity c WHERE "
            + "(:tipo IS NULL OR :tipo = '' OR LOWER(c.tipo) LIKE LOWER(CONCAT('%', :tipo, '%')))")
    Page<AppConfigEntity> findAllByFilter(@Param("tipo") String tipo, Pageable pageable);
}
