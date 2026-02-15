package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import com.srv.setebit.dropshipping.domain.user.BloqueioStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bloqueio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueioEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "login", nullable = false, length = 255)
    private String login;

    @Column(name = "motivo", nullable = false, length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BloqueioStatus status;

    @Column(name = "data_do_bloqueio", nullable = false)
    private Instant dataDoBloqueio;

    @Column(name = "data_do_desbloqueio")
    private Instant dataDoDesbloqueio;

    @Column(name = "data_do_usuario_desbloqueou")
    private Instant dataDoUsuarioDesbloqueou;

    @Column(name = "desbloqueado_por")
    private UUID desbloqueadoPor;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
