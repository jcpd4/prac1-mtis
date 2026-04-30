package com.practica1.cliente;

import com.practica1.cliente.soap.validaciones.*;
import com.practica1.cliente.soap.empleados.*;
import com.practica1.cliente.soap.controlaccesos.*;
import com.practica1.cliente.soap.controlpresencia.*;
import jakarta.xml.ws.Holder;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

/**
 * Cliente de prueba para todos los servicios SOAP y REST de la Práctica 1.
 * Todos los servicios deben estar arrancados antes de ejecutar:
 *   Validaciones    http://localhost:8081/ws/validaciones
 *   Empleados       http://localhost:8082/ws/empleados
 *   ControlAccesos  http://localhost:8083/ws/controlaccesos
 *   ControlPresencia http://localhost:8084/ws/controlpresencia
 *   Salas REST      http://localhost:8085/api/salas
 *   Niveles REST    http://localhost:8086/api/niveles
 *   Dispositivos REST http://localhost:8087/api/dispositivos
 *   Notificaciones  http://localhost:8088/api/notificaciones
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
    // SOAP: Validaciones
    // ================================================================

    private static void probarValidaciones() {
        System.out.println("--- Servicio SOAP: Validaciones ---");
        ValidacionesPortType svc = crearClienteSoap(ValidacionesPortType.class,
                "http://localhost:8081/ws/validaciones");

        Holder<Boolean> resultado = new Holder<>();
        Holder<String> mensaje = new Holder<>();

        svc.validarNIF("33900165M", WS_KEY, resultado, mensaje);
        System.out.println("validarNIF(33900165M) -> " + resultado.value + " | " + mensaje.value);

        svc.validarNIE("Z9817136M", WS_KEY, resultado, mensaje);
        System.out.println("validarNIE(Z9817136M) -> " + resultado.value + " | " + mensaje.value);

        svc.validarNAF("527575965274", WS_KEY, resultado, mensaje);
        System.out.println("validarNAF(527575965274) -> " + resultado.value + " | " + mensaje.value);

        svc.validarIBAN("ES0690000001210123456789", WS_KEY, resultado, mensaje);
        System.out.println("validarIBAN(ES0690000001210123456789) -> " + resultado.value + " | " + mensaje.value);

        // WSKey inválida
        svc.validarNIF("33900165M", "WSKEY_INVALIDA", resultado, mensaje);
        System.out.println("validarNIF(wskey inválida) -> " + resultado.value + " | " + mensaje.value);

        System.out.println();
    }

    // ================================================================
    // SOAP: Empleados
    // ================================================================

    private static void probarEmpleados() {
        System.out.println("--- Servicio SOAP: Empleados ---");
        EmpleadosPortType svc = crearClienteSoap(EmpleadosPortType.class,
                "http://localhost:8082/ws/empleados");

        Holder<Boolean> resultado = new Holder<>();
        Holder<String> mensaje = new Holder<>();
        Holder<com.practica1.cliente.soap.empleados.EmpleadoType> empleadoHolder = new Holder<>();

        // Consultar empleado existente
        svc.consultar("33900165M", WS_KEY, empleadoHolder, mensaje);
        System.out.println("consultar(33900165M) -> " + mensaje.value);
        if (empleadoHolder.value != null)
            System.out.println("  Nombre: " + empleadoHolder.value.getNombre() + " " + empleadoHolder.value.getApellidos());

        // Nuevo empleado de prueba
        com.practica1.cliente.soap.empleados.EmpleadoType emp = new com.practica1.cliente.soap.empleados.EmpleadoType();
        emp.setNif("12345678Z");
        emp.setNombre("María");
        emp.setApellidos("Pérez Ruiz");
        emp.setEmail("maria.test@empresa.com");
        emp.setNaf("280012345601");
        emp.setIban("ES9121000418450200051332");
        emp.setTipoDocumento("NIF");

        svc.nuevo(emp, WS_KEY, resultado, mensaje);
        System.out.println("nuevo(12345678Z) -> " + resultado.value + " | " + mensaje.value);

        // Modificar empleado
        emp.setEmail("maria.modificada@empresa.com");
        svc.modificar(emp, WS_KEY, resultado, mensaje);
        System.out.println("modificar(12345678Z) -> " + resultado.value + " | " + mensaje.value);

        // Borrar empleado de prueba
        svc.borrar("12345678Z", WS_KEY, resultado, mensaje);
        System.out.println("borrar(12345678Z) -> " + resultado.value + " | " + mensaje.value);

        System.out.println();
    }

    // ================================================================
    // SOAP: ControlAccesos
    // ================================================================

    private static void probarControlAccesos() throws Exception {
        System.out.println("--- Servicio SOAP: ControlAccesos ---");
        ControlAccesosPortType svc = crearClienteSoap(ControlAccesosPortType.class,
                "http://localhost:8083/ws/controlaccesos");

        Holder<Boolean> resultado = new Holder<>();
        Holder<String> mensaje = new Holder<>();

        svc.registrar("33900165M", "S001", "D001", WS_KEY, resultado, mensaje);
        System.out.println("registrar acceso -> " + resultado.value + " | " + mensaje.value);

        // Consultar con rango de fechas
        Holder<com.practica1.cliente.soap.controlaccesos.ListaRegistrosType> registros = new Holder<>();
        XMLGregorianCalendar desde = toXmlCalendar(LocalDateTime.now().minusDays(1));
        XMLGregorianCalendar hasta = toXmlCalendar(LocalDateTime.now().plusDays(1));
        svc.consultar("33900165M", null, null, desde, hasta, WS_KEY, registros, mensaje);
        System.out.println("consultar accesos -> " + mensaje.value);
        if (registros.value != null)
            System.out.println("  Registros: " + registros.value.getRegistro().size());

        System.out.println();
    }

    // ================================================================
    // SOAP: ControlPresencia
    // ================================================================

    private static void probarControlPresencia() {
        System.out.println("--- Servicio SOAP: ControlPresencia ---");
        ControlPresenciaPortType svc = crearClienteSoap(ControlPresenciaPortType.class,
                "http://localhost:8084/ws/controlpresencia");

        Holder<Boolean> resultado = new Holder<>();
        Holder<String> mensaje = new Holder<>();

        svc.registrar("33900165M", "S001", WS_KEY, resultado, mensaje);
        System.out.println("registrar presencia -> " + resultado.value + " | " + mensaje.value);

        Holder<com.practica1.cliente.soap.controlpresencia.ListaEmpleadosType> empleados = new Holder<>();
        svc.controlEmpleadosSala("S001", WS_KEY, empleados, mensaje);
        System.out.println("controlEmpleadosSala(S001) -> " + mensaje.value);
        if (empleados.value != null)
            System.out.println("  Empleados en sala: " + empleados.value.getEmpleado().size());

        svc.eliminar("33900165M", "S001", WS_KEY, resultado, mensaje);
        System.out.println("eliminar presencia -> " + resultado.value + " | " + mensaje.value);

        System.out.println();
    }

    // ================================================================
    // REST: Salas
    // ================================================================

    private static void probarSalasRest() throws Exception {
        System.out.println("--- Servicio REST: Salas ---");
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> resp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/salas/S001"))
                .header("wskey", WS_KEY).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("GET /salas/S001 -> " + resp.statusCode() + " | " + resp.body());

        String body = "{\"codigosala\":\"S099\",\"nombre\":\"Sala de prueba\",\"nivel\":1}";
        HttpResponse<String> postResp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/salas"))
                .header("wskey", WS_KEY).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /salas -> " + postResp.statusCode() + " | " + postResp.body());

        HttpResponse<String> delResp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/salas/S099"))
                .header("wskey", WS_KEY).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("DELETE /salas/S099 -> " + delResp.statusCode() + " | " + delResp.body());

        System.out.println();
    }

    // ================================================================
    // REST: Niveles
    // ================================================================

    private static void probarNivelesRest() throws Exception {
        System.out.println("--- Servicio REST: Niveles ---");
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> resp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8086/api/niveles/1"))
                .header("wskey", WS_KEY).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("GET /niveles/1 -> " + resp.statusCode() + " | " + resp.body());

        System.out.println();
    }

    // ================================================================
    // REST: Dispositivos
    // ================================================================

    private static void probarDispositivosRest() throws Exception {
        System.out.println("--- Servicio REST: Dispositivos ---");
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> resp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8087/api/dispositivos/D001"))
                .header("wskey", WS_KEY).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("GET /dispositivos/D001 -> " + resp.statusCode() + " | " + resp.body());

        System.out.println();
    }

    // ================================================================
    // REST: Notificaciones
    // ================================================================

    private static void probarNotificacionesRest() throws Exception {
        System.out.println("--- Servicio REST: Notificaciones ---");
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> resp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/notificaciones/usuario-valido/33900165M"))
                .header("wskey", WS_KEY)
                .POST(HttpRequest.BodyPublishers.noBody()).build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /notificaciones/usuario-valido/33900165M -> " + resp.statusCode() + " | " + resp.body());

        String errBody = "{\"nif\":\"33900165M\",\"error\":\"Error de prueba desde el cliente\"}";
        HttpResponse<String> errResp = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/notificaciones/error"))
                .header("wskey", WS_KEY).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(errBody)).build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println("POST /notificaciones/error -> " + errResp.statusCode() + " | " + errResp.body());

        System.out.println();
    }

    // ================================================================
    // Helpers
    // ================================================================

    @SuppressWarnings("unchecked")
    private static <T> T crearClienteSoap(Class<T> serviceClass, String url) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceClass);
        factory.setAddress(url);
        return (T) factory.create();
    }

    private static XMLGregorianCalendar toXmlCalendar(LocalDateTime ldt) throws Exception {
        GregorianCalendar gc = GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }
}
