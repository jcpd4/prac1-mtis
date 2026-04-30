package com.practica1.controlpresencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "controlpresencia")
@IdClass(ControlPresenciaId.class)
public class ControlPresencia {

    @Id
    @Column(length = 9)
    private String nif;

    @Id
    @Column(length = 10)
    private String codigosala;

    public String getNif()               { return nif; }
    public void   setNif(String v)       { this.nif = v; }
    public String getCodigosala()        { return codigosala; }
    public void   setCodigosala(String v){ this.codigosala = v; }
}
