# Guía de pruebas manuales — Práctica 1 MTIS

---

## Antes de empezar: arrancar el entorno

Cada vez que quieras probar, ejecuta estos comandos en terminales separadas:

**Terminal 1 — Mailpit (servidor de email):**
```bash
mailpit --smtp=0.0.0.0:1025 --listen=0.0.0.0:8025
```

**Terminales 2–9 — Un servicio por terminal:**
```bash
# Terminal 2
cd soap/servicio-validaciones && mvn spring-boot:run       # puerto 8081

# Terminal 3
cd soap/servicio-empleados && mvn spring-boot:run          # puerto 8082

# Terminal 4
cd soap/servicio-control-accesos && mvn spring-boot:run    # puerto 8083

# Terminal 5
cd soap/servicio-control-presencia && mvn spring-boot:run  # puerto 8084

# Terminal 6
cd rest/servicio-salas && mvn spring-boot:run              # puerto 8085

# Terminal 7
cd rest/servicio-niveles && mvn spring-boot:run            # puerto 8086

# Terminal 8
cd rest/servicio-dispositivos && mvn spring-boot:run       # puerto 8087

# Terminal 9
cd rest/servicio-notificaciones && mvn spring-boot:run     # puerto 8088
```

Espera hasta que cada terminal muestre `Started ... in X seconds`.

Si algún puerto está ocupado de una sesión anterior:
```bash
lsof -ti:8081,8082,8083,8084,8085,8086,8087,8088 | xargs kill -9 2>/dev/null
```

---

## Valores de prueba útiles

| Dato | Valor válido | Valor inválido |
|---|---|---|
| NIF | `33900165M` | `33900165X` (letra incorrecta) |
| NIE | `Z9817136M` | `Z9817136X` |
| NAF | `527575965274` · `123456789002` | `000000000000` |
| IBAN | `ES0690000001210123456789` | `ES0000000000000000000000` |
| WSKey válida | `PRACTICA1_KEY_2024` | `CLAVE_MALA` |
| Sala existente | `S001` · `S002` · `S003` | `S999` |
| Nivel existente | `1` · `2` · `3` | `99` |
| Dispositivo existente | `D001` · `D002` · `D003` | `D999` |
| Empleado existente | `33900165M` | `00000000A` |

---

## PARTE 1 — Servicios SOAP con SoapUI

### Instalar SoapUI
```bash
brew install --cask soapui
```
O descarga la versión Open Source (gratuita) desde `soapui.org`.

### Crear un proyecto SOAP

1. Abre SoapUI → `File` → `New SOAP Project`
2. **Project Name**: nombre del servicio (ej: `Validaciones`)
3. **Initial WSDL**: pega la URL del WSDL del servicio
4. Marca `Create Requests` → `OK`

SoapUI cargará todas las operaciones en el panel izquierdo.

### Ejecutar una petición

1. Panel izquierdo → expande el proyecto → expande la operación
2. Doble clic en `Request 1`
3. Edita los valores entre las etiquetas XML
4. Pulsa el botón **▶ (play verde)**
5. La respuesta aparece en el panel derecho

---

### 1.1 Validaciones — `http://localhost:8081/ws/validaciones?wsdl`

Verifica el WSDL abriendo esa URL en el navegador. Debe mostrar XML.

#### validarNIF — válido
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:val="http://practica1.com/validaciones">
  <soapenv:Body>
    <val:validarNIF>
      <val:nif>33900165M</val:nif>
      <val:wskey>PRACTICA1_KEY_2024</val:wskey>
    </val:validarNIF>
  </soapenv:Body>
</soapenv:Envelope>
```
**Esperado:** `<resultado>true</resultado>` · `<mensaje>OK: NIF válido.</mensaje>`

#### validarNIF — letra incorrecta
```xml
<val:nif>33900165X</val:nif>
<val:wskey>PRACTICA1_KEY_2024</val:wskey>
```
**Esperado:** `<resultado>false</resultado>` · `ERROR: Letra de control incorrecta. Esperada: M`

#### validarNIF — WSKey inválida
```xml
<val:nif>33900165M</val:nif>
<val:wskey>CLAVE_MALA</val:wskey>
```
**Esperado:** `<resultado>false</resultado>` · `ERROR: WSKey inválida. Acceso denegado.`

#### validarNIE — válido
```xml
<val:nie>Z9817136M</val:nie>
<val:wskey>PRACTICA1_KEY_2024</val:wskey>
```
**Esperado:** `<resultado>true</resultado>` · `OK: NIE válido.`

#### validarNAF — válido
```xml
<val:naf>527575965274</val:naf>
<val:wskey>PRACTICA1_KEY_2024</val:wskey>
```
**Esperado:** `<resultado>true</resultado>` · `OK: NAF válido.`

#### validarIBAN — válido
```xml
<val:iban>ES0690000001210123456789</val:iban>
<val:wskey>PRACTICA1_KEY_2024</val:wskey>
```
**Esperado:** `<resultado>true</resultado>` · `OK: IBAN válido.`

---

### 1.2 Empleados — `http://localhost:8082/ws/empleados?wsdl`

#### consultar — empleado existente
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:emp="http://practica1.com/empleados">
  <soapenv:Body>
    <emp:consultar>
      <emp:nif>33900165M</emp:nif>
      <emp:wskey>PRACTICA1_KEY_2024</emp:wskey>
    </emp:consultar>
  </soapenv:Body>
</soapenv:Envelope>
```
**Esperado:** devuelve los datos de Juan García López (nombre, apellidos, email, naf, iban).

#### consultar — empleado inexistente
```xml
<emp:nif>99999999Z</emp:nif>
<emp:wskey>PRACTICA1_KEY_2024</emp:wskey>
```
**Esperado:** `ERROR: No existe empleado con NIF 99999999Z`

#### nuevo — empleado válido
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:emp="http://practica1.com/empleados">
  <soapenv:Body>
    <emp:nuevo>
      <emp:empleado>
        <emp:nif>12345678Z</emp:nif>
        <emp:nombre>Maria</emp:nombre>
        <emp:apellidos>Perez Ruiz</emp:apellidos>
        <emp:email>maria@test.com</emp:email>
        <emp:naf>123456789002</emp:naf>
        <emp:iban>ES9121000418450200051332</emp:iban>
        <emp:tipoDocumento>NIF</emp:tipoDocumento>
      </emp:empleado>
      <emp:wskey>PRACTICA1_KEY_2024</emp:wskey>
    </emp:nuevo>
  </soapenv:Body>
</soapenv:Envelope>
```
**Esperado:** `<resultado>true</resultado>` · `OK: Empleado 12345678Z creado.`

#### nuevo — duplicado (repite el mismo XML)
**Esperado:** `<resultado>false</resultado>` · `ERROR: Ya existe un empleado con NIF 12345678Z`

#### nuevo — NAF inválido
```xml
<emp:naf>000000000000</emp:naf>
```
**Esperado:** `<resultado>false</resultado>` · `ERROR: NAF inválido: 000000000000`

#### borrar
```xml
<emp:borrar>
  <emp:nif>12345678Z</emp:nif>
  <emp:wskey>PRACTICA1_KEY_2024</emp:wskey>
</emp:borrar>
```
**Esperado:** `<resultado>true</resultado>` · `OK: Empleado 12345678Z eliminado.`

---

### 1.3 Control Accesos — `http://localhost:8083/ws/controlaccesos?wsdl`

#### registrar
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ca="http://practica1.com/controlaccesos">
  <soapenv:Body>
    <ca:registrar>
      <ca:nif>33900165M</ca:nif>
      <ca:codigosala>S001</ca:codigosala>
      <ca:codigodispositivo>D001</ca:codigodispositivo>
      <ca:wskey>PRACTICA1_KEY_2024</ca:wskey>
    </ca:registrar>
  </soapenv:Body>
</soapenv:Envelope>
```
**Esperado:** `<resultado>true</resultado>` · `OK: Acceso registrado. ID=X`

> Puedes ejecutarlo varias veces — cada vez crea un registro nuevo con distinto ID.

#### consultar (con filtro de fechas)
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ca="http://practica1.com/controlaccesos">
  <soapenv:Body>
    <ca:consultar>
      <ca:nif>33900165M</ca:nif>
      <ca:codigosala></ca:codigosala>
      <ca:codigodispositivo></ca:codigodispositivo>
      <ca:fechaDesde>2026-01-01T00:00:00</ca:fechaDesde>
      <ca:fechaHasta>2026-12-31T23:59:59</ca:fechaHasta>
      <ca:wskey>PRACTICA1_KEY_2024</ca:wskey>
    </ca:consultar>
  </soapenv:Body>
</soapenv:Envelope>
```
**Esperado:** lista de registros + `OK: X registro(s) encontrado(s).`

> Dejar `codigosala` y `codigodispositivo` vacíos equivale a "sin filtro".

---

### 1.4 Control Presencia — `http://localhost:8084/ws/controlpresencia?wsdl`

#### registrar
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cp="http://practica1.com/controlpresencia">
  <soapenv:Body>
    <cp:registrar>
      <cp:nif>33900165M</cp:nif>
      <cp:codigosala>S001</cp:codigosala>
      <cp:wskey>PRACTICA1_KEY_2024</cp:wskey>
    </cp:registrar>
  </soapenv:Body>
</soapenv:Envelope>
```
**Esperado:** `<resultado>true</resultado>` · `OK: Presencia registrada.`

#### registrar — duplicado (repite el mismo XML)
**Esperado:** `<resultado>false</resultado>` · `ERROR: El empleado 33900165M ya está en sala S001`

#### controlEmpleadosSala
```xml
<cp:controlEmpleadosSala>
  <cp:codigosala>S001</cp:codigosala>
  <cp:wskey>PRACTICA1_KEY_2024</cp:wskey>
</cp:controlEmpleadosSala>
```
**Esperado:** lista con los datos de Juan García · `OK: 1 empleado(s) en sala S001`

#### eliminar
```xml
<cp:eliminar>
  <cp:nif>33900165M</cp:nif>
  <cp:codigosala>S001</cp:codigosala>
  <cp:wskey>PRACTICA1_KEY_2024</cp:wskey>
</cp:eliminar>
```
**Esperado:** `<resultado>true</resultado>` · `OK: Presencia eliminada.`

---

## PARTE 2 — Servicios REST con Postman

### Instalar Postman
```bash
brew install --cask postman
```
O descarga desde `postman.com` (versión gratuita).

### Hacer una petición en Postman

1. Botón `+` → nueva pestaña
2. Selecciona el **método** (GET / POST / PUT / DELETE) en el desplegable izquierdo
3. Escribe la **URL**
4. Pestaña **Headers** → añade:
   - Key: `wskey` · Value: `PRACTICA1_KEY_2024`
5. Para POST/PUT → pestaña **Body** → `raw` → tipo `JSON` → escribe el JSON
6. Pulsa **Send**
7. Comprueba el **código HTTP** (número arriba a la derecha de la respuesta) y el **body**

---

### 2.1 Salas — `http://localhost:8085/api/salas`

#### GET sala existente
- Método: `GET` · URL: `.../api/salas/S001` · Header `wskey: PRACTICA1_KEY_2024`

**Esperado:** HTTP `200` · `{"codigosala":"S001","nombre":"Recepción","nivel":1}`

#### GET sala inexistente
- URL: `.../api/salas/S999`

**Esperado:** HTTP `404` · `{"codigo":404,"mensaje":"No existe ninguna sala con código S999"}`

#### GET con WSKey inválida
- Header: `wskey: CLAVE_MALA`

**Esperado:** HTTP `401` · `{"codigo":401,"mensaje":"WSKey inválida. Acceso denegado."}`

#### POST crear sala
- Método: `POST` · URL: `.../api/salas`
- Header: `Content-Type: application/json`
- Body:
```json
{
  "codigosala": "S099",
  "nombre": "Sala de prueba",
  "nivel": 1
}
```
**Esperado:** HTTP `200` · `{"mensaje":"Sala S099 creada correctamente."}`

#### POST sala duplicada (mismo body)
**Esperado:** HTTP `409` · `{"codigo":409,"mensaje":"Ya existe una sala con código S099"}`

#### PUT modificar sala
- Método: `PUT` · URL: `.../api/salas/S099`
- Body:
```json
{
  "nombre": "Sala Modificada",
  "nivel": 2
}
```
**Esperado:** HTTP `200` · `{"mensaje":"Sala S099 actualizada correctamente."}`

#### DELETE borrar sala
- Método: `DELETE` · URL: `.../api/salas/S099`

**Esperado:** HTTP `200` · `{"mensaje":"Sala S099 eliminada correctamente."}`

#### DELETE sala ya borrada
**Esperado:** HTTP `404`

---

### 2.2 Niveles — `http://localhost:8086/api/niveles`

> El campo del body es **`descripcion`**, no `nombre`.

| Prueba | Método | URL | Body | HTTP esperado |
|---|---|---|---|---|
| Consultar nivel 1 | GET | `/api/niveles/1` | — | `200` · `{"nivel":1,"descripcion":"Acceso general"}` |
| Nivel inexistente | GET | `/api/niveles/99` | — | `404` |
| WSKey inválida | GET | `/api/niveles/1` wskey=MALA | — | `401` |
| Crear | POST | `/api/niveles` | `{"nivel":99,"descripcion":"Nivel Test"}` | `201` |
| Duplicado | POST | `/api/niveles` | mismo body | `409` |
| Modificar | PUT | `/api/niveles/99` | `{"descripcion":"Nivel Modificado"}` | `200` |
| Borrar | DELETE | `/api/niveles/99` | — | `200` |

---

### 2.3 Dispositivos — `http://localhost:8087/api/dispositivos`

> Los campos del body son `codigodispositivo`, **`descripcion`** y `codigosala`.

| Prueba | Método | URL | Body | HTTP esperado |
|---|---|---|---|---|
| Consultar D001 | GET | `/api/dispositivos/D001` | — | `200` · datos del dispositivo |
| Inexistente | GET | `/api/dispositivos/D999` | — | `404` |
| WSKey inválida | GET | `/api/dispositivos/D001` wskey=MALA | — | `401` |
| Crear | POST | `/api/dispositivos` | `{"codigodispositivo":"D099","descripcion":"Disp Test","codigosala":"S001"}` | `201` |
| Duplicado | POST | `/api/dispositivos` | mismo body | `409` |
| Modificar | PUT | `/api/dispositivos/D099` | `{"descripcion":"Disp Modificado","codigosala":"S002"}` | `200` |
| Borrar | DELETE | `/api/dispositivos/D099` | — | `200` |

---

### 2.4 Notificaciones — `http://localhost:8088/api/notificaciones`

> Mailpit debe estar corriendo. Ver emails en: **http://localhost:8025**

#### POST /usuario-valido/{nif} — empleado válido
- Método: `POST` · URL: `.../api/notificaciones/usuario-valido/33900165M`
- Body: ninguno

**Esperado:** HTTP `200` · `{"mensaje":"Notificación enviada a juan.garcia@empresa.com"}`

Abre `http://localhost:8025` → verás el email con asunto `Validación de empleado`.

#### POST /usuario-valido/{nif} — NIF inexistente
- URL: `.../api/notificaciones/usuario-valido/00000000A`

**Esperado:** HTTP `404` · `No existe ningún empleado con NIF 00000000A`

#### POST /error — error con empleado válido
- Método: `POST` · URL: `.../api/notificaciones/error`
- Header: `Content-Type: application/json`
- Body:
```json
{
  "nif": "33900165M",
  "error": "Se ha producido un fallo en el sistema de accesos"
}
```
**Esperado:** HTTP `200` · `{"mensaje":"Notificación de error enviada a juan.garcia@empresa.com"}`

#### POST /error — body vacío
```json
{}
```
**Esperado:** HTTP `400` · `Los campos 'nif' y 'error' son obligatorios.`

#### POST /presencia-sala
Primero registra una presencia desde SoapUI (operación `registrar` de ControlPresencia con NIF=`33900165M` y sala=`S001`). Luego:

- Método: `POST` · URL: `.../api/notificaciones/presencia-sala`
- Body: ninguno

**Esperado:** HTTP `200` · `{"mensaje":"Notificaciones enviadas: 1 email(s)."}`

---

## PARTE 3 — Ver emails con Mailpit

1. Abre el navegador en **http://localhost:8025**
2. Verás la bandeja de entrada con todos los emails de prueba
3. Haz clic en un email para ver asunto, destinatario y cuerpo
4. Comprueba que el destinatario es el email del empleado consultado (`juan.garcia@empresa.com`)

---

## Checklist final

| Servicio | Pruebas clave | Herramienta |
|---|---|---|
| SOAP Validaciones :8081 | NIF/NIE/NAF/IBAN válido e inválido + WSKey | SoapUI |
| SOAP Empleados :8082 | consultar / nuevo / duplicado / borrar | SoapUI |
| SOAP ControlAccesos :8083 | registrar / consultar con fechas | SoapUI |
| SOAP ControlPresencia :8084 | registrar / duplicado / controlEmpleadosSala / eliminar | SoapUI |
| REST Salas :8085 | GET/POST/PUT/DELETE + 404 + 409 + 401 | Postman |
| REST Niveles :8086 | GET/POST/PUT/DELETE + 404 + 409 + 401 | Postman |
| REST Dispositivos :8087 | GET/POST/PUT/DELETE + 404 + 409 + 401 | Postman |
| REST Notificaciones :8088 | usuario-valido / error / presencia-sala + 401/404/400 | Postman + Mailpit |
