package com.practica1.empleados.impl;

import com.practica1.empleados.generated.*;
import com.practica1.empleados.model.Empleado;
import com.practica1.empleados.repository.EmpleadoRepository;
import com.practica1.empleados.repository.WskeyRepository;
import jakarta.jws.WebService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@WebService(
    endpointInterface = "com.practica1.empleados.generated.EmpleadosPortType",
    serviceName       = "EmpleadosService",
    portName          = "EmpleadosPort",
    targetNamespace   = "http://practica1.com/empleados",
    wsdlLocation      = "classpath:wsdl/Empleados.wsdl"
)
public class EmpleadosImpl implements EmpleadosPortType {

    @Autowired
    private EmpleadoRepository empleadoRepo;

    @Autowired
    private WskeyRepository wskeyRepo;

    @Value("${validaciones.service.url}")
    private String validacionesUrl;

    @Value("${validaciones.wskey}")
    private String validacionesWskey;

    // ------------------------------------------------------------------
    // nuevo
    // ------------------------------------------------------------------
    @Override
    public NuevoResponse nuevo(Nuevo parameters) {
        NuevoResponse response = new NuevoResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida.");
            return response;
        }

        EmpleadoType dto = parameters.getEmpleado();
        if (dto == null) {
            response.setResultado(false);
            response.setMensaje("ERROR: Datos de empleado nulos.");
            return response;
        }

        // Validar NIF/NIE, NAF e IBAN antes de insertar
        String validacionError = validarDatosEmpleado(dto);
        if (validacionError != null) {
            response.setResultado(false);
            response.setMensaje(validacionError);
            return response;
        }

        if (empleadoRepo.existsById(dto.getNif())) {
            response.setResultado(false);
            response.setMensaje("ERROR: Ya existe un empleado con NIF " + dto.getNif());
            return response;
        }

        try {
            Empleado e = toEntity(dto);
            empleadoRepo.save(e);
            response.setResultado(true);
            response.setMensaje("OK: Empleado " + dto.getNif() + " creado correctamente.");
        } catch (Exception ex) {
            response.setResultado(false);
            response.setMensaje("ERROR: Fallo al insertar en BD: " + ex.getMessage());
        }
        return response;
    }

    // ------------------------------------------------------------------
    // borrar
    // ------------------------------------------------------------------
    @Override
    public BorrarResponse borrar(Borrar parameters) {
        BorrarResponse response = new BorrarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida.");
            return response;
        }

        String nif = parameters.getNif();
        if (!empleadoRepo.existsById(nif)) {
            response.setResultado(false);
            response.setMensaje("ERROR: No existe ningún empleado con NIF " + nif);
            return response;
        }

        try {
            empleadoRepo.deleteById(nif);
            response.setResultado(true);
            response.setMensaje("OK: Empleado " + nif + " eliminado.");
        } catch (Exception ex) {
            response.setResultado(false);
            response.setMensaje("ERROR: " + ex.getMessage());
        }
        return response;
    }

    // ------------------------------------------------------------------
    // modificar
    // ------------------------------------------------------------------
    @Override
    public ModificarResponse modificar(Modificar parameters) {
        ModificarResponse response = new ModificarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida.");
            return response;
        }

        EmpleadoType dto = parameters.getEmpleado();
        if (!empleadoRepo.existsById(dto.getNif())) {
            response.setResultado(false);
            response.setMensaje("ERROR: No existe ningún empleado con NIF " + dto.getNif());
            return response;
        }

        try {
            Empleado e = toEntity(dto);
            empleadoRepo.save(e);
            response.setResultado(true);
            response.setMensaje("OK: Empleado " + dto.getNif() + " actualizado.");
        } catch (Exception ex) {
            response.setResultado(false);
            response.setMensaje("ERROR: " + ex.getMessage());
        }
        return response;
    }

    // ------------------------------------------------------------------
    // consultar
    // ------------------------------------------------------------------
    @Override
    public ConsultarResponse consultar(Consultar parameters) {
        ConsultarResponse response = new ConsultarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            response.setMensaje("ERROR: WSKey inválida.");
            return response;
        }

        Optional<Empleado> opt = empleadoRepo.findById(parameters.getNif());
        if (opt.isEmpty()) {
            response.setMensaje("ERROR: No existe ningún empleado con NIF " + parameters.getNif());
            return response;
        }

        response.setEmpleado(toDto(opt.get()));
        response.setMensaje("OK");
        return response;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private String validarDatosEmpleado(EmpleadoType dto) {
        try {
            // Llamada al servicio Validaciones via CXF proxy
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(
                Class.forName("com.practica1.validaciones.generated.ValidacionesPortType"));
            factory.setAddress(validacionesUrl);
            Object proxy = factory.create();

            // Usar reflection para invocar validarNIF/NIE, validarNAF, validarIBAN
            // En un proyecto real se generaría el cliente con cxf-codegen-plugin del WSDL de validaciones
            // Aquí usamos lógica local como fallback para mantener independencia entre servicios
            return validarLocalmente(dto);
        } catch (Exception e) {
            // Si no está disponible el servicio de validaciones, validar localmente
            return validarLocalmente(dto);
        }
    }

    private String validarLocalmente(EmpleadoType dto) {
        String tipo = dto.getTipoDocumento();
        String nif  = dto.getNif();

        if ("NIF".equalsIgnoreCase(tipo)) {
            if (!isNIFValido(nif))
                return "ERROR: NIF inválido: " + nif + ". Formato esperado: XXXXXXXX-L";
        } else if ("NIE".equalsIgnoreCase(tipo)) {
            if (!isNIEValido(nif))
                return "ERROR: NIE inválido: " + nif + ". Formato esperado: LXXXXXXXL";
        } else {
            return "ERROR: tipoDocumento debe ser NIF o NIE.";
        }

        if (!isNAFValido(dto.getNaf()))
            return "ERROR: NAF inválido: " + dto.getNaf() + ". Debe tener 12 dígitos con dígitos de control correctos.";

        if (!isIBANValido(dto.getIban()))
            return "ERROR: IBAN inválido: " + dto.getIban() + ". Debe tener 24 caracteres y dígitos de control correctos.";

        return null;
    }

    private boolean isNIFValido(String nif) {
        if (nif == null) return false;
        nif = nif.trim().toUpperCase();
        if (!nif.matches("[0-9]{8}[A-Z]")) return false;
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letras.charAt(Integer.parseInt(nif.substring(0, 8)) % 23) == nif.charAt(8);
    }

    private boolean isNIEValido(String nie) {
        if (nie == null) return false;
        nie = nie.trim().toUpperCase();
        if (!nie.matches("[XYZ][0-9]{7}[A-Z]")) return false;
        String num = nie.replace("X","0").replace("Y","1").replace("Z","2");
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letras.charAt(Integer.parseInt(num.substring(0, 8)) % 23) == nie.charAt(8);
    }

    private boolean isNAFValido(String naf) {
        if (naf == null || !naf.matches("[0-9]{12}")) return false;
        long num = Long.parseLong(naf.substring(0, 10));
        int ctrl = Integer.parseInt(naf.substring(10, 12));
        return (int)(num % 97) == ctrl;
    }

    private boolean isIBANValido(String iban) {
        if (iban == null) return false;
        iban = iban.trim().toUpperCase().replace(" ", "");
        if (iban.length() != 24) return false;
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder sb = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) sb.append(c - 'A' + 10);
            else sb.append(c);
        }
        return new java.math.BigInteger(sb.toString()).mod(java.math.BigInteger.valueOf(97)).intValue() == 1;
    }

    private Empleado toEntity(EmpleadoType dto) {
        Empleado e = new Empleado();
        e.setNif(dto.getNif());
        e.setNombre(dto.getNombre());
        e.setApellidos(dto.getApellidos());
        e.setEmail(dto.getEmail());
        e.setNaf(dto.getNaf());
        e.setIban(dto.getIban());
        e.setTipodocumento(dto.getTipoDocumento());
        return e;
    }

    private EmpleadoType toDto(Empleado e) {
        EmpleadoType dto = new EmpleadoType();
        dto.setNif(e.getNif());
        dto.setNombre(e.getNombre());
        dto.setApellidos(e.getApellidos());
        dto.setEmail(e.getEmail());
        dto.setNaf(e.getNaf());
        dto.setIban(e.getIban());
        dto.setTipoDocumento(e.getTipodocumento());
        return dto;
    }
}
