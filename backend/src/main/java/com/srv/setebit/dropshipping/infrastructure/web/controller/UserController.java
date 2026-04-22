package com.srv.setebit.dropshipping.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srv.setebit.dropshipping.application.access.AssignPerfisToUserUseCase;
import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.access.GetUserRotinasUseCase;
import com.srv.setebit.dropshipping.application.access.dto.request.AssignPerfisRequest;
import com.srv.setebit.dropshipping.application.access.dto.response.PerfilResponse;
import com.srv.setebit.dropshipping.application.user.ActivateUserUseCase;
import com.srv.setebit.dropshipping.application.user.ChangePasswordUseCase;
import com.srv.setebit.dropshipping.application.user.CreateUserUseCase;
import com.srv.setebit.dropshipping.application.user.DeactivateUserUseCase;
import com.srv.setebit.dropshipping.application.user.GetUserByIdUseCase;
import com.srv.setebit.dropshipping.application.user.ListUsersUseCase;
import com.srv.setebit.dropshipping.application.user.UpdateUserUseCase;
import com.srv.setebit.dropshipping.application.user.dto.request.ChangePasswordRequest;
import com.srv.setebit.dropshipping.application.user.dto.request.CreateUserRequest;
import com.srv.setebit.dropshipping.application.user.dto.request.UpdateUserRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.PageUserResponse;
import com.srv.setebit.dropshipping.application.user.dto.response.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "CRUD de usuários (ADMIN/MANAGER)")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final GetUserPerfisUseCase getUserPerfisUseCase;
    private final AssignPerfisToUserUseCase assignPerfisToUserUseCase;
    private final GetUserRotinasUseCase getUserRotinasUseCase;

    private static final Set<String> USER_SORT_PROPERTIES = Set.of(
            "id", "email", "name", "phone", "active",
            "createdAt", "updatedAt",
            "failedLoginAttempts", "locked", "lockedReason", "lockedAt", "unlockedAt");

    public UserController(CreateUserUseCase createUserUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            UpdateUserUseCase updateUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ListUsersUseCase listUsersUseCase,
            ActivateUserUseCase activateUserUseCase,
            DeactivateUserUseCase deactivateUserUseCase,
            GetUserPerfisUseCase getUserPerfisUseCase,
            AssignPerfisToUserUseCase assignPerfisToUserUseCase,
            GetUserRotinasUseCase getUserRotinasUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
        this.assignPerfisToUserUseCase = assignPerfisToUserUseCase;
        this.getUserRotinasUseCase = getUserRotinasUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar usuário")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = createUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuários")
    public ResponseEntity<PageUserResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String perfilCode,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Pageable safePageable = sanitizeSort(pageable, USER_SORT_PROPERTIES, "name");
        PageUserResponse response = listUsersUseCase.execute(name, email, perfilCode, safePageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perfis")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar perfis do usuário autenticado")
    public ResponseEntity<List<PerfilResponse>> getMyPerfis(@AuthenticationPrincipal UUID userId) {
        List<PerfilResponse> response = getUserPerfisUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        UserResponse response = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UUID userId) {
        UserResponse response = updateUserUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Alterar senha")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UUID userId) {
        if (!id.equals(userId) && !isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        changePasswordUseCase.execute(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativar usuário")
    public ResponseEntity<UserResponse> activate(@PathVariable UUID id) {
        UserResponse response = activateUserUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<UserResponse> deactivate(@PathVariable UUID id) {
        UserResponse response = deactivateUserUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/perfis")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @Operation(summary = "Listar perfis do usuário")
    public ResponseEntity<List<PerfilResponse>> getPerfis(@PathVariable UUID id) {
        List<PerfilResponse> response = getUserPerfisUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/perfis")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atribuir perfis ao usuário")
    public ResponseEntity<Void> assignPerfis(@PathVariable UUID id,
            @Valid @RequestBody AssignPerfisRequest request) {
        assignPerfisToUserUseCase.execute(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/rotinas")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @Operation(summary = "Listar rotinas acessíveis pelo usuário")
    public ResponseEntity<List<String>> getRotinas(@PathVariable UUID id) {
        List<String> response = getUserRotinasUseCase.execute(id);
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

    private boolean isAdmin(UUID userId) {
        return getUserPerfisUseCase.execute(userId).stream()
                .map(PerfilResponse::code)
                .anyMatch("ADMIN"::equals);
    }
}
