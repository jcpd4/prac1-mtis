package com.practica1.salas.repository;

import com.practica1.salas.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, String> {
}
