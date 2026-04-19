package com.srv.setebit.dropshipping.application.appconfig;

import com.srv.setebit.dropshipping.domain.appconfig.exception.AppConfigNotFoundException;
import com.srv.setebit.dropshipping.domain.appconfig.port.AppConfigRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteAppConfigUseCase {

    private final AppConfigRepositoryPort appConfigRepository;

    public DeleteAppConfigUseCase(AppConfigRepositoryPort appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    @Transactional
    public void execute(UUID id) {
        if (appConfigRepository.findById(id).isEmpty()) {
            throw new AppConfigNotFoundException(id);
        }
        appConfigRepository.deleteById(id);
    }
}
