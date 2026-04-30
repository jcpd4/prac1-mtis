package com.practica1.empleados.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wskeys")
public class Wskey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String clave;

    public Integer getId()          { return id; }
    public String  getClave()       { return clave; }
    public void    setClave(String v) { this.clave = v; }
}
