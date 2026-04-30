package com.practica1.salas.controller;

import com.practica1.salas.model.Sala;
import com.practica1.salas.repository.SalaRepository;
import com.practica1.salas.repository.WskeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/salas")
public class SalasController {

    @Autowired private SalaRepository   salaRepo;
    @Autowired private WskeyRepository  wskeyRepo;

    // ----------------------------------------------------------------
    // POST /api/salas  →  crear sala
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Map<String, Object>> nuevaSala(
            @RequestHeader("wskey") String wskey,
            @RequestBody Sala sala) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida. Acceso denegado.");

        if (sala.getCodigosala() == null || sala.getCodigosala().isBlank())
            return error(HttpStatus.BAD_REQUEST, "El campo codigosala es obligatorio.");

        if (salaRepo.existsById(sala.getCodigosala()))
            return error(HttpStatus.CONFLICT, "Ya existe una sala con código " + sala.getCodigosala());

        try {
            salaRepo.save(sala);
            return ok("Sala " + sala.getCodigosala() + " creada correctamente.");
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, "Error al crear sala: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // GET /api/salas/{codigosala}  →  consultar sala
    // ----------------------------------------------------------------
    @GetMapping("/{codigosala}")
    public ResponseEntity<?> consultarSala(
            @RequestHeader("wskey") String wskey,
            @PathVariable String codigosala) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida. Acceso denegado.");

        Optional<Sala> opt = salaRepo.findById(codigosala);
        if (opt.isEmpty())
            return error(HttpStatus.NOT_FOUND, "No existe ninguna sala con código " + codigosala);

        return ResponseEntity.ok(opt.get());
    }

    // ----------------------------------------------------------------
    // PUT /api/salas/{codigosala}  →  modificar sala
    // ----------------------------------------------------------------
    @PutMapping("/{codigosala}")
    public ResponseEntity<Map<String, Object>> modificarSala(
            @RequestHeader("wskey") String wskey,
            @PathVariable String codigosala,
            @RequestBody Sala sala) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida. Acceso denegado.");

        if (!salaRepo.existsById(codigosala))
            return error(HttpStatus.NOT_FOUND, "No existe ninguna sala con código " + codigosala);

        try {
            sala.setCodigosala(codigosala);
            salaRepo.save(sala);
            return ok("Sala " + codigosala + " actualizada correctamente.");
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, "Error al actualizar sala: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // DELETE /api/salas/{codigosala}  →  borrar sala
    // ----------------------------------------------------------------
    @DeleteMapping("/{codigosala}")
    public ResponseEntity<Map<String, Object>> borrarSala(
            @RequestHeader("wskey") String wskey,
            @PathVariable String codigosala) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida. Acceso denegado.");

        if (!salaRepo.existsById(codigosala))
            return error(HttpStatus.NOT_FOUND, "No existe ninguna sala con código " + codigosala);

        try {
            salaRepo.deleteById(codigosala);
            return ok("Sala " + codigosala + " eliminada correctamente.");
        } catch (Exception ex) {
            return error(HttpStatus.BAD_REQUEST, "No se puede eliminar la sala (puede tener registros dependientes): " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private ResponseEntity<Map<String, Object>> ok(String mensaje) {
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("codigo", status.value(), "mensaje", mensaje));
    }
}
