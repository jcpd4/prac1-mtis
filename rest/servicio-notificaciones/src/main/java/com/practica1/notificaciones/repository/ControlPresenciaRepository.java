package com.practica1.notificaciones.repository;

import com.practica1.notificaciones.model.ControlPresencia;
import com.practica1.notificaciones.model.ControlPresenciaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ControlPresenciaRepository extends JpaRepository<ControlPresencia, ControlPresenciaId> {
    List<ControlPresencia> findAll();
}
