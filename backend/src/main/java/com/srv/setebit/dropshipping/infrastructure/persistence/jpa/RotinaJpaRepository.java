package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RotinaJpaRepository extends JpaRepository<RotinaEntity, UUID> {

    Optional<RotinaEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    List<RotinaEntity> findAllByIdIn(List<UUID> ids);

    @Query("SELECT r FROM RotinaEntity r WHERE " +
            "(:code IS NULL OR :code = '' OR LOWER(r.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
            "(:name IS NULL OR :name = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:active IS NULL OR r.active = :active)")
    Page<RotinaEntity> findAllByFilter(@Param("code") String code,
                                       @Param("name") String name,
                                       @Param("active") Boolean active,
                                       Pageable pageable);
}
