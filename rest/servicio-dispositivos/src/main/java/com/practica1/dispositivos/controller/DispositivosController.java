package com.practica1.dispositivos.controller;

import com.practica1.dispositivos.model.Dispositivo;
import com.practica1.dispositivos.repository.DispositivoRepository;
import com.practica1.dispositivos.repository.WskeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivosController {

    @Autowired private DispositivoRepository dispRepo;
    @Autowired private WskeyRepository       wskeyRepo;

    @PostMapping
    public ResponseEntity<Map<String, Object>> nuevoDispositivo(
            @RequestHeader("wskey") String wskey,
            @RequestBody Dispositivo d) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        if (d.getCodigodispositivo() == null || d.getCodigodispositivo().isBlank())
            return error(HttpStatus.BAD_REQUEST, "codigodispositivo es obligatorio.");

        if (dispRepo.existsById(d.getCodigodispositivo()))
            return error(HttpStatus.CONFLICT, "Ya existe el dispositivo " + d.getCodigodispositivo());

        try {
            dispRepo.save(d);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Dispositivo " + d.getCodigodispositivo() + " creado."));
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> consultarDispositivo(
            @RequestHeader("wskey") String wskey,
            @PathVariable String codigo) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        Optional<Dispositivo> opt = dispRepo.findById(codigo);
        if (opt.isEmpty())
            return error(HttpStatus.NOT_FOUND, "No existe el dispositivo " + codigo);

        return ResponseEntity.ok(opt.get());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Map<String, Object>> modificarDispositivo(
            @RequestHeader("wskey") String wskey,
            @PathVariable String codigo,
            @RequestBody Dispositivo d) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        if (!dispRepo.existsById(codigo))
            return error(HttpStatus.NOT_FOUND, "No existe el dispositivo " + codigo);

        d.setCodigodispositivo(codigo);
        dispRepo.save(d);
        return ok("Dispositivo " + codigo + " actualizado.");
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Map<String, Object>> borrarDispositivo(
            @RequestHeader("wskey") String wskey,
            @PathVariable String codigo) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        if (!dispRepo.existsById(codigo))
            return error(HttpStatus.NOT_FOUND, "No existe el dispositivo " + codigo);

        try {
            dispRepo.deleteById(codigo);
            return ok("Dispositivo " + codigo + " eliminado.");
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, "No se puede eliminar: " + ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> ok(String msg) {
        return ResponseEntity.ok(Map.of("mensaje", msg));
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("codigo", s.value(), "mensaje", msg));
    }
}
