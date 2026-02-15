package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PerfilJpaRepository extends JpaRepository<PerfilEntity, UUID> {

    Optional<PerfilEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    @Query("SELECT p FROM PerfilEntity p WHERE " +
            "(:code IS NULL OR :code = '' OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
            "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:active IS NULL OR p.active = :active)")
    Page<PerfilEntity> findAllByFilter(@Param("code") String code,
                                       @Param("name") String name,
                                       @Param("active") Boolean active,
                                       Pageable pageable);
}
