package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')) AND " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', COALESCE(:email, ''), '%')) AND " +
            "(:perfilCode IS NULL OR :perfilCode = '' OR u.id IN (" +
            "SELECT up.id.userId FROM UserPerfilEntity up JOIN up.perfil p WHERE p.code = :perfilCode))")
    Page<UserEntity> findAllByFilter(@Param("name") String name,
                                     @Param("email") String email,
                                     @Param("perfilCode") String perfilCode,
                                     Pageable pageable);
}
