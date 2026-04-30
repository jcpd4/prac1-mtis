package com.practica1.controlpresencia.model;

import java.io.Serializable;
import java.util.Objects;

public class ControlPresenciaId implements Serializable {
    private String nif;
    private String codigosala;

    public ControlPresenciaId() {}
    public ControlPresenciaId(String nif, String codigosala) {
        this.nif = nif;
        this.codigosala = codigosala;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlPresenciaId that)) return false;
        return Objects.equals(nif, that.nif) && Objects.equals(codigosala, that.codigosala);
    }
    @Override public int hashCode() { return Objects.hash(nif, codigosala); }
}
