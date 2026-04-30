package com.practica1.notificaciones.controller;

import com.practica1.notificaciones.model.*;
import com.practica1.notificaciones.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    @Autowired private EmpleadoRepository      empRepo;
    @Autowired private ControlPresenciaRepository cpRepo;
    @Autowired private SalaRepository          salaRepo;
    @Autowired private WskeyRepository         wskeyRepo;
    @Autowired private JavaMailSender           mailSender;

    @Value("${mail.from}")
    private String mailFrom;

    // ----------------------------------------------------------------
    // POST /api/notificaciones/presencia-sala
    // ----------------------------------------------------------------
    @PostMapping("/presencia-sala")
    public ResponseEntity<Map<String, Object>> notificarPresenciaSala(
            @RequestHeader("wskey") String wskey) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        try {
            List<ControlPresencia> presencias = cpRepo.findAll();
            if (presencias.isEmpty())
                return ok("No hay empleados en ninguna sala actualmente.");

            int enviados = 0;
            for (ControlPresencia cp : presencias) {
                Optional<Empleado> empOpt  = empRepo.findById(cp.getNif());
                Optional<Sala>     salaOpt = salaRepo.findById(cp.getCodigosala());

                if (empOpt.isPresent() && salaOpt.isPresent()) {
                    Empleado emp  = empOpt.get();
                    Sala     sala = salaOpt.get();

                    SimpleMailMessage msg = new SimpleMailMessage();
                    msg.setFrom(mailFrom);
                    msg.setTo(emp.getEmail());
                    msg.setSubject("Notificación de presencia en sala");
                    msg.setText("Hola " + emp.getNombre() + " " + emp.getApellidos() + ",\n\n" +
                            "Se le notifica que actualmente tiene presencia registrada en la sala: " +
                            sala.getNombre() + " (código: " + sala.getCodigosala() + ").\n\n" +
                            "Edificio Inteligente - Sistema Automático");
                    mailSender.send(msg);
                    enviados++;
                }
            }
            return ok("Notificaciones enviadas: " + enviados + " email(s).");
        } catch (Exception ex) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar notificaciones: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // POST /api/notificaciones/usuario-valido/{nif}
    // ----------------------------------------------------------------
    @PostMapping("/usuario-valido/{nif}")
    public ResponseEntity<Map<String, Object>> notificarUsuarioValido(
            @RequestHeader("wskey") String wskey,
            @PathVariable String nif) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        Optional<Empleado> empOpt = empRepo.findById(nif);
        if (empOpt.isEmpty())
            return error(HttpStatus.NOT_FOUND, "No existe ningún empleado con NIF " + nif);

        try {
            Empleado emp = empOpt.get();
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(mailFrom);
            msg.setTo(emp.getEmail());
            msg.setSubject("Validación de empleado");
            msg.setText("Hola " + emp.getNombre() + " " + emp.getApellidos() + ",\n\n" +
                    "Le informamos de que su perfil de empleado es válido en el sistema.\n" +
                    "NIF registrado: " + emp.getNif() + "\n\n" +
                    "Edificio Inteligente - Sistema Automático");
            mailSender.send(msg);
            return ok("Notificación enviada a " + emp.getEmail());
        } catch (Exception ex) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar email: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // POST /api/notificaciones/error
    // ----------------------------------------------------------------
    @PostMapping("/error")
    public ResponseEntity<Map<String, Object>> notificarError(
            @RequestHeader("wskey") String wskey,
            @RequestBody Map<String, String> body) {

        if (!wskeyRepo.existsByClave(wskey))
            return error(HttpStatus.UNAUTHORIZED, "WSKey inválida.");

        String nif   = body.get("nif");
        String error = body.get("error");

        if (nif == null || nif.isBlank() || error == null || error.isBlank())
            return error(HttpStatus.BAD_REQUEST, "Los campos 'nif' y 'error' son obligatorios.");

        Optional<Empleado> empOpt = empRepo.findById(nif);
        if (empOpt.isEmpty())
            return error(HttpStatus.NOT_FOUND, "No existe ningún empleado con NIF " + nif);

        try {
            Empleado emp = empOpt.get();
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(mailFrom);
            msg.setTo(emp.getEmail());
            msg.setSubject("Notificación de Error - Edificio Inteligente");
            msg.setText("Hola " + emp.getNombre() + " " + emp.getApellidos() + ",\n\n" +
                    "Se ha producido el siguiente error:\n\n" + error + "\n\n" +
                    "Por favor, contacte con el administrador del sistema.\n\n" +
                    "Edificio Inteligente - Sistema Automático");
            mailSender.send(msg);
            return ok("Notificación de error enviada a " + emp.getEmail());
        } catch (Exception ex) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar email: " + ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> ok(String msg) {
        return ResponseEntity.ok(Map.of("mensaje", msg));
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("codigo", s.value(), "mensaje", msg));
    }
}
