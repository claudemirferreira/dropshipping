package com.srv.setebit.dropshipping.domain.access.port;

import com.srv.setebit.dropshipping.domain.access.Perfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PerfilRepositoryPort {

    Perfil save(Perfil perfil);

    Optional<Perfil> findById(UUID id);

    Optional<Perfil> findByIdWithRotinas(UUID id);

    Optional<Perfil> findByCode(String code);

    Page<Perfil> findAll(String code, String name, Boolean active, Pageable pageable);

    List<Perfil> findAll();

    /** Substitui as rotinas do perfil pelos IDs informados. */
    void replaceRotinasForPerfil(UUID perfilId, Set<UUID> rotinaIds);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    /** Verifica se o perfil possui rotinas associadas. */
    boolean hasRotinas(UUID perfilId);

    void deleteById(UUID id);
}
