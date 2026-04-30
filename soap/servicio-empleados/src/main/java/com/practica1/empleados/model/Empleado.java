package com.practica1.empleados.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @Column(length = 9)
    private String nif;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 12)
    private String naf;

    @Column(nullable = false, length = 24)
    private String iban;

    @Column(nullable = false, length = 3)
    private String tipodocumento;

    public String getNif()            { return nif; }
    public void   setNif(String v)    { this.nif = v; }
    public String getNombre()         { return nombre; }
    public void   setNombre(String v) { this.nombre = v; }
    public String getApellidos()      { return apellidos; }
    public void   setApellidos(String v) { this.apellidos = v; }
    public String getEmail()          { return email; }
    public void   setEmail(String v)  { this.email = v; }
    public String getNaf()            { return naf; }
    public void   setNaf(String v)    { this.naf = v; }
    public String getIban()           { return iban; }
    public void   setIban(String v)   { this.iban = v; }
    public String getTipodocumento()  { return tipodocumento; }
    public void   setTipodocumento(String v) { this.tipodocumento = v; }
}
