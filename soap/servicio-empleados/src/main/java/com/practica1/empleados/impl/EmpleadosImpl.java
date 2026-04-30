package com.practica1.empleados.impl;

import com.practica1.empleados.generated.EmpleadoType;
import com.practica1.empleados.generated.EmpleadosPortType;
import com.practica1.empleados.model.Empleado;
import com.practica1.empleados.repository.EmpleadoRepository;
import com.practica1.empleados.repository.WskeyRepository;
import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

    @Autowired private EmpleadoRepository empleadoRepo;
    @Autowired private WskeyRepository    wskeyRepo;

    @Override
    public void nuevo(EmpleadoType empleado, String wskey,
                      Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida."; return;
        }
        if (empleado == null) {
            resultado.value = false; mensaje.value = "ERROR: Datos del empleado nulos."; return;
        }
        String err = validarLocalmente(empleado);
        if (err != null) {
            resultado.value = false; mensaje.value = err; return;
        }
        if (empleadoRepo.existsById(empleado.getNif())) {
            resultado.value = false;
            mensaje.value   = "ERROR: Ya existe un empleado con NIF " + empleado.getNif();
            return;
        }
        try {
            empleadoRepo.save(toEntity(empleado));
            resultado.value = true;
            mensaje.value   = "OK: Empleado " + empleado.getNif() + " creado.";
        } catch (Exception ex) {
            resultado.value = false;
            mensaje.value   = "ERROR BD: " + ex.getMessage();
        }
    }

    @Override
    public void borrar(String nif, String wskey,
                       Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida."; return;
        }
        if (!empleadoRepo.existsById(nif)) {
            resultado.value = false; mensaje.value = "ERROR: No existe empleado con NIF " + nif; return;
        }
        try {
            empleadoRepo.deleteById(nif);
            resultado.value = true; mensaje.value = "OK: Empleado " + nif + " eliminado.";
        } catch (Exception ex) {
            resultado.value = false; mensaje.value = "ERROR BD: " + ex.getMessage();
        }
    }

    @Override
    public void modificar(EmpleadoType empleado, String wskey,
                          Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida."; return;
        }
        if (!empleadoRepo.existsById(empleado.getNif())) {
            resultado.value = false;
            mensaje.value   = "ERROR: No existe empleado con NIF " + empleado.getNif();
            return;
        }
        try {
            empleadoRepo.save(toEntity(empleado));
            resultado.value = true; mensaje.value = "OK: Empleado " + empleado.getNif() + " actualizado.";
        } catch (Exception ex) {
            resultado.value = false; mensaje.value = "ERROR BD: " + ex.getMessage();
        }
    }

    @Override
    public void consultar(String nif, String wskey,
                          Holder<EmpleadoType> empleado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            mensaje.value = "ERROR: WSKey inválida."; return;
        }
        Optional<Empleado> opt = empleadoRepo.findById(nif);
        if (opt.isEmpty()) {
            mensaje.value = "ERROR: No existe empleado con NIF " + nif; return;
        }
        empleado.value = toDto(opt.get());
        mensaje.value  = "OK";
    }

    private String validarLocalmente(EmpleadoType dto) {
        String tipo = dto.getTipoDocumento();
        String nif  = dto.getNif();
        if ("NIF".equalsIgnoreCase(tipo)) {
            if (!isNIFValido(nif)) return "ERROR: NIF inválido: " + nif;
        } else if ("NIE".equalsIgnoreCase(tipo)) {
            if (!isNIEValido(nif)) return "ERROR: NIE inválido: " + nif;
        } else {
            return "ERROR: tipoDocumento debe ser NIF o NIE.";
        }
        if (!isNAFValido(dto.getNaf()))   return "ERROR: NAF inválido: " + dto.getNaf();
        if (!isIBANValido(dto.getIban())) return "ERROR: IBAN inválido: " + dto.getIban();
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
        String num    = nie.replace("X","0").replace("Y","1").replace("Z","2");
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letras.charAt(Integer.parseInt(num.substring(0, 8)) % 23) == nie.charAt(8);
    }

    private boolean isNAFValido(String naf) {
        if (naf == null || !naf.matches("[0-9]{12}")) return false;
        long num  = Long.parseLong(naf.substring(0, 10));
        int  ctrl = Integer.parseInt(naf.substring(10, 12));
        return (int)(num % 97) == ctrl;
    }

    private boolean isIBANValido(String iban) {
        if (iban == null) return false;
        iban = iban.trim().toUpperCase().replace(" ", "");
        if (iban.length() != 24) return false;
        String        rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder sb         = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) sb.append(c - 'A' + 10); else sb.append(c);
        }
        return new BigInteger(sb.toString()).mod(BigInteger.valueOf(97)).intValue() == 1;
    }

    private Empleado toEntity(EmpleadoType dto) {
        Empleado e = new Empleado();
        e.setNif(dto.getNif());             e.setNombre(dto.getNombre());
        e.setApellidos(dto.getApellidos()); e.setEmail(dto.getEmail());
        e.setNaf(dto.getNaf());             e.setIban(dto.getIban());
        e.setTipodocumento(dto.getTipoDocumento());
        return e;
    }

    private EmpleadoType toDto(Empleado e) {
        EmpleadoType dto = new EmpleadoType();
        dto.setNif(e.getNif());             dto.setNombre(e.getNombre());
        dto.setApellidos(e.getApellidos()); dto.setEmail(e.getEmail());
        dto.setNaf(e.getNaf());             dto.setIban(e.getIban());
        dto.setTipoDocumento(e.getTipodocumento());
        return dto;
    }
}
