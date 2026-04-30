-- ============================================================
-- Edificio Inteligente - Schema MySQL
-- Práctica 1 MTIS - Interoperabilidad SW
-- ============================================================

CREATE DATABASE IF NOT EXISTS edificio_inteligente
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE edificio_inteligente;

-- ------------------------------------------------------------
-- Tabla: niveles
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS niveles (
    nivel       INT          NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    PRIMARY KEY (nivel)
);

-- ------------------------------------------------------------
-- Tabla: empleados
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS empleados (
    nif            VARCHAR(9)   NOT NULL,
    nombre         VARCHAR(50)  NOT NULL,
    apellidos      VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL,
    naf            VARCHAR(12)  NOT NULL,
    iban           VARCHAR(24)  NOT NULL,
    tipodocumento  VARCHAR(3)   NOT NULL COMMENT 'NIF o NIE',
    PRIMARY KEY (nif)
);

-- ------------------------------------------------------------
-- Tabla: salas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS salas (
    codigosala VARCHAR(10)  NOT NULL,
    nombre     VARCHAR(100) NOT NULL,
    nivel      INT          NOT NULL,
    PRIMARY KEY (codigosala),
    CONSTRAINT fk_salas_nivel FOREIGN KEY (nivel) REFERENCES niveles(nivel)
);

-- ------------------------------------------------------------
-- Tabla: dispositivos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dispositivos (
    codigodispositivo VARCHAR(10)  NOT NULL,
    descripcion       VARCHAR(100) NOT NULL,
    codigosala        VARCHAR(10)  NOT NULL,
    PRIMARY KEY (codigodispositivo),
    CONSTRAINT fk_disp_sala FOREIGN KEY (codigosala) REFERENCES salas(codigosala)
);

-- ------------------------------------------------------------
-- Tabla: controlaccesos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS controlaccesos (
    id                INT          NOT NULL AUTO_INCREMENT,
    nif               VARCHAR(9)   NOT NULL,
    codigosala        VARCHAR(10)  NOT NULL,
    codigodispositivo VARCHAR(10)  NOT NULL,
    fechahora         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ca_empleado    FOREIGN KEY (nif)               REFERENCES empleados(nif),
    CONSTRAINT fk_ca_sala        FOREIGN KEY (codigosala)        REFERENCES salas(codigosala),
    CONSTRAINT fk_ca_dispositivo FOREIGN KEY (codigodispositivo) REFERENCES dispositivos(codigodispositivo)
);

-- ------------------------------------------------------------
-- Tabla: controlpresencia
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS controlpresencia (
    nif        VARCHAR(9)  NOT NULL,
    codigosala VARCHAR(10) NOT NULL,
    PRIMARY KEY (nif, codigosala),
    CONSTRAINT fk_cp_empleado FOREIGN KEY (nif)        REFERENCES empleados(nif),
    CONSTRAINT fk_cp_sala     FOREIGN KEY (codigosala) REFERENCES salas(codigosala)
);

-- ------------------------------------------------------------
-- Tabla: wskeys  (para nota máxima: autenticación de servicios)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS wskeys (
    id    INT         NOT NULL AUTO_INCREMENT,
    clave VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_clave (clave)
);

-- ============================================================
-- Datos de prueba
-- ============================================================
INSERT INTO niveles (nivel, descripcion) VALUES
    (1, 'Acceso general'),
    (2, 'Acceso restringido'),
    (3, 'Alta seguridad');

INSERT INTO salas (codigosala, nombre, nivel) VALUES
    ('S001', 'Recepción',         1),
    ('S002', 'Sala de reuniones', 2),
    ('S003', 'CPD',               3);

INSERT INTO dispositivos (codigodispositivo, descripcion, codigosala) VALUES
    ('D001', 'Lector entrada recepción',   'S001'),
    ('D002', 'Lector sala de reuniones',   'S002'),
    ('D003', 'Lector CPD',                 'S003');

INSERT INTO empleados (nif, nombre, apellidos, email, naf, iban, tipodocumento) VALUES
    ('33900165M', 'Juan',   'García López',   'juan.garcia@empresa.com',   '527575965274', 'ES0690000001210123456789', 'NIF'),
    ('12345678Z', 'María',  'Pérez Ruiz',     'maria.perez@empresa.com',   '280012345601', 'ES9121000418450200051332', 'NIF'),
    ('X1234567L', 'Pierre', 'Dupont Renault', 'pierre.dupont@empresa.com', '280087654321', 'ES8023100001180000012345', 'NIE');

INSERT INTO wskeys (clave) VALUES
    ('PRACTICA1_KEY_2024'),
    ('ADMIN_KEY_SECRET');
