package com.practica1.dispositivos.repository;

import com.practica1.dispositivos.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispositivoRepository extends JpaRepository<Dispositivo, String> {
}
