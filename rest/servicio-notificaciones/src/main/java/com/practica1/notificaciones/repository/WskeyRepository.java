package com.practica1.notificaciones.repository;

import com.practica1.notificaciones.model.Wskey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WskeyRepository extends JpaRepository<Wskey, Integer> {
    boolean existsByClave(String clave);
}
