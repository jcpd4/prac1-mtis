package com.practica1.dispositivos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dispositivos")
public class Dispositivo {
    @Id @Column(length = 10)
    private String codigodispositivo;
    @Column(nullable = false, length = 100) private String descripcion;
    @Column(nullable = false, length = 10)  private String codigosala;

    public String getCodigodispositivo()           { return codigodispositivo; }
    public void   setCodigodispositivo(String v)   { this.codigodispositivo = v; }
    public String getDescripcion()                 { return descripcion; }
    public void   setDescripcion(String v)         { this.descripcion = v; }
    public String getCodigosala()                  { return codigosala; }
    public void   setCodigosala(String v)          { this.codigosala = v; }
}
