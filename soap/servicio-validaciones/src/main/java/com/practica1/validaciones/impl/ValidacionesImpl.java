package com.practica1.validaciones.impl;

import com.practica1.validaciones.generated.ValidacionesPortType;
import com.practica1.validaciones.repository.WskeyRepository;
import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;
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

    @Override
    public void validarNIF(String nif, String wskey,
                           Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepository.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida. Acceso denegado."; return;
        }
        if (nif == null || nif.isBlank()) {
            resultado.value = false; mensaje.value = "ERROR: El NIF no puede estar vacío."; return;
        }
        nif = nif.trim().toUpperCase();
        if (!nif.matches("[0-9]{8}[A-Z]")) {
            resultado.value = false;
            mensaje.value = "ERROR: Formato incorrecto. Esperado: XXXXXXXX-L (ej: 33900165M)";
            return;
        }
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        char letraEsperada = letras.charAt(Integer.parseInt(nif.substring(0, 8)) % 23);
        if (nif.charAt(8) != letraEsperada) {
            resultado.value = false;
            mensaje.value = "ERROR: Letra de control incorrecta. Esperada: " + letraEsperada;
            return;
        }
        resultado.value = true; mensaje.value = "OK: NIF válido.";
    }

    @Override
    public void validarNIE(String nie, String wskey,
                           Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepository.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida. Acceso denegado."; return;
        }
        if (nie == null || nie.isBlank()) {
            resultado.value = false; mensaje.value = "ERROR: El NIE no puede estar vacío."; return;
        }
        nie = nie.trim().toUpperCase();
        if (!nie.matches("[XYZ][0-9]{7}[A-Z]")) {
            resultado.value = false;
            mensaje.value = "ERROR: Formato incorrecto. Esperado: LXXXXXXXL (ej: Z9817136M)";
            return;
        }
        String num = nie.replace("X", "0").replace("Y", "1").replace("Z", "2");
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        char letraEsperada = letras.charAt(Integer.parseInt(num.substring(0, 8)) % 23);
        if (nie.charAt(8) != letraEsperada) {
            resultado.value = false;
            mensaje.value = "ERROR: Letra de control incorrecta. Esperada: " + letraEsperada;
            return;
        }
        resultado.value = true; mensaje.value = "OK: NIE válido.";
    }

    @Override
    public void validarNAF(String naf, String wskey,
                           Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepository.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida. Acceso denegado."; return;
        }
        if (naf == null || naf.isBlank()) {
            resultado.value = false; mensaje.value = "ERROR: El NAF no puede estar vacío."; return;
        }
        naf = naf.trim();
        if (!naf.matches("[0-9]{12}")) {
            resultado.value = false;
            mensaje.value = "ERROR: Formato incorrecto. Esperado: 12 dígitos XXXXXXXXXXYY (ej: 527575965274)";
            return;
        }
        long numero  = Long.parseLong(naf.substring(0, 10));
        int  control = Integer.parseInt(naf.substring(10, 12));
        int  esperado = (int) (numero % 97);
        if (control != esperado) {
            resultado.value = false;
            mensaje.value = "ERROR: Dígitos de control incorrectos. Esperado: " + String.format("%02d", esperado);
            return;
        }
        resultado.value = true; mensaje.value = "OK: NAF válido.";
    }

    @Override
    public void validarIBAN(String iban, String wskey,
                            Holder<Boolean> resultado, Holder<String> mensaje) {
        if (!wskeyRepository.existsByClave(wskey)) {
            resultado.value = false; mensaje.value = "ERROR: WSKey inválida. Acceso denegado."; return;
        }
        if (iban == null || iban.isBlank()) {
            resultado.value = false; mensaje.value = "ERROR: El IBAN no puede estar vacío."; return;
        }
        iban = iban.trim().toUpperCase().replace(" ", "");
        if (iban.length() != 24) {
            resultado.value = false;
            mensaje.value = "ERROR: El IBAN español debe tener exactamente 24 caracteres. Recibidos: " + iban.length();
            return;
        }
        if (!iban.matches("[A-Z]{2}[0-9]{2}[0-9A-Z]+")) {
            resultado.value = false;
            mensaje.value = "ERROR: Formato de IBAN inválido. Ejemplo: ES0690000001210123456789";
            return;
        }
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder sb = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) sb.append(c - 'A' + 10); else sb.append(c);
        }
        if (new BigInteger(sb.toString()).mod(BigInteger.valueOf(97)).intValue() != 1) {
            resultado.value = false; mensaje.value = "ERROR: Dígitos de control del IBAN incorrectos."; return;
        }
        resultado.value = true; mensaje.value = "OK: IBAN válido.";
    }
}
