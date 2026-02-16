package com.srv.setebit.dropshipping.domain.access.port;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserPerfilRepositoryPort {

    /** Retorna os códigos das rotinas acessíveis pelo usuário (via perfis). */
    List<String> findRotinaCodesByUserId(UUID userId);

    /** Retorna os códigos dos perfis do usuário. */
    List<String> findPerfilCodesByUserId(UUID userId);

    /** Retorna os IDs dos perfis do usuário. */
    Set<UUID> findPerfilIdsByUserId(UUID userId);

    /** Substitui os perfis do usuário pelos IDs informados. */
    void assignPerfisToUser(UUID userId, Set<UUID> perfilIds);
}
