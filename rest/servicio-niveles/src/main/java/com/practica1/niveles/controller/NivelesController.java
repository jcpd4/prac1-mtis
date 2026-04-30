package com.practica1.niveles.controller;

import com.practica1.niveles.model.Nivel;
import com.practica1.niveles.repository.NivelRepository;
import com.practica1.niveles.repository.WskeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/niveles")
public class NivelesController {

    @Autowired private NivelRepository  nivelRepo;
    @Autowired private WskeyRepository  wskeyRepo;

    @PostMapping
    public ResponseEntity<Map<String, Object>> nuevoNivel(
            @RequestHeader("wskey") String wskey,
            @RequestBody Nivel nivel) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        if (nivel.getNivel() == null)
            return error(HttpStatus.BAD_REQUEST, "El campo nivel es obligatorio.");

        if (nivelRepo.existsById(nivel.getNivel()))
            return error(HttpStatus.CONFLICT, "Ya existe el nivel " + nivel.getNivel());

        try {
            nivelRepo.save(nivel);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Nivel " + nivel.getNivel() + " creado."));
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{nivel}")
    public ResponseEntity<?> consultarNivel(
            @RequestHeader("wskey") String wskey,
            @PathVariable Integer nivel) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        Optional<Nivel> opt = nivelRepo.findById(nivel);
        if (opt.isEmpty())
            return error(HttpStatus.NOT_FOUND, "No existe el nivel " + nivel);

        return ResponseEntity.ok(opt.get());
    }

    @PutMapping("/{nivel}")
    public ResponseEntity<Map<String, Object>> modificarNivel(
            @RequestHeader("wskey") String wskey,
            @PathVariable Integer nivel,
            @RequestBody Nivel body) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        if (!nivelRepo.existsById(nivel))
            return error(HttpStatus.NOT_FOUND, "No existe el nivel " + nivel);

        body.setNivel(nivel);
        nivelRepo.save(body);
        return ok("Nivel " + nivel + " actualizado.");
    }

    @DeleteMapping("/{nivel}")
    public ResponseEntity<Map<String, Object>> borrarNivel(
            @RequestHeader("wskey") String wskey,
            @PathVariable Integer nivel) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        if (!nivelRepo.existsById(nivel))
            return error(HttpStatus.NOT_FOUND, "No existe el nivel " + nivel);

        try {
            nivelRepo.deleteById(nivel);
            return ok("Nivel " + nivel + " eliminado.");
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, "No se puede eliminar (puede tener salas asociadas): " + ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> ok(String msg) {
        return ResponseEntity.ok(Map.of("mensaje", msg));
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("codigo", s.value(), "mensaje", msg));
    }
}
