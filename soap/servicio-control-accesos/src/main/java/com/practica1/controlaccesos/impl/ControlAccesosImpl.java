package com.practica1.controlaccesos.impl;

import com.practica1.controlaccesos.generated.*;
import com.practica1.controlaccesos.model.ControlAcceso;
import com.practica1.controlaccesos.repository.ControlAccesoRepository;
import com.practica1.controlaccesos.repository.WskeyRepository;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;

@Service
@WebService(
    endpointInterface = "com.practica1.controlaccesos.generated.ControlAccesosPortType",
    serviceName       = "ControlAccesosService",
    portName          = "ControlAccesosPort",
    targetNamespace   = "http://practica1.com/controlaccesos",
    wsdlLocation      = "classpath:wsdl/ControlAccesos.wsdl"
)
public class ControlAccesosImpl implements ControlAccesosPortType {

    @Autowired private ControlAccesoRepository repo;
    @Autowired private WskeyRepository wskeyRepo;

    @Override
    public RegistrarResponse registrar(Registrar parameters) {
        RegistrarResponse r = new RegistrarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            r.setResultado(false);
            r.setMensaje("ERROR: WSKey inválida.");
            return r;
        }

        try {
            ControlAcceso ca = new ControlAcceso();
            ca.setNif(parameters.getNif());
            ca.setCodigosala(parameters.getCodigosala());
            ca.setCodigodispositivo(parameters.getCodigodispositivo());
            ca.setFechahora(LocalDateTime.now());
            repo.save(ca);
            r.setResultado(true);
            r.setMensaje("OK: Acceso registrado. ID=" + ca.getId());
        } catch (Exception ex) {
            r.setResultado(false);
            r.setMensaje("ERROR: " + ex.getMessage());
        }
        return r;
    }

    @Override
    public ConsultarResponse consultar(Consultar parameters) {
        ConsultarResponse r = new ConsultarResponse();

        if (!wskeyRepo.existsByClave(parameters.getWskey())) {
            r.setMensaje("ERROR: WSKey inválida.");
            r.setRegistros(new ListaRegistrosType());
            return r;
        }

        try {
            LocalDateTime desde = toLocalDateTime(parameters.getFechaDesde());
            LocalDateTime hasta = toLocalDateTime(parameters.getFechaHasta());

            List<ControlAcceso> lista = repo.buscarConFiltros(
                    parameters.getNif(),
                    parameters.getCodigosala(),
                    parameters.getCodigodispositivo(),
                    desde, hasta);

            ListaRegistrosType listaType = new ListaRegistrosType();
            for (ControlAcceso ca : lista) {
                RegistroAccesoType reg = new RegistroAccesoType();
                reg.setId(ca.getId());
                reg.setNif(ca.getNif());
                reg.setCodigosala(ca.getCodigosala());
                reg.setCodigodispositivo(ca.getCodigodispositivo());
                reg.setFechahora(toXmlCalendar(ca.getFechahora()));
                listaType.getRegistro().add(reg);
            }

            r.setRegistros(listaType);
            r.setMensaje("OK: " + lista.size() + " registro(s) encontrado(s).");
        } catch (Exception ex) {
            r.setMensaje("ERROR: " + ex.getMessage());
            r.setRegistros(new ListaRegistrosType());
        }
        return r;
    }

    private LocalDateTime toLocalDateTime(XMLGregorianCalendar cal) {
        return cal.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    private XMLGregorianCalendar toXmlCalendar(LocalDateTime ldt) {
        try {
            GregorianCalendar gc = GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (Exception e) {
            return null;
        }
    }
}
