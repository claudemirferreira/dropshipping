package com.srv.setebit.dropshipping.domain.appconfig.port;

import com.srv.setebit.dropshipping.domain.appconfig.AppConfig;
import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AppConfigRepositoryPort {

    AppConfig save(AppConfig config);

    Optional<AppConfig> findById(UUID id);

    Page<AppConfig> findAll(TipoConfigEnum tipo, Pageable pageable);

    void deleteById(UUID id);
}
