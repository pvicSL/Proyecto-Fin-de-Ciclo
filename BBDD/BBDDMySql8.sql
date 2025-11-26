CREATE DATABASE estudio_tatuajes;
USE estudio_tatuajes;

CREATE TABLE IF NOT EXISTS clientes (
	id_cliente INTEGER PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR (30) NOT NULL,
	apellido1 VARCHAR (30) NOT NULL,
	apellido2 VARCHAR (30),
	email VARCHAR (50) NOT NULL,
	telefono VARCHAR (30) NOT NULL,
	documento_identificacion VARCHAR(30),
	UNIQUE INDEX ID_UNIQUE_CLIENTE (nombre, apellido1, telefono) /*Evita inserción de registros duplicados*/
);
+
CREATE INDEX idx_documento_upper ON clientes ((UPPER(documento_identificacion)));

CREATE TABLE IF NOT EXISTS trabajadores (
	id_trabajador INT PRIMARY KEY AUTO_INCREMENT,
	contrasenia VARCHAR (255) NOT NULL,
	nombre VARCHAR (30) NOT NULL,
	apellido1 VARCHAR (30) NOT NULL,
	apellido2 VARCHAR (30),
	email VARCHAR (50) NOT NULL,
	telefono VARCHAR (30) NOT NULL,
	rol ENUM ('ADMIN', 'TRABAJADOR') NOT NULL,	/*controla las opciones del rol*/
	funciones ENUM ('CREACION', 'ELIMINACION') NOT NULL
);

CREATE TABLE IF NOT EXISTS servicios(
	id_servicio INT PRIMARY KEY AUTO_INCREMENT,
	tipo ENUM ('TATUAJE', 'ELIMINACION', 'COVER', 'RETOQUE') NOT NULL,
	zona ENUM ('BRAZO', 'ANTEBRAZO', 'CODO', 'HOMBRO', 'TÓRAX', 'ABDOMEN', 'PUBIS', 'MUSLO', 'RODILLA',
		'PANTORILLA', 'PIE', 'MANO', 'CERVIAL', 'LUMBARES', 'NALGA', 'CABEZA') NOT NULL,
	tamanio ENUM ('MINI', 'PEQUEÑO', 'MEDIANO', 'GRANDE', 'MUY_GRANDE') NOT NULL,
	detalle ENUM ('SENCILLO', 'MEDIO', 'DENSO') NOT NULL,
	coloracion ENUM ('COLOR', 'NEGRO') NOT NULL,
	estilo ENUM ('ANIME', 'BLACKANDGREY', 'FINELINE', 'LETERING', 'JAPONES', 
		'REALISMO', 'TRADICIONAL') NOT NULL,
	fecha DATE, /*YYYY-YY-DD*/
	hora TIME, /*HH:MM:SS*/
	comentarios VARCHAR (200),
	id_cliente INTEGER NOT NULL,
	factura BOOLEAN NOT NULL,
	estatus ENUM ('PENDIENTE', 'CONFIRMADO'),
	FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente),
	UNIQUE INDEX ID_UNIQUE_SERVICIO (id_cliente, id_servicio) /*evita servicios duplicados*/
	/*imagen1 LONGBLOB NOT NULL,
	imagen2 LONGBLOB,*/

);

CREATE TABLE IF NOT EXISTS precios_adicionales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    categoria ENUM('TIPO', 'ZONA', 'TAMANIO', 'DETALLE', 'COLORACION', 'ESTILO'),
    valor VARCHAR(50),				/*Valor que le damos al enum seleccionado de las categorías*/
    precio_adicional DECIMAL(8,2),
    activo BOOLEAN DEFAULT TRUE		/*Si se pone a false, se desactiva el servicio pero no se elimina de la bbdd*/
);

CREATE TABLE IF NOT EXISTS presupuestos (
	id_presupuesto INT PRIMARY KEY AUTO_INCREMENT,
    id_servicio INT NOT NULL,
	precio_base DECIMAL(8,2) NOT NULL,
	iva DECIMAL(8,2) NOT NULL, 
	precio_final DECIMAL(8,2) NOT NULL,
	fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	vigente BOOLEAN NOT NULL,
	estado enum ('PENDIENTE', 'ACEPTADO', 'RECHAZADO') NOT NULL,
	comentarios VARCHAR (200),
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
);

CREATE INDEX idx_presupuesto_servicio ON presupuesto(id_servicio);	/*Indexación para mejorar rendimiento*/

/*TODO presupuestos: Trigger que automáticamente pone vigente=FALSE a los otros
-- cuando insertas uno nuevo con vigente=TRUE*/


/*Pruebas*/
drop table clientes;
drop table servicios;
drop table trabajadores;

 select * from clientes;

