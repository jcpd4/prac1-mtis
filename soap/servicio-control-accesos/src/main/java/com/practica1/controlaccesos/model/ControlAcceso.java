package com.practica1.controlaccesos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "controlaccesos")
public class ControlAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 9)
    private String nif;

    @Column(nullable = false, length = 10)
    private String codigosala;

    @Column(nullable = false, length = 10)
    private String codigodispositivo;

    @Column(nullable = false)
    private LocalDateTime fechahora;

    public Integer getId()                    { return id; }
    public String  getNif()                   { return nif; }
    public void    setNif(String v)           { this.nif = v; }
    public String  getCodigosala()            { return codigosala; }
    public void    setCodigosala(String v)    { this.codigosala = v; }
    public String  getCodigodispositivo()     { return codigodispositivo; }
    public void    setCodigodispositivo(String v) { this.codigodispositivo = v; }
    public LocalDateTime getFechahora()       { return fechahora; }
    public void    setFechahora(LocalDateTime v) { this.fechahora = v; }
}
