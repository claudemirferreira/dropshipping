package com.srv.setebit.dropshipping.domain.access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

    private UUID id;
    private String code;
    private String name;
    private String icon;
    private boolean active;
    private int displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<Rotina> rotinas;
}
