package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StockEmbeddable {

    @Column(name = "stock_quantity")
    private Integer atual;

    @Column(name = "stock_minimum")
    private Integer minimo;

    public Integer getAtual() {
        return atual;
    }

    public void setAtual(Integer atual) {
        this.atual = atual;
    }

    public Integer getMinimo() {
        return minimo;
    }

    public void setMinimo(Integer minimo) {
        this.minimo = minimo;
    }
}

