package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Registra cada edição de perfil com: quem editou, quando, campo alterado e valores antes/depois.
 */
@Entity
@Table(name = "perfil_audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAuditLogEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** ID do perfil que foi editado. */
    @Column(name = "perfil_id", nullable = false)
    private UUID perfilId;

    /** E-mail ou identificador do usuário que realizou a edição. */
    @Column(name = "edited_by", nullable = false, length = 255)
    private String editedBy;

    /** Momento exato da edição. */
    @Column(name = "edited_at", nullable = false)
    private Instant editedAt;

    /** Nome do campo que foi alterado (ex: "name", "active", "rotinas"). */
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    /** Valor antes da alteração (pode ser null se era vazio). */
    @Column(name = "value_before", columnDefinition = "TEXT")
    private String valueBefore;

    /** Valor depois da alteração. */
    @Column(name = "value_after", columnDefinition = "TEXT")
    private String valueAfter;
}
