package com.practica1.notificaciones.repository;

import com.practica1.notificaciones.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {
}
