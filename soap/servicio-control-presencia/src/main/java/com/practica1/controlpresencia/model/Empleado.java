package com.practica1.controlpresencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id @Column(length = 9)
    private String nif;
    @Column(nullable = false, length = 50)  private String nombre;
    @Column(nullable = false, length = 100) private String apellidos;
    @Column(nullable = false, length = 100) private String email;
    @Column(nullable = false, length = 12)  private String naf;
    @Column(nullable = false, length = 24)  private String iban;
    @Column(nullable = false, length = 3)   private String tipodocumento;

    public String getNif()           { return nif; }
    public String getNombre()        { return nombre; }
    public String getApellidos()     { return apellidos; }
    public String getEmail()         { return email; }
    public String getNaf()           { return naf; }
    public String getIban()          { return iban; }
    public String getTipodocumento() { return tipodocumento; }
}
