package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.appconfig.CreateAppConfigUseCase;
import com.srv.setebit.dropshipping.application.appconfig.DeleteAppConfigUseCase;
import com.srv.setebit.dropshipping.application.appconfig.GetAppConfigByIdUseCase;
import com.srv.setebit.dropshipping.application.appconfig.ListAppConfigsUseCase;
import com.srv.setebit.dropshipping.application.appconfig.UpdateAppConfigUseCase;
import com.srv.setebit.dropshipping.application.appconfig.dto.request.CreateAppConfigRequest;
import com.srv.setebit.dropshipping.application.appconfig.dto.request.UpdateAppConfigRequest;
import com.srv.setebit.dropshipping.application.appconfig.dto.response.AppConfigResponse;
import com.srv.setebit.dropshipping.application.appconfig.dto.response.PageAppConfigResponse;
import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/configs")
@Tag(name = "Config", description = "CRUD de configurações (tipo + payload JSON) — ADMIN/MANAGER")
public class AppConfigController {

    private static final Set<String> CONFIG_SORT_PROPERTIES = Set.of("id", "tipo", "createdAt", "updatedAt");

    private final ListAppConfigsUseCase listAppConfigsUseCase;
    private final CreateAppConfigUseCase createAppConfigUseCase;
    private final GetAppConfigByIdUseCase getAppConfigByIdUseCase;
    private final UpdateAppConfigUseCase updateAppConfigUseCase;
    private final DeleteAppConfigUseCase deleteAppConfigUseCase;

    public AppConfigController(ListAppConfigsUseCase listAppConfigsUseCase,
                               CreateAppConfigUseCase createAppConfigUseCase,
                               GetAppConfigByIdUseCase getAppConfigByIdUseCase,
                               UpdateAppConfigUseCase updateAppConfigUseCase,
                               DeleteAppConfigUseCase deleteAppConfigUseCase) {
        this.listAppConfigsUseCase = listAppConfigsUseCase;
        this.createAppConfigUseCase = createAppConfigUseCase;
        this.getAppConfigByIdUseCase = getAppConfigByIdUseCase;
        this.updateAppConfigUseCase = updateAppConfigUseCase;
        this.deleteAppConfigUseCase = deleteAppConfigUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar configurações")
    public ResponseEntity<PageAppConfigResponse> list(
            @Parameter(description = "Filtro por tipo (código ou nome do enum), opcional")
            @RequestParam(required = false) TipoConfigEnum tipo,
            @PageableDefault(size = 20, sort = "tipo", direction = Sort.Direction.ASC) Pageable pageable) {
        Pageable safePageable = sanitizeSort(pageable, CONFIG_SORT_PROPERTIES, "tipo");
        PageAppConfigResponse response = listAppConfigsUseCase.execute(tipo, safePageable);
        return ResponseEntity.ok(response);
    }

    private Pageable sanitizeSort(Pageable pageable, Set<String> allowedProperties, String defaultProperty) {
        Sort sort = pageable.getSort().stream()
                .filter(order -> allowedProperties.contains(order.getProperty()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Sort::by));
        if (sort.isEmpty()) {
            sort = Sort.by(Sort.Direction.ASC, defaultProperty);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Criar configuração")
    public ResponseEntity<AppConfigResponse> create(@Valid @RequestBody CreateAppConfigRequest request) {
        AppConfigResponse response = createAppConfigUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Buscar configuração por ID")
    public ResponseEntity<AppConfigResponse> getById(@PathVariable UUID id) {
        AppConfigResponse response = getAppConfigByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Atualizar configuração")
    public ResponseEntity<AppConfigResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody UpdateAppConfigRequest request) {
        AppConfigResponse response = updateAppConfigUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Excluir configuração")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteAppConfigUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
