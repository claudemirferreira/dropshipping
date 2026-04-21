package com.srv.setebit.dropshipping.application.appconfig;

import com.srv.setebit.dropshipping.application.appconfig.dto.request.UpdateAppConfigRequest;
import com.srv.setebit.dropshipping.application.appconfig.dto.response.AppConfigResponse;
import com.srv.setebit.dropshipping.domain.appconfig.AppConfig;
import com.srv.setebit.dropshipping.domain.appconfig.exception.AppConfigNotFoundException;
import com.srv.setebit.dropshipping.domain.appconfig.port.AppConfigRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UpdateAppConfigUseCase {

    private final AppConfigRepositoryPort appConfigRepository;

    public UpdateAppConfigUseCase(AppConfigRepositoryPort appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    @Transactional
    public AppConfigResponse execute(UUID id, UpdateAppConfigRequest request) {
        AppConfig config = appConfigRepository.findById(id)
                .orElseThrow(() -> new AppConfigNotFoundException(id));

        Map<String, Object> payload = request.payload() != null
                ? new HashMap<>(request.payload())
                : new HashMap<>();

        config.setTipo(request.tipo());
        config.setPayload(payload);

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
