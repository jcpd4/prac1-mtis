package com.practica1.controlpresencia.repository;

import com.practica1.controlpresencia.model.ControlPresencia;
import com.practica1.controlpresencia.model.ControlPresenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ControlPresenciaRepository extends JpaRepository<ControlPresencia, ControlPresenciaId> {

    @Query("SELECT cp FROM ControlPresencia cp WHERE cp.codigosala = :sala")
    List<ControlPresencia> findByCodigosala(@Param("sala") String sala);
}
