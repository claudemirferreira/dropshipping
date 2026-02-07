package com.srv.setebit.dropshipping.application.user.dto.request;

import com.srv.setebit.dropshipping.domain.user.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para criação de usuário")
public record CreateUserRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Schema(description = "Email do usuário", example = "user@example.com", required = true)
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "Senha deve conter pelo menos 1 maiúscula, 1 minúscula, 1 número e 1 caractere especial")
        @Schema(description = "Senha do usuário", example = "Senha@123", required = true)
        String password,

        @NotBlank(message = "Nome é obrigatório")
        @Length(max = 255)
        @Schema(description = "Nome completo", example = "João Silva", required = true)
        String name,

        @Length(max = 50)
        @Schema(description = "Telefone")
        String phone,

        @NotNull(message = "Perfil é obrigatório")
        @Schema(description = "Perfil do usuário", required = true)
        UserProfile profile
) {
}
