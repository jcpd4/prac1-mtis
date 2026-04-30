package com.practica1.notificaciones.repository;

import com.practica1.notificaciones.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, String> {
}
