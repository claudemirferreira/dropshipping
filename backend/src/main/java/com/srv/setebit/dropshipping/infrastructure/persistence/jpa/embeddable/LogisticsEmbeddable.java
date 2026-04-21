package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class LogisticsEmbeddable {
    @Column(name = "weight", precision = 10, scale = 4)
    private BigDecimal pesoKg;

    @Column(name = "height", precision = 10, scale = 4)
    private BigDecimal alturaCm;

    @Column(name = "width", precision = 10, scale = 4)
    private BigDecimal larguraCm;

    @Column(name = "length", precision = 10, scale = 4)
    private BigDecimal comprimentoCm;

    @Column(name = "lead_time_days")
    private Integer leadTimeEnvioDias;

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getAlturaCm() {
        return alturaCm;
    }

    public void setAlturaCm(BigDecimal alturaCm) {
        this.alturaCm = alturaCm;
    }

    public BigDecimal getLarguraCm() {
        return larguraCm;
    }

    public void setLarguraCm(BigDecimal larguraCm) {
        this.larguraCm = larguraCm;
    }

    public BigDecimal getComprimentoCm() {
        return comprimentoCm;
    }

    public void setComprimentoCm(BigDecimal comprimentoCm) {
        this.comprimentoCm = comprimentoCm;
    }

    public Integer getLeadTimeEnvioDias() {
        return leadTimeEnvioDias;
    }

    public void setLeadTimeEnvioDias(Integer leadTimeEnvioDias) {
        this.leadTimeEnvioDias = leadTimeEnvioDias;
    }
}

