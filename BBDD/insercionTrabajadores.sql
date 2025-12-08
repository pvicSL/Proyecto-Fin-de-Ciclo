-- Usuario ADMIN
INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones) 
VALUES ('12345678A', 'ES2100491500051234567892', 'admin123', 'Admin', 'Sistema', NULL, 'admin@tatuajes.com', '666123456', 'ADMIN', 'CREACION');

-- Trabajador 1 - Juan (Función: Creación)
INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones) 
VALUES ('87654321B', 'ES6000491500022345678903', 'trabajador123', 'Juan', 'Pérez', 'García', 'juan@tatuajes.com', '666789123', 'TRABAJADOR', 'CREACION');

-- Trabajador 2 - María (Función: Eliminación)
INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones)
VALUES ('11223344C', 'ES9121000418450200051332', 'trabajador123', 'María', 'López', 'Martín', 'maria@tatuajes.com', '666456789', 'TRABAJADOR', 'ELIMINACION');

-- Trabajador 3 - Carlos (Función: Creación, sin segundo apellido)
INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones)
VALUES ('55667788D', 'ES4114910001234567890193', 'trabajador123', 'Carlos', 'Ruiz', NULL, 'carlos@tatuajes.com', '666321654', 'TRABAJADOR', 'CREACION');

-- Trabajador 4 - Ana (Función: Eliminación, sin número de cuenta por ahora)
INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones)
VALUES ('99887766E', NULL, 'trabajador123', 'Ana', 'Fernández', 'Sánchez', 'ana@tatuajes.com', '666987654', 'TRABAJADOR', 'ELIMINACION');
