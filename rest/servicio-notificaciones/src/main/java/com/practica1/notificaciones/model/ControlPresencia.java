package com.practica1.notificaciones.model;

import jakarta.persistence.*;

@Entity @Table(name = "controlpresencia")
@IdClass(ControlPresenciaId.class)
public class ControlPresencia {
    @Id @Column(length = 9)  private String nif;
    @Id @Column(length = 10) private String codigosala;

    public String getNif()        { return nif; }
    public String getCodigosala() { return codigosala; }
}
