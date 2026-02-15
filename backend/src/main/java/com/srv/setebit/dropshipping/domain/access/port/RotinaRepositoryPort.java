package com.srv.setebit.dropshipping.domain.access.port;

import com.srv.setebit.dropshipping.domain.access.Rotina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RotinaRepositoryPort {

    Rotina save(Rotina rotina);

    Optional<Rotina> findById(UUID id);

    Optional<Rotina> findByCode(String code);

    Page<Rotina> findAll(String code, String name, Boolean active, Pageable pageable);

    List<Rotina> findAllById(Iterable<UUID> ids);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    void deleteById(UUID id);
}
