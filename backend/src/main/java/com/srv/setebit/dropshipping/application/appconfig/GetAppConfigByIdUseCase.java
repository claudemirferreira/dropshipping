package com.srv.setebit.dropshipping.application.appconfig;

import com.srv.setebit.dropshipping.application.appconfig.dto.response.AppConfigResponse;
import com.srv.setebit.dropshipping.domain.appconfig.AppConfig;
import com.srv.setebit.dropshipping.domain.appconfig.exception.AppConfigNotFoundException;
import com.srv.setebit.dropshipping.domain.appconfig.port.AppConfigRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetAppConfigByIdUseCase {

    private final AppConfigRepositoryPort appConfigRepository;

    public GetAppConfigByIdUseCase(AppConfigRepositoryPort appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    public AppConfigResponse execute(UUID id) {
        AppConfig config = appConfigRepository.findById(id)
                .orElseThrow(() -> new AppConfigNotFoundException(id));
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
