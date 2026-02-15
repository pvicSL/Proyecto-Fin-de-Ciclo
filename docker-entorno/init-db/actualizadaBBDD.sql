-- =====================================================
-- TatuSys - Sistema de Gestión de Estudio de Tatuajes
-- Script de creación de base de datos organizado por dependencias
-- ACTUALIZADO: Incluye tabla password_reset_tokens
-- =====================================================

CREATE DATABASE IF NOT EXISTS estudio_tatuajes;
USE estudio_tatuajes;

-- =====================================================
-- NIVEL 0: Tablas independientes (sin FK)
-- =====================================================

-- Tabla de clientes (independiente)
CREATE TABLE clientes (
    id_cliente INTEGER PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    apellido1 VARCHAR(30) NOT NULL,
    apellido2 VARCHAR(30),
    email VARCHAR(50) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    documento_identificacion VARCHAR(30),
    UNIQUE INDEX ID_UNIQUE_CLIENTE (nombre, apellido1, telefono)
);

-- Índice adicional para búsquedas por documento (insensible a mayúsculas)
CREATE INDEX idx_documento_upper ON clientes ((UPPER(documento_identificacion)));

-- Tabla de trabajadores (independiente)
CREATE TABLE trabajadores (
    id_trabajador INT PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(15) UNIQUE NOT NULL,
    numero_cuenta VARCHAR(30),
    contrasenia VARCHAR(255) NOT NULL,
    nombre VARCHAR(30) NOT NULL,
    apellido1 VARCHAR(30) NOT NULL,
    apellido2 VARCHAR(30),
    email VARCHAR(50) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    rol ENUM('ADMIN', 'TRABAJADOR') NOT NULL,
    funciones ENUM('CREACION', 'ELIMINACION') NOT NULL
);

-- Tabla de precios adicionales (independiente)
CREATE TABLE precios_adicionales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    categoria ENUM('TIPO', 'ZONA', 'TAMANIO', 'DETALLE', 'COLORACION', 'ESTILO', 'BASE') NOT NULL,
    valor VARCHAR(50) NOT NULL,
    precio_adicional DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY unique_categoria_valor (categoria, valor)
);

-- =====================================================
-- NIVEL 1: Tablas que dependen de nivel 0
-- =====================================================

-- Tabla de tokens para reset de contraseña (depende de trabajadores)
CREATE TABLE password_reset_tokens (
    id_token INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    id_trabajador INT NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_trabajador) REFERENCES trabajadores(id_trabajador),
    INDEX idx_token (token),
    INDEX idx_trabajador (id_trabajador),
    INDEX idx_expiracion (fecha_expiracion)
);

-- Tabla de servicios (depende de clientes)
CREATE TABLE servicios (
    id_servicio INT PRIMARY KEY AUTO_INCREMENT,
    tipo ENUM('TATUAJE', 'ELIMINACION', 'COVER', 'RETOQUE') NOT NULL,
    zona ENUM('BRAZO', 'ANTEBRAZO', 'CODO', 'HOMBRO', 'TÓRAX', 'ABDOMEN', 'PUBIS', 'MUSLO', 'RODILLA',
        'PANTORILLA', 'PIE', 'MANO', 'CERVICAL', 'LUMBARES', 'NALGA', 'CABEZA') NOT NULL,
    tamanio ENUM('MINI', 'PEQUEÑO', 'MEDIANO', 'GRANDE', 'MUY_GRANDE') NOT NULL,
    detalle ENUM('SENCILLO', 'MEDIO', 'DENSO') NOT NULL,
    coloracion ENUM('COLOR', 'NEGRO') NOT NULL,
    estilo ENUM('ANIME', 'BLACKANDGREY', 'FINELINE', 'LETERING', 'JAPONES', 
        'REALISMO', 'TRADICIONAL') NOT NULL,
    fecha DATE,
    hora TIME,
    comentarios VARCHAR(200),
    duracion_minutos INT,
    imagen_ref_1 VARCHAR(255) DEFAULT NULL,
    imagen_ref_2 VARCHAR(255) DEFAULT NULL,
    imagen_ref_3 VARCHAR(255) DEFAULT NULL,
    referencia VARCHAR(12) UNIQUE DEFAULT NULL,
    id_cliente INTEGER NOT NULL,
    id_trabajador INT DEFAULT NULL,
    factura BOOLEAN NOT NULL,
    estado_factura ENUM('NO_REQUIERE', 'PENDIENTE', 'ENVIADA') NULL,
    estatus ENUM('PENDIENTE', 'CONFIRMADO', 'FINALIZADO'),
    fecha_limite_pago DATETIME,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_trabajador) REFERENCES trabajadores(id_trabajador),
    UNIQUE INDEX ID_UNIQUE_SERVICIO (id_cliente, id_servicio)
);

-- =====================================================
-- NIVEL 2: Tablas que dependen de nivel 1
-- =====================================================

-- Tabla de presupuestos (depende de servicios)
CREATE TABLE presupuestos (
    id_presupuesto INT PRIMARY KEY AUTO_INCREMENT,
    id_servicio INT NOT NULL,
    precio_sin_iva DECIMAL(8,2) NOT NULL,
    iva DECIMAL(8,2) NOT NULL,
    precio_final DECIMAL(8,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vigente BOOLEAN NOT NULL,
    estado ENUM('PENDIENTE', 'GENERADO', 'ACEPTADO', 'RECHAZADO', 'FINALIZADO') NOT NULL,
    comentarios VARCHAR(200),
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
);

-- =====================================================
-- Script completado - Base de datos lista para usar
-- =====================================================

-- COMENTARIOS SOBRE NUEVAS FUNCIONALIDADES:
-- 1. password_reset_tokens: Maneja tokens de recuperación de contraseña
-- 2. Tokens expiran automáticamente después de 30 minutos
-- 3. Se incluyen índices para optimizar búsquedas por token y trabajador
-- 4. Campo 'utilizado' previene reutilización de tokens