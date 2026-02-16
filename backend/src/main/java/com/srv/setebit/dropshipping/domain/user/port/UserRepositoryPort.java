package com.srv.setebit.dropshipping.domain.user.port;

import com.srv.setebit.dropshipping.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByFilter(String name, String email, String perfilCode, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    void delete(User user);
}
