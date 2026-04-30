package com.practica1.controlpresencia.repository;

import com.practica1.controlpresencia.model.Wskey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WskeyRepository extends JpaRepository<Wskey, Integer> {
    boolean existsByClave(String clave);
}
