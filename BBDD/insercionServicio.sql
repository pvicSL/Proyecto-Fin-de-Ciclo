I-- Insertar citas variadas para testing completo
INSERT INTO servicios (tipo, zona, tamanio, detalle, coloracion, estilo, fecha, hora, comentarios, id_cliente, factura, estatus) 
VALUES 

-- CITA 1: Tatuaje básico económico (precio base + pocos extras)
('TATUAJE', 'BRAZO', 'MINI', 'SENCILLO', 'NEGRO', 'LETERING', 
 '2024-12-15', '10:00:00', 'Nombre en el brazo', 1, true, 'PENDIENTE'),

-- CITA 2: Tatuaje premium caro (muchos extras)
('TATUAJE', 'TÓRAX', 'GRANDE', 'DENSO', 'COLOR', 'REALISMO', 
 '2024-12-16', '14:00:00', 'Retrato realista en pecho', 2, true, 'CONFIRMADO'),

-- CITA 3: Cover-up mediano
('COVER', 'HOMBRO', 'MEDIANO', 'MEDIO', 'COLOR', 'JAPONES', 
 '2024-12-17', '11:30:00', 'Cubrir tatuaje antiguo', 3, false, 'PENDIENTE'),

-- CITA 4: Eliminación láser
('ELIMINACION', 'ANTEBRAZO', 'PEQUEÑO', 'SENCILLO', 'NEGRO', 'TRADICIONAL', 
 '2024-12-18', '16:00:00', 'Sesión eliminación láser', 1, true, 'CONFIRMADO'),

-- CITA 5: Retoque económico (precio negativo + extras)
('RETOQUE', 'MANO', 'MINI', 'SENCILLO', 'NEGRO', 'FINELINE', 
 '2024-12-19', '09:15:00', 'Retoque líneas desgastadas', 4, false, 'PENDIENTE'),

-- CITA 6: Tatuaje muy grande y complejo (máximo precio)
('TATUAJE', 'CERVICAL', 'MUY_GRANDE', 'DENSO', 'COLOR', 'JAPONES', 
 '2024-12-20', '13:00:00', 'Manga completa estilo japonés', 2, true, 'PENDIENTE'),

-- CITA 7: Tatuaje zona sensible
('TATUAJE', 'PUBIS', 'PEQUEÑO', 'MEDIO', 'NEGRO', 'BLACKANDGREY', 
 '2024-12-21', '15:30:00', 'Diseño íntimo discreto', 3, false, 'CONFIRMADO'),

-- CITA 8: Cover en zona complicada
('COVER', 'RODILLA', 'MEDIANO', 'DENSO', 'COLOR', 'ANIME', 
 '2024-12-22', '12:00:00', 'Cover en zona articular', 4, true, 'PENDIENTE');