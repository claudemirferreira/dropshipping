package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class CommercialEmbeddable {

    @Column(name = "cost_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorCusto;

    @Column(name = "seller_fee_percent", precision = 5, scale = 2)
    private BigDecimal taxaSellerPercent;

    @Column(name = "warranty", length = 500)
    private String garantia;

    public BigDecimal getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(BigDecimal valorCusto) {
        this.valorCusto = valorCusto;
    }

    public BigDecimal getTaxaSellerPercent() {
        return taxaSellerPercent;
    }

    public void setTaxaSellerPercent(BigDecimal taxaSellerPercent) {
        this.taxaSellerPercent = taxaSellerPercent;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }
}
