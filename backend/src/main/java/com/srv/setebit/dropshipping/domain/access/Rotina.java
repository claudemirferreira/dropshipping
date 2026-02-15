package com.srv.setebit.dropshipping.domain.access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rotina {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String path;
    private boolean active;
    private int displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}
