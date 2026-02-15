package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.user.*;
import com.srv.setebit.dropshipping.application.user.dto.request.*;
import com.srv.setebit.dropshipping.application.user.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de login, refresh e logout")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GenerateTemporaryPasswordUseCase generateTemporaryPasswordUseCase;
    private final UnlockUserUseCase unlockUserUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          GenerateTemporaryPasswordUseCase generateTemporaryPasswordUseCase,
                          UnlockUserUseCase unlockUserUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.generateTemporaryPasswordUseCase = generateTemporaryPasswordUseCase;
        this.unlockUserUseCase = unlockUserUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica usuário e retorna tokens")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Renova o access token usando refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = refreshTokenUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalida o refresh token do usuário")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UUID userId) {
        logoutUseCase.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Usuário logado", description = "Retorna dados do usuário autenticado")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UUID userId) {
        UserResponse response = getUserByIdUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Esqueceu senha", description = "Gera uma senha temporária e envia instruções")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        generateTemporaryPasswordUseCase.execute(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/unlock/{userId}")
    @Operation(summary = "Desbloquear usuário", description = "Desbloqueia o usuário (admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminUnlock(@AuthenticationPrincipal UUID adminId, @PathVariable UUID userId) {
        unlockUserUseCase.execute(userId, adminId);
        return ResponseEntity.noContent().build();
    }
}
