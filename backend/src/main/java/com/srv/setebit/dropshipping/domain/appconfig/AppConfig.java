package com.srv.setebit.dropshipping.domain.appconfig;

import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {

    private UUID id;
    private TipoConfigEnum tipo;
    private Map<String, Object> payload;
    private Instant createdAt;
    private Instant updatedAt;
}
