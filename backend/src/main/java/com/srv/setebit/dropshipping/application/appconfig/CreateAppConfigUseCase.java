package com.srv.setebit.dropshipping.application.appconfig;

import com.srv.setebit.dropshipping.application.appconfig.dto.request.CreateAppConfigRequest;
import com.srv.setebit.dropshipping.application.appconfig.dto.response.AppConfigResponse;
import com.srv.setebit.dropshipping.domain.appconfig.AppConfig;
import com.srv.setebit.dropshipping.domain.appconfig.port.AppConfigRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateAppConfigUseCase {

    private final AppConfigRepositoryPort appConfigRepository;

    public CreateAppConfigUseCase(AppConfigRepositoryPort appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    @Transactional
    public AppConfigResponse execute(CreateAppConfigRequest request) {
        Instant now = Instant.now();
        Map<String, Object> payload = request.payload() != null
                ? new HashMap<>(request.payload())
                : new HashMap<>();

        AppConfig config = new AppConfig();
        config.setId(UUID.randomUUID());
        config.setTipo(request.tipo());
        config.setPayload(payload);
        config.setCreatedAt(now);
        config.setUpdatedAt(now);

        config = appConfigRepository.save(config);
        return toResponse(config);
    }

    private AppConfigResponse toResponse(AppConfig c) {
        return new AppConfigResponse(
                c.getId(),
                c.getTipo(),
                c.getPayload(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
