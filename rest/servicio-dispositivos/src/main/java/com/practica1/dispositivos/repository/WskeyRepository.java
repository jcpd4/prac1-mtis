package com.practica1.dispositivos.repository;

import com.practica1.dispositivos.model.Wskey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WskeyRepository extends JpaRepository<Wskey, Integer> {
    boolean existsByClave(String clave);
}
