USE estudio_tatuajes;

-- Trabajadores de prueba para TatuSys
INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones) VALUES
	('11223344A', 'ES9121000418450200051332', 'admin123', 'Roberto', 'Fernández', 'Silva', 'Roberto.Fernandez@tatusys.com', '654789123', 'ADMIN', 'CREACION'),
	('55667788B', 'ES6001820200123456789012', 'trabajador123', 'Carmen', 'López', 'Martín', 'carmen.lopez@tatusys.com', '687456123', 'TRABAJADOR', 'CREACION'),
	('99887766C', 'ES1234567890123456789012', 'password123', 'Antonio', 'Morales', NULL, 'ANTONIO.MORALES@tatusys.com', '612345678', 'TRABAJADOR', 'ELIMINACION'),
	('44556677D', 'ES9876543210987654321098', 'worker456', 'Beatriz', 'Ruiz', 'González', 'beatriz.ruiz@tatusys.com', '698123456', 'TRABAJADOR', 'CREACION'),
	('33445566E', NULL, 'temp789', 'Fernando', 'Jiménez', 'Torres', 'fernando.jimenez@tatusys.com', '645987321', 'TRABAJADOR', 'ELIMINACION');

-- Trabajadores con contraseña hasheada (BCrypt rounds=10)
-- Contraseñas: Isabel->Tinta2024! | Marcos->Estudio#99 | Nuria->Aguja&2025 | Raúl->Pigmento7!

INSERT INTO trabajadores (dni, numero_cuenta, contrasenia, nombre, apellido1, apellido2, email, telefono, rol, funciones) VALUES
	('12345678F', 'ES7620770024003102575766', '$2b$10$AvO34n8baElPoIZAgJ2QkOP8PsfgQ41ACW5n5uem7qlQdacCwCIwK', 'Isabel', 'Navarro', 'Blanco', 'isabel.navarro@tatusys.com', '611223344', 'TRABAJADOR', 'CREACION'),
	('87654321G', NULL, '$2b$10$x9R7vkK17LmEkmzR6kVMfunvA3WXIH6iQNo6izRArcOfgrDU5kQDG', 'Marcos', 'Alonso', NULL, 'marcos.alonso@tatusys.com', '622334455', 'TRABAJADOR', 'ELIMINACION'),
	('56473829H', 'ES2531902332164565949596', '$2b$10$EWKD/pVIJ7jMDTcFTEHEOeXSesZP3m8vsjFU1r9CP7Eu0XBlfRfe2', 'Nuria', 'Castillo', 'Vidal', 'nuria.castillo@tatusys.com', '633445566', 'TRABAJADOR', 'CREACION'),
	('19283746I', 'ES8200491500042310126611', '$2b$10$b6kivVOK34/P5QAK.Kibn.99FbttpisIpf8q16MyzcV9V/6NnmDIO', 'Raúl', 'Pedraza', 'Fuentes', 'raul.pedraza@tatusys.com', '644556677', 'ADMIN', 'CREACION');

