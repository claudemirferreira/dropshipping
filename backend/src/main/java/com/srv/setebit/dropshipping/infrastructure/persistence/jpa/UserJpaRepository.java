package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import com.srv.setebit.dropshipping.domain.user.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    @Query("SELECT u FROM UserJpaEntity u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:profile IS NULL OR u.profile = :profile)")
    Page<UserJpaEntity> findAllByFilter(@Param("name") String name,
                                        @Param("email") String email,
                                        @Param("profile") UserProfile profile,
                                        Pageable pageable);
}
