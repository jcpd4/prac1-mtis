package com.practica1.cliente;

import com.practica1.cliente.soap.validaciones.*;
import com.practica1.cliente.soap.empleados.*;
import com.practica1.cliente.soap.controlaccesos.*;
import com.practica1.cliente.soap.controlpresencia.*;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Aplicación cliente que prueba todos los servicios SOAP y REST de la Práctica 1.
 *
 * Requisito: todos los servicios deben estar arrancados antes de ejecutar.
 *   - Validaciones:    http://localhost:8081/ws/validaciones
 *   - Empleados:       http://localhost:8082/ws/empleados
 *   - ControlAccesos:  http://localhost:8083/ws/controlaccesos
 *   - ControlPresencia:http://localhost:8084/ws/controlpresencia
 *   - Salas REST:      http://localhost:8085/api/salas
 *   - Niveles REST:    http://localhost:8086/api/niveles
 *   - Dispositivos REST:http://localhost:8087/api/dispositivos
 *   - Notificaciones:  http://localhost:8088/api/notificaciones
 */
public class ClienteMain {

    private static final String WS_KEY = "PRACTICA1_KEY_2024";

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  CLIENTE PRÁCTICA 1 - MTIS");
        System.out.println("========================================\n");

        probarValidaciones();
        probarEmpleados();
        probarControlAccesos();
        probarControlPresencia();
        probarSalasRest();
        probarNivelesRest();
        probarDispositivosRest();
        probarNotificacionesRest();

        System.out.println("\n========================================");
        System.out.println("  FIN DE PRUEBAS");
        System.out.println("========================================");
    }

    // ================================================================
    // SERVICIOS SOAP
    // ================================================================

    private static void probarValidaciones() {
        System.out.println("--- Servicio SOAP: Validaciones ---");
        ValidacionesPortType svc = crearClienteSoap(ValidacionesPortType.class,
                "http://localhost:8081/ws/validaciones");

        // NIF válido
        ValidarNIF reqNif = new ValidarNIF();
        reqNif.setNif("33900165M");
        reqNif.setWskey(WS_KEY);
        ValidarNIFResponse rNif = svc.validarNIF(reqNif);
        System.out.println("validarNIF(33900165M) -> " + rNif.isMensaje() + " | " + rNif.getMensaje());

        // NIE válido
        ValidarNIE reqNie = new ValidarNIE();
        reqNie.setNie("Z9817136M");
        reqNie.setWskey(WS_KEY);
        ValidarNIEResponse rNie = svc.validarNIE(reqNie);
        System.out.println("validarNIE(Z9817136M) -> " + rNie.isMensaje() + " | " + rNie.getMensaje());

        // NAF válido
        ValidarNAF reqNaf = new ValidarNAF();
        reqNaf.setNaf("527575965274");
        reqNaf.setWskey(WS_KEY);
        ValidarNAFResponse rNaf = svc.validarNAF(reqNaf);
        System.out.println("validarNAF(527575965274) -> " + rNaf.isMensaje() + " | " + rNaf.getMensaje());

        // IBAN válido
        ValidarIBAN reqIban = new ValidarIBAN();
        reqIban.setIban("ES0690000001210123456789");
        reqIban.setWskey(WS_KEY);
        ValidarIBANResponse rIban = svc.validarIBAN(reqIban);
        System.out.println("validarIBAN(ES0690000001210123456789) -> " + rIban.isMensaje() + " | " + rIban.getMensaje());
        System.out.println();
    }

    private static void probarEmpleados() {
        System.out.println("--- Servicio SOAP: Empleados ---");
        EmpleadosPortType svc = crearClienteSoap(EmpleadosPortType.class,
                "http://localhost:8082/ws/empleados");

        // Consultar empleado existente
        Consultar reqCons = new Consultar();
        reqCons.setNif("33900165M");
        reqCons.setWskey(WS_KEY);
        ConsultarResponse rCons = svc.consultar(reqCons);
        System.out.println("consultar(33900165M) -> " + rCons.getMensaje());
        if (rCons.getEmpleado() != null)
            System.out.println("  Nombre: " + rCons.getEmpleado().getNombre() + " " + rCons.getEmpleado().getApellidos());

        // Nuevo empleado de prueba
        EmpleadoType emp = new EmpleadoType();
        emp.setNif("12345678Z");
        emp.setNombre("María");
        emp.setApellidos("Pérez Ruiz");
        emp.setEmail("maria.test@empresa.com");
        emp.setNaf("280012345601");
        emp.setIban("ES9121000418450200051332");
        emp.setTipoDocumento("NIF");

        Nuevo reqNuevo = new Nuevo();
        reqNuevo.setEmpleado(emp);
        reqNuevo.setWskey(WS_KEY);
        NuevoResponse rNuevo = svc.nuevo(reqNuevo);
        System.out.println("nuevo(12345678Z) -> " + rNuevo.getMensaje());
        System.out.println();
    }

    private static void probarControlAccesos() {
        System.out.println("--- Servicio SOAP: ControlAccesos ---");
        ControlAccesosPortType svc = crearClienteSoap(ControlAccesosPortType.class,
                "http://localhost:8083/ws/controlaccesos");

        Registrar req = new Registrar();
        req.setNif("33900165M");
        req.setCodigosala("S001");
        req.setCodigodispositivo("D001");
        req.setWskey(WS_KEY);
        RegistrarResponse r = svc.registrar(req);
        System.out.println("registrar acceso -> " + r.getMensaje());
        System.out.println();
    }

    private static void probarControlPresencia() {
        System.out.println("--- Servicio SOAP: ControlPresencia ---");
        ControlPresenciaPortType svc = crearClienteSoap(ControlPresenciaPortType.class,
                "http://localhost:8084/ws/controlpresencia");

        Registrar reqReg = new Registrar();
        reqReg.setNif("33900165M");
        reqReg.setCodigosala("S001");
        reqReg.setWskey(WS_KEY);
        RegistrarResponse rReg = svc.registrar(reqReg);
        System.out.println("registrar presencia -> " + rReg.getMensaje());

        ControlEmpleadosSala reqSala = new ControlEmpleadosSala();
        reqSala.setCodigosala("S001");
        reqSala.setWskey(WS_KEY);
        ControlEmpleadosSalaResponse rSala = svc.controlEmpleadosSala(reqSala);
        System.out.println("controlEmpleadosSala(S001) -> " + rSala.getMensaje());
        System.out.println();
    }

    // ================================================================
    // SERVICIOS REST
    // ================================================================

    private static void probarSalasRest() throws Exception {
        System.out.println("--- Servicio REST: Salas ---");
        HttpClient client = HttpClient.newHttpClient();

        // GET sala existente
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/salas/S001"))
                .header("wskey", WS_KEY)
                .GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET /salas/S001 -> " + resp.statusCode() + " | " + resp.body());

        // POST nueva sala
        String body = """
                {"codigosala":"S099","nombre":"Sala de prueba","nivel":1}
                """;
        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/salas"))
                .header("wskey", WS_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> postResp = client.send(postReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /salas -> " + postResp.statusCode() + " | " + postResp.body());

        // DELETE sala de prueba
        HttpRequest delReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/salas/S099"))
                .header("wskey", WS_KEY)
                .DELETE().build();
        HttpResponse<String> delResp = client.send(delReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("DELETE /salas/S099 -> " + delResp.statusCode() + " | " + delResp.body());
        System.out.println();
    }

    private static void probarNivelesRest() throws Exception {
        System.out.println("--- Servicio REST: Niveles ---");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8086/api/niveles/1"))
                .header("wskey", WS_KEY)
                .GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET /niveles/1 -> " + resp.statusCode() + " | " + resp.body());
        System.out.println();
    }

    private static void probarDispositivosRest() throws Exception {
        System.out.println("--- Servicio REST: Dispositivos ---");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8087/api/dispositivos/D001"))
                .header("wskey", WS_KEY)
                .GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET /dispositivos/D001 -> " + resp.statusCode() + " | " + resp.body());
        System.out.println();
    }

    private static void probarNotificacionesRest() throws Exception {
        System.out.println("--- Servicio REST: Notificaciones ---");
        HttpClient client = HttpClient.newHttpClient();

        // NotificarUsuarioValido
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/notificaciones/usuario-valido/33900165M"))
                .header("wskey", WS_KEY)
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /notificaciones/usuario-valido/33900165M -> " + resp.statusCode() + " | " + resp.body());

        // NotificarError
        String body = """
                {"nif":"33900165M","error":"Error de prueba desde el cliente"}
                """;
        HttpRequest errReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/notificaciones/error"))
                .header("wskey", WS_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> errResp = client.send(errReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /notificaciones/error -> " + errResp.statusCode() + " | " + errResp.body());
        System.out.println();
    }

    // ================================================================
    // Helper: crea un proxy CXF para un servicio SOAP
    // ================================================================
    @SuppressWarnings("unchecked")
    private static <T> T crearClienteSoap(Class<T> serviceClass, String url) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceClass);
        factory.setAddress(url);
        return (T) factory.create();
    }
}
