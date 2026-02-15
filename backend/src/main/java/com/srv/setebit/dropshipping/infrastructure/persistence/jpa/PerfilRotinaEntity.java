package com.srv.setebit.dropshipping.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "perfil_rotina")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRotinaEntity {

    @EmbeddedId
    private PerfilRotinaId id;

    @MapsId("perfilId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private PerfilEntity perfil;

    @MapsId("rotinaId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rotina_id", nullable = false)
    private RotinaEntity rotina;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PerfilRotinaId implements Serializable {
        private UUID perfilId;
        private UUID rotinaId;
    }
}
