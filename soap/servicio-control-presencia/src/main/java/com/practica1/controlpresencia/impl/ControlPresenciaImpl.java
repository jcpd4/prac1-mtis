package com.practica1.controlpresencia.impl;

import com.practica1.controlpresencia.generated.ControlPresenciaPortType;
import com.practica1.controlpresencia.generated.EmpleadoType;
import com.practica1.controlpresencia.generated.ListaEmpleadosType;
import com.practica1.controlpresencia.model.ControlPresencia;
import com.practica1.controlpresencia.model.ControlPresenciaId;
import com.practica1.controlpresencia.model.Empleado;
import com.practica1.controlpresencia.repository.ControlPresenciaRepository;
import com.practica1.controlpresencia.repository.EmpleadoRepository;
import com.practica1.controlpresencia.repository.WskeyRepository;
import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
    endpointInterface = "com.practica1.controlpresencia.generated.ControlPresenciaPortType",
    serviceName       = "ControlPresenciaService",
    portName          = "ControlPresenciaPort",
    targetNamespace   = "http://practica1.com/controlpresencia",
    wsdlLocation      = "classpath:wsdl/ControlPresencia.wsdl"
)
public class ControlPresenciaImpl implements ControlPresenciaPortType {

    @Autowired private ControlPresenciaRepository cpRepo;
    @Autowired private EmpleadoRepository         empRepo;
    @Autowired private WskeyRepository            wskeyRepo;

    @Override
    public void registrar(String nif, String codigosala, String wskey,
                          Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida."; return;
        }
        ControlPresenciaId id = new ControlPresenciaId(nif, codigosala);
        if (cpRepo.existsById(id)) {
            resultado.value = false;
            mensaje.value   = "ERROR: El empleado " + nif + " ya está en sala " + codigosala;
            return;
        }
        try {
            ControlPresencia cp = new ControlPresencia();
            cp.setNif(nif); cp.setCodigosala(codigosala);
            cpRepo.save(cp);
            resultado.value = true; mensaje.value = "OK: Presencia registrada.";
        } catch (Exception ex) {
            resultado.value = false; mensaje.value = "ERROR BD: " + ex.getMessage();
        }
    }

    @Override
    public void eliminar(String nif, String codigosala, String wskey,
                         Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida."; return;
        }
        ControlPresenciaId id = new ControlPresenciaId(nif, codigosala);
        if (!cpRepo.existsById(id)) {
            resultado.value = false;
            mensaje.value   = "ERROR: No existe presencia para NIF=" + nif + " sala=" + codigosala;
            return;
        }
        try {
            cpRepo.deleteById(id);
            resultado.value = true; mensaje.value = "OK: Presencia eliminada.";
        } catch (Exception ex) {
            resultado.value = false; mensaje.value = "ERROR BD: " + ex.getMessage();
        }
    }

    @Override
    public void controlEmpleadosSala(String codigosala, String wskey,
                                     Holder<ListaEmpleadosType> empleados, Holder<String> mensaje) {
        empleados.value = new ListaEmpleadosType();
        if (!wskeyRepo.existsByClave(wskey)) {
            mensaje.value = "ERROR: WSKey inválida."; return;
        }
        try {
            List<ControlPresencia> presencias = cpRepo.findByCodigosala(codigosala);
            for (ControlPresencia cp : presencias) {
                String nifPresencia = cp.getNif();
                if (nifPresencia != null) {
                    empRepo.findById(nifPresencia)
                           .ifPresent(e -> empleados.value.getEmpleado().add(toDto(e)));
                }
            }
            mensaje.value = "OK: " + empleados.value.getEmpleado().size() + " empleado(s) en sala " + codigosala;
        } catch (Exception ex) {
            mensaje.value = "ERROR: " + ex.getMessage();
        }
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
