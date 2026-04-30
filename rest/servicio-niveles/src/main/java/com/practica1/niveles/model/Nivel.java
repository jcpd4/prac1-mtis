package com.practica1.niveles.model;

import jakarta.persistence.*;

@Entity
@Table(name = "niveles")
public class Nivel {
    @Id
    private Integer nivel;
    @Column(nullable = false, length = 100)
    private String descripcion;

    public Integer getNivel()              { return nivel; }
    public void    setNivel(Integer v)     { this.nivel = v; }
    public String  getDescripcion()        { return descripcion; }
    public void    setDescripcion(String v){ this.descripcion = v; }
}
