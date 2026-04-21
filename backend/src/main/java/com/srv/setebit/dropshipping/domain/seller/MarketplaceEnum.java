package com.srv.setebit.dropshipping.domain.seller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Marketplaces suportados para integração de seller.
 * API/persistência: {@link #getCodigo()}; exibição: {@link #getDescricao()}.
 */
public enum MarketplaceEnum {

    MERCADO_LIVRE("mercado_livre", "Mercado Livre"),
    SHOPEE("shopee", "Shopee");

    private final String codigo;
    private final String descricao;

    MarketplaceEnum(String codigo, String descricao) {
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
    public static MarketplaceEnum fromCodigo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("marketplace não pode ser vazio");
        }
        String trimmed = value.trim();
        String lower = trimmed.toLowerCase();
        return Arrays.stream(values())
                .filter(m -> m.codigo.equals(lower) || m.name().equalsIgnoreCase(trimmed))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("marketplace desconhecido: " + trimmed));
    }

    public static boolean isCodigoConhecido(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String trimmed = value.trim();
        String lower = trimmed.toLowerCase();
        return Arrays.stream(values()).anyMatch(m -> m.codigo.equals(lower) || m.name().equalsIgnoreCase(trimmed));
    }
}
