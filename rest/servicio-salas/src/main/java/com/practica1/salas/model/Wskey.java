package com.practica1.salas.model;

import jakarta.persistence.*;

@Entity @Table(name = "wskeys")
public class Wskey {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(nullable = false, unique = true) private String clave;
    public String getClave() { return clave; }
}
