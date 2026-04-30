package com.practica1.notificaciones.model;

import jakarta.persistence.*;

@Entity @Table(name = "empleados")
public class Empleado {
    @Id @Column(length = 9) private String nif;
    @Column(length = 50)  private String nombre;
    @Column(length = 100) private String apellidos;
    @Column(length = 100) private String email;
    @Column(length = 12)  private String naf;
    @Column(length = 24)  private String iban;
    @Column(length = 3)   private String tipodocumento;

    public String getNif()       { return nif; }
    public String getNombre()    { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getEmail()     { return email; }
}
