package com.practica1.notificaciones.model;

import jakarta.persistence.*;

@Entity @Table(name = "salas")
public class Sala {
    @Id @Column(length = 10) private String codigosala;
    @Column(length = 100) private String nombre;
    private Integer nivel;

    public String  getCodigosala() { return codigosala; }
    public String  getNombre()     { return nombre; }
    public Integer getNivel()      { return nivel; }
}
