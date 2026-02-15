package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.access.*;
import com.srv.setebit.dropshipping.application.access.dto.request.CreateRotinaRequest;
import com.srv.setebit.dropshipping.application.access.dto.request.UpdateRotinaRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PageRotinaResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.RotinaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rotinas")
@Tag(name = "Rotinas", description = "CRUD de rotinas (ADMIN/MANAGER)")
public class RotinaController {

    private final ListRotinasUseCase listRotinasUseCase;
    private final CreateRotinaUseCase createRotinaUseCase;
    private final GetRotinaByIdUseCase getRotinaByIdUseCase;
    private final UpdateRotinaUseCase updateRotinaUseCase;
    private final DeleteRotinaUseCase deleteRotinaUseCase;

    public RotinaController(ListRotinasUseCase listRotinasUseCase,
                            CreateRotinaUseCase createRotinaUseCase,
                            GetRotinaByIdUseCase getRotinaByIdUseCase,
                            UpdateRotinaUseCase updateRotinaUseCase,
                            DeleteRotinaUseCase deleteRotinaUseCase) {
        this.listRotinasUseCase = listRotinasUseCase;
        this.createRotinaUseCase = createRotinaUseCase;
        this.getRotinaByIdUseCase = getRotinaByIdUseCase;
        this.updateRotinaUseCase = updateRotinaUseCase;
        this.deleteRotinaUseCase = deleteRotinaUseCase;
    }

    private static final Set<String> ROTINA_SORT_PROPERTIES = Set.of(
            "id", "code", "name", "description", "icon", "path", "active", "createdAt", "updatedAt");

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar rotinas")
    public ResponseEntity<PageRotinaResponse> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Pageable safePageable = sanitizeSort(pageable, ROTINA_SORT_PROPERTIES, "name");
        PageRotinaResponse response = listRotinasUseCase.execute(code, name, active, safePageable);
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
    @Operation(summary = "Criar rotina")
    public ResponseEntity<RotinaResponse> create(@Valid @RequestBody CreateRotinaRequest request) {
        RotinaResponse response = createRotinaUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Buscar rotina por ID")
    public ResponseEntity<RotinaResponse> getById(@PathVariable UUID id) {
        RotinaResponse response = getRotinaByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Atualizar rotina")
    public ResponseEntity<RotinaResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateRotinaRequest request) {
        RotinaResponse response = updateRotinaUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Excluir rotina")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteRotinaUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
