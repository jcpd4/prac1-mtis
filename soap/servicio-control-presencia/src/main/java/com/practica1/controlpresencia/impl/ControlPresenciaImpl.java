package com.practica1.controlpresencia.impl;

import com.practica1.controlpresencia.generated.*;
import com.practica1.controlpresencia.model.ControlPresencia;
import com.practica1.controlpresencia.model.ControlPresenciaId;
import com.practica1.controlpresencia.model.Empleado;
import com.practica1.controlpresencia.repository.ControlPresenciaRepository;
import com.practica1.controlpresencia.repository.EmpleadoRepository;
import com.practica1.controlpresencia.repository.WskeyRepository;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public RegistrarResponse registrar(Registrar parameters) {
        RegistrarResponse r = new RegistrarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            r.setResultado(false);
            r.setMensaje("ERROR: WSKey inválida.");
            return r;
        }

        ControlPresenciaId id = new ControlPresenciaId(parameters.getNif(), parameters.getCodigosala());
        if (cpRepo.existsById(id)) {
            r.setResultado(false);
            r.setMensaje("ERROR: El empleado " + parameters.getNif() + " ya está registrado en la sala " + parameters.getCodigosala());
            return r;
        }

        try {
            ControlPresencia cp = new ControlPresencia();
            cp.setNif(parameters.getNif());
            cp.setCodigosala(parameters.getCodigosala());
            cpRepo.save(cp);
            r.setResultado(true);
            r.setMensaje("OK: Presencia registrada.");
        } catch (Exception ex) {
            r.setResultado(false);
            r.setMensaje("ERROR: " + ex.getMessage());
        }
        return r;
    }

    @Override
    public EliminarResponse eliminar(Eliminar parameters) {
        EliminarResponse r = new EliminarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            r.setResultado(false);
            r.setMensaje("ERROR: WSKey inválida.");
            return r;
        }

        ControlPresenciaId id = new ControlPresenciaId(parameters.getNif(), parameters.getCodigosala());
        if (!cpRepo.existsById(id)) {
            r.setResultado(false);
            r.setMensaje("ERROR: No existe registro de presencia para NIF=" + parameters.getNif() + " sala=" + parameters.getCodigosala());
            return r;
        }

        try {
            cpRepo.deleteById(id);
            r.setResultado(true);
            r.setMensaje("OK: Presencia eliminada.");
        } catch (Exception ex) {
            r.setResultado(false);
            r.setMensaje("ERROR: " + ex.getMessage());
        }
        return r;
    }

    @Override
    public ControlEmpleadosSalaResponse controlEmpleadosSala(ControlEmpleadosSala parameters) {
        ControlEmpleadosSalaResponse r = new ControlEmpleadosSalaResponse();
        ListaEmpleadosType lista = new ListaEmpleadosType();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            r.setMensaje("ERROR: WSKey inválida.");
            r.setEmpleados(lista);
            return r;
        }

        try {
            List<ControlPresencia> presencias = cpRepo.findByCodigosala(parameters.getCodigosala());
            for (ControlPresencia cp : presencias) {
                Optional<Empleado> empOpt = empRepo.findById(cp.getNif());
                empOpt.ifPresent(e -> lista.getEmpleado().add(toDto(e)));
            }
            r.setEmpleados(lista);
            r.setMensaje("OK: " + lista.getEmpleado().size() + " empleado(s) en sala " + parameters.getCodigosala());
        } catch (Exception ex) {
            r.setMensaje("ERROR: " + ex.getMessage());
            r.setEmpleados(lista);
        }
        return r;
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
