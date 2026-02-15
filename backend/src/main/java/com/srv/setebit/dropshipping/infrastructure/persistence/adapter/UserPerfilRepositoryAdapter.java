package com.srv.setebit.dropshipping.infrastructure.persistence.adapter;

import com.srv.setebit.dropshipping.domain.access.port.UserPerfilRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.PerfilJpaRepository;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.UserEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.UserPerfilEntity;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.UserPerfilJpaRepository;
import com.srv.setebit.dropshipping.infrastructure.persistence.jpa.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class UserPerfilRepositoryAdapter implements UserPerfilRepositoryPort {

    private final UserPerfilJpaRepository userPerfilRepository;
    private final UserRepository userRepository;
    private final PerfilJpaRepository perfilJpaRepository;

    public UserPerfilRepositoryAdapter(UserPerfilJpaRepository userPerfilRepository,
                                       UserRepository userRepository,
                                       PerfilJpaRepository perfilJpaRepository) {
        this.userPerfilRepository = userPerfilRepository;
        this.userRepository = userRepository;
        this.perfilJpaRepository = perfilJpaRepository;
    }

    @Override
    public List<String> findRotinaCodesByUserId(UUID userId) {
        return userPerfilRepository.findRotinaCodesByUserId(userId);
    }

    @Override
    public Set<UUID> findPerfilIdsByUserId(UUID userId) {
        return new HashSet<>(userPerfilRepository.findPerfilIdsByUserId(userId));
    }

    @Override
    @Transactional
    public void assignPerfisToUser(UUID userId, Set<UUID> perfilIds) {
        userPerfilRepository.deleteByUserId(userId);
        if (perfilIds != null && !perfilIds.isEmpty()) {
            UserEntity user = userRepository.findById(userId).orElseThrow();
            for (UUID perfilId : perfilIds) {
                UserPerfilEntity up = new UserPerfilEntity();
                up.setId(new UserPerfilEntity.UserPerfilId(userId, perfilId));
                up.setUser(user);
                up.setPerfil(perfilJpaRepository.getReferenceById(perfilId));
                userPerfilRepository.save(up);
            }
        }
    }
}
