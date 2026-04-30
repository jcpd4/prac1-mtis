package com.practica1.controlpresencia.repository;

import com.practica1.controlpresencia.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {
}
