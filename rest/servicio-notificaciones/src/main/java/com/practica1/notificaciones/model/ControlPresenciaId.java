package com.practica1.notificaciones.model;

import java.io.Serializable;
import java.util.Objects;

public class ControlPresenciaId implements Serializable {
    private String nif;
    private String codigosala;
    @Override public boolean equals(Object o) {
        if (!(o instanceof ControlPresenciaId t)) return false;
        return Objects.equals(nif, t.nif) && Objects.equals(codigosala, t.codigosala);
    }
    @Override public int hashCode() { return Objects.hash(nif, codigosala); }
}
