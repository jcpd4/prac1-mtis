package com.practica1.controlaccesos.impl;

import com.practica1.controlaccesos.generated.ControlAccesosPortType;
import com.practica1.controlaccesos.generated.ListaRegistrosType;
import com.practica1.controlaccesos.generated.RegistroAccesoType;
import com.practica1.controlaccesos.model.ControlAcceso;
import com.practica1.controlaccesos.repository.ControlAccesoRepository;
import com.practica1.controlaccesos.repository.WskeyRepository;
import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;
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
    public void registrar(String nif, String codigosala, String codigodispositivo, String wskey,
                          Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepo.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida."; return;
        }
        try {
            ControlAcceso ca = new ControlAcceso();
            ca.setNif(nif);
            ca.setCodigosala(codigosala);
            ca.setCodigodispositivo(codigodispositivo);
            ca.setFechahora(LocalDateTime.now());
            repo.save(ca);
            resultado.value = true; mensaje.value = "OK: Acceso registrado. ID=" + ca.getId();
        } catch (Exception ex) {
            resultado.value = false; mensaje.value = "ERROR: " + ex.getMessage();
        }
    }

    @Override
    public void consultar(String nif, String codigosala, String codigodispositivo,
                          XMLGregorianCalendar fechaDesde, XMLGregorianCalendar fechaHasta, String wskey,
                          Holder<ListaRegistrosType> registros, Holder<String> mensaje) {
        registros.value = new ListaRegistrosType();
        if (!wskeyRepo.existsByClave(wskey)) {
            mensaje.value = "ERROR: WSKey inválida."; return;
        }
        try {
            LocalDateTime desde = fechaDesde != null ? toLocalDateTime(fechaDesde) : null;
            LocalDateTime hasta = fechaHasta != null ? toLocalDateTime(fechaHasta) : null;

            List<ControlAcceso> lista = repo.buscarConFiltros(nif, codigosala, codigodispositivo, desde, hasta);

            for (ControlAcceso ca : lista) {
                RegistroAccesoType reg = new RegistroAccesoType();
                reg.setId(ca.getId());
                reg.setNif(ca.getNif());
                reg.setCodigosala(ca.getCodigosala());
                reg.setCodigodispositivo(ca.getCodigodispositivo());
                reg.setFechahora(toXmlCalendar(ca.getFechahora()));
                registros.value.getRegistro().add(reg);
            }
            mensaje.value = "OK: " + lista.size() + " registro(s) encontrado(s).";
        } catch (Exception ex) {
            mensaje.value = "ERROR: " + ex.getMessage();
        }
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
