package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.access.*;
import com.srv.setebit.dropshipping.application.access.dto.request.CreatePerfilRequest;
import com.srv.setebit.dropshipping.application.access.dto.request.UpdatePerfilRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PagePerfilResponse;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/perfis")
@Tag(name = "Perfis", description = "CRUD de perfis (ADMIN/MANAGER)")
public class PerfilController {

    private final ListPerfisUseCase listPerfisUseCase;
    private final CreatePerfilUseCase createPerfilUseCase;
    private final GetPerfilByIdUseCase getPerfilByIdUseCase;
    private final UpdatePerfilUseCase updatePerfilUseCase;
    private final DeletePerfilUseCase deletePerfilUseCase;

    public PerfilController(ListPerfisUseCase listPerfisUseCase,
                            CreatePerfilUseCase createPerfilUseCase,
                            GetPerfilByIdUseCase getPerfilByIdUseCase,
                            UpdatePerfilUseCase updatePerfilUseCase,
                            DeletePerfilUseCase deletePerfilUseCase) {
        this.listPerfisUseCase = listPerfisUseCase;
        this.createPerfilUseCase = createPerfilUseCase;
        this.getPerfilByIdUseCase = getPerfilByIdUseCase;
        this.updatePerfilUseCase = updatePerfilUseCase;
        this.deletePerfilUseCase = deletePerfilUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar perfis")
    public ResponseEntity<PagePerfilResponse> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PagePerfilResponse response = listPerfisUseCase.execute(code, name, active, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Criar perfil")
    public ResponseEntity<PerfilResponse> create(@Valid @RequestBody CreatePerfilRequest request) {
        PerfilResponse response = createPerfilUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Buscar perfil por ID")
    public ResponseEntity<PerfilResponse> getById(@PathVariable UUID id) {
        PerfilResponse response = getPerfilByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Atualizar perfil")
    public ResponseEntity<PerfilResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdatePerfilRequest request) {
        PerfilResponse response = updatePerfilUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Excluir perfil")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deletePerfilUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
