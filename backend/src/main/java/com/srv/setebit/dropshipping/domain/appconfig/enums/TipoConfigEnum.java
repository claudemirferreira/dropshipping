package com.srv.setebit.dropshipping.domain.appconfig.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.srv.setebit.dropshipping.domain.appconfig.exception.InvalidAppConfigTipoException;

import java.util.Arrays;

/**
 * Tipos de registro na tabela {@code config} ({@code tipo} + {@code payload} JSON).
 * Persistência e API: {@link #getCodigo()}; exibição humana: {@link #getDescricao()}.
 */
public enum TipoConfigEnum {

    MERCADO_LIVRE("mercado_livre", "Mercado Livre"),
    SHOPEE("shopee", "Shopee"),
    CUSTOM("custom", "Configuração personalizada");

    private final String codigo;
    private final String descricao;

    TipoConfigEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @JsonValue
    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoConfigEnum fromCodigo(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidAppConfigTipoException("tipo não pode ser vazio");
        }
        String trimmed = value.trim();
        String lower = trimmed.toLowerCase();
        return Arrays.stream(values())
                .filter(t -> t.codigo.equals(lower) || t.name().equalsIgnoreCase(trimmed))
                .findFirst()
                .orElseThrow(() -> new InvalidAppConfigTipoException("tipo desconhecido: " + trimmed));
    }

    public static boolean isCodigoConhecido(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String trimmed = value.trim();
        String lower = trimmed.toLowerCase();
        return Arrays.stream(values()).anyMatch(t -> t.codigo.equals(lower) || t.name().equalsIgnoreCase(trimmed));
    }
}
