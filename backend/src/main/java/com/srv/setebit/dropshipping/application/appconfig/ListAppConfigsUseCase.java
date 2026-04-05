package com.srv.setebit.dropshipping.application.appconfig;

import com.srv.setebit.dropshipping.application.appconfig.dto.response.AppConfigResponse;
import com.srv.setebit.dropshipping.application.appconfig.dto.response.PageAppConfigResponse;
import com.srv.setebit.dropshipping.domain.appconfig.AppConfig;
import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import com.srv.setebit.dropshipping.domain.appconfig.port.AppConfigRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListAppConfigsUseCase {

    private final AppConfigRepositoryPort appConfigRepository;

    public ListAppConfigsUseCase(AppConfigRepositoryPort appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    public PageAppConfigResponse execute(TipoConfigEnum tipo, Pageable pageable) {
        Page<AppConfig> page = appConfigRepository.findAll(tipo, pageable);
        return new PageAppConfigResponse(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast()
        );
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
