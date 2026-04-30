package com.practica1.validaciones.impl;

import com.practica1.validaciones.generated.*;
import com.practica1.validaciones.repository.WskeyRepository;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@WebService(
    endpointInterface = "com.practica1.validaciones.generated.ValidacionesPortType",
    serviceName       = "ValidacionesService",
    portName          = "ValidacionesPort",
    targetNamespace   = "http://practica1.com/validaciones",
    wsdlLocation      = "classpath:wsdl/Validaciones.wsdl"
)
public class ValidacionesImpl implements ValidacionesPortType {

    @Autowired
    private WskeyRepository wskeyRepository;

    // ------------------------------------------------------------------
    // validarNIF
    // ------------------------------------------------------------------
    @Override
    public ValidarNIFResponse validarNIF(ValidarNIF parameters) {
        ValidarNIFResponse response = new ValidarNIFResponse();

        if (!wskeyRepository.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida. Acceso denegado.");
            return response;
        }

        String nif = parameters.getNif();
        if (nif == null || nif.isBlank()) {
            response.setResultado(false);
            response.setMensaje("ERROR: El NIF no puede estar vacío.");
            return response;
        }

        nif = nif.trim().toUpperCase();
        if (!nif.matches("[0-9]{8}[A-Z]")) {
            response.setResultado(false);
            response.setMensaje("ERROR: Formato incorrecto. Esperado: XXXXXXXX-L (ej: 33900165M)");
            return response;
        }

        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(nif.substring(0, 8));
        char letraEsperada = letras.charAt(numero % 23);

        if (Character.toUpperCase(nif.charAt(8)) != letraEsperada) {
            response.setResultado(false);
            response.setMensaje("ERROR: Letra de control incorrecta. Esperada: " + letraEsperada);
            return response;
        }

        response.setResultado(true);
        response.setMensaje("OK: NIF válido.");
        return response;
    }

    // ------------------------------------------------------------------
    // validarNIE
    // ------------------------------------------------------------------
    @Override
    public ValidarNIEResponse validarNIE(ValidarNIE parameters) {
        ValidarNIEResponse response = new ValidarNIEResponse();

        if (!wskeyRepository.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida. Acceso denegado.");
            return response;
        }

        String nie = parameters.getNie();
        if (nie == null || nie.isBlank()) {
            response.setResultado(false);
            response.setMensaje("ERROR: El NIE no puede estar vacío.");
            return response;
        }

        nie = nie.trim().toUpperCase();
        if (!nie.matches("[XYZ][0-9]{7}[A-Z]")) {
            response.setResultado(false);
            response.setMensaje("ERROR: Formato incorrecto. Esperado: LXXXXXXXL (ej: Z9817136M)");
            return response;
        }

        // Sustituir letra inicial: X→0, Y→1, Z→2
        String nieNumerico = nie.replace("X", "0").replace("Y", "1").replace("Z", "2");
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(nieNumerico.substring(0, 8));
        char letraEsperada = letras.charAt(numero % 23);

        if (nie.charAt(8) != letraEsperada) {
            response.setResultado(false);
            response.setMensaje("ERROR: Letra de control incorrecta. Esperada: " + letraEsperada);
            return response;
        }

        response.setResultado(true);
        response.setMensaje("OK: NIE válido.");
        return response;
    }

    // ------------------------------------------------------------------
    // validarNAF
    // ------------------------------------------------------------------
    @Override
    public ValidarNAFResponse validarNAF(ValidarNAF parameters) {
        ValidarNAFResponse response = new ValidarNAFResponse();

        if (!wskeyRepository.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida. Acceso denegado.");
            return response;
        }

        String naf = parameters.getNaf();
        if (naf == null || naf.isBlank()) {
            response.setResultado(false);
            response.setMensaje("ERROR: El NAF no puede estar vacío.");
            return response;
        }

        naf = naf.trim();
        if (!naf.matches("[0-9]{12}")) {
            response.setResultado(false);
            response.setMensaje("ERROR: Formato incorrecto. Esperado: 12 dígitos XXXXXXXXXXYY (ej: 527575965274)");
            return response;
        }

        long numero  = Long.parseLong(naf.substring(0, 10));
        int control  = Integer.parseInt(naf.substring(10, 12));
        int esperado = (int) (numero % 97);

        if (control != esperado) {
            response.setResultado(false);
            response.setMensaje("ERROR: Dígitos de control incorrectos. Esperado: " +
                    String.format("%02d", esperado));
            return response;
        }

        response.setResultado(true);
        response.setMensaje("OK: NAF válido.");
        return response;
    }

    // ------------------------------------------------------------------
    // validarIBAN
    // ------------------------------------------------------------------
    @Override
    public ValidarIBANResponse validarIBAN(ValidarIBAN parameters) {
        ValidarIBANResponse response = new ValidarIBANResponse();

        if (!wskeyRepository.existsByClave(parameters.getWskey())) {
            response.setResultado(false);
            response.setMensaje("ERROR: WSKey inválida. Acceso denegado.");
            return response;
        }

        String iban = parameters.getIban();
        if (iban == null || iban.isBlank()) {
            response.setResultado(false);
            response.setMensaje("ERROR: El IBAN no puede estar vacío.");
            return response;
        }

        iban = iban.trim().toUpperCase().replace(" ", "");

        if (iban.length() != 24) {
            response.setResultado(false);
            response.setMensaje("ERROR: El IBAN español debe tener exactamente 24 caracteres. Recibidos: " + iban.length());
            return response;
        }

        if (!iban.matches("[A-Z]{2}[0-9]{2}[0-9A-Z]+")) {
            response.setResultado(false);
            response.setMensaje("ERROR: Formato de IBAN inválido. Ejemplo: ES0690000001210123456789");
            return response;
        }

        // Mover los 4 primeros caracteres al final
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // Convertir letras a números (A=10, B=11, ..., Z=35)
        StringBuilder numericIban = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numericIban.append(c - 'A' + 10);
            } else {
                numericIban.append(c);
            }
        }

        // Verificar que mod 97 == 1
        BigInteger ibanNumber = new BigInteger(numericIban.toString());
        if (ibanNumber.mod(BigInteger.valueOf(97)).intValue() != 1) {
            response.setResultado(false);
            response.setMensaje("ERROR: Dígitos de control del IBAN incorrectos.");
            return response;
        }

        response.setResultado(true);
        response.setMensaje("OK: IBAN válido.");
        return response;
    }
}
