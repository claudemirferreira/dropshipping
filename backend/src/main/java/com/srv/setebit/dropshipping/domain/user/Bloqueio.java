package com.srv.setebit.dropshipping.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bloqueio {
    private UUID id;
    private UUID userId;
    private String login;
    private String motivo;
    private BloqueioStatus status;
    private Instant dataDoBloqueio;
    private Instant dataDoDesbloqueio;
    private Instant dataDoUsuarioDesbloqueou;
    private UUID desbloqueadoPor;
    private Instant createdAt;
}
