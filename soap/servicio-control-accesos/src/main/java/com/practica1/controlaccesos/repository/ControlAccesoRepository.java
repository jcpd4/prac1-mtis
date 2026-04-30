package com.practica1.controlaccesos.repository;

import com.practica1.controlaccesos.model.ControlAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ControlAccesoRepository extends JpaRepository<ControlAcceso, Integer> {

    @Query("SELECT c FROM ControlAcceso c WHERE " +
           "(:nif IS NULL OR c.nif = :nif) AND " +
           "(:sala IS NULL OR c.codigosala = :sala) AND " +
           "(:disp IS NULL OR c.codigodispositivo = :disp) AND " +
           "c.fechahora BETWEEN :desde AND :hasta")
    List<ControlAcceso> buscarConFiltros(
            @Param("nif")   String nif,
            @Param("sala")  String sala,
            @Param("disp")  String disp,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
