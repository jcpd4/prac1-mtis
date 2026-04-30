package com.practica1.salas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @Column(length = 10)
    private String codigosala;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Integer nivel;

    public String  getCodigosala()           { return codigosala; }
    public void    setCodigosala(String v)   { this.codigosala = v; }
    public String  getNombre()               { return nombre; }
    public void    setNombre(String v)       { this.nombre = v; }
    public Integer getNivel()                { return nivel; }
    public void    setNivel(Integer v)       { this.nivel = v; }
}
