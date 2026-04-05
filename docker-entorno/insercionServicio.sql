USE estudio_tatuajes;

-- Servicios de prueba para TatuSys
INSERT INTO servicios (tipo, zona, tamanio, detalle, coloracion, estilo, fecha, hora, comentarios, duracion_minutos, imagen_ref_1, imagen_ref_2, imagen_ref_3, referencia, id_cliente, factura, estatus) VALUES
	('TATUAJE', 'BRAZO', 'MEDIANO', 'MEDIO', 'COLOR', 'REALISMO', '2024-12-15', '10:30:00', 'Cliente quiere un diseño de león realista', 120, 'leon_ref1.jpg', 'leon_ref2.jpg', NULL, NULL, 1, true, 'CONFIRMADO'),

	('COVER', 'HOMBRO', 'GRANDE', 'DENSO', 'COLOR', 'TRADICIONAL', '2024-11-20', '14:00:00', 'Cubrir tatuaje antiguo con rosa tradicional', 180, 'cover_rosa1.jpg', NULL, NULL, NULL, 1, false, 'PENDIENTE'),

	('TATUAJE', 'ANTEBRAZO', 'PEQUEÑO', 'SENCILLO', 'NEGRO', 'FINELINE', '2025-01-10', '11:00:00', 'Diseño minimalista geométrico', 90, NULL, NULL, NULL, NULL, 2, true, 'CONFIRMADO'),

	('ELIMINACION', 'MANO', 'MINI', 'SENCILLO', 'NEGRO', 'TRADICIONAL', '2024-10-05', '16:30:00', 'Eliminar tatuaje pequeño del dedo', 45, NULL, NULL, NULL, NULL, 3, true, 'CONFIRMADO'),

	('TATUAJE', 'MUSLO', 'MUY_GRANDE', 'DENSO', 'COLOR', 'JAPONES', '2025-02-14', '09:00:00', 'Dragón japonés completo, sesión larga', 300, 'dragon_ref1.jpg', 'dragon_ref2.jpg', 'dragon_ref3.jpg', NULL, 4, false, 'PENDIENTE'),

	('RETOQUE', 'TÓRAX', 'MEDIANO', 'MEDIO', 'COLOR', 'REALISMO', NULL, NULL, 'Retoque de colores en tatuaje existente', NULL, NULL, NULL, NULL, NULL, 5, true, 'PENDIENTE'),

	('TATUAJE', 'PIE', 'MINI', 'SENCILLO', 'NEGRO', 'LETERING', '2024-12-28', '17:00:00', 'Frase corta en el pie', 60, 'lettering_pie.jpg', NULL, NULL, NULL, 6, false, 'CONFIRMADO'),

	('TATUAJE', 'CERVICAL', 'PEQUEÑO', 'MEDIO', 'NEGRO', 'ANIME', '2025-01-20', '12:30:00', NULL, 75, 'anime_cuello.jpg', NULL, NULL, NULL, 7, true, 'PENDIENTE'),

	('COVER', 'PANTORILLA', 'GRANDE', 'DENSO', 'COLOR', 'BLACKANDGREY', '2024-09-15', '13:45:00', 'Cover up con motivos tribales', 200, NULL, NULL, NULL, NULL, 8, true, 'CONFIRMADO'),

	('TATUAJE', 'ABDOMEN', 'MEDIANO', 'SENCILLO', 'COLOR', 'TRADICIONAL', '2025-03-05', '15:15:00', 'Diseño floral en abdomen lateral', 135, 'floral_abd1.jpg', 'floral_abd2.jpg', NULL, NULL, 9, false, 'PENDIENTE'),

	('ELIMINACION', 'RODILLA', 'PEQUEÑO', 'MEDIO', 'NEGRO', 'TRADICIONAL', '2024-11-30', '10:00:00', 'Eliminación láser zona rodilla', 30, NULL, NULL, NULL, NULL, 10, true, 'CONFIRMADO'),

	('TATUAJE', 'CODO', 'MINI', 'SENCILLO', 'NEGRO', 'FINELINE', NULL, NULL, 'Diseño geométrico en codo', NULL, NULL, NULL, NULL, NULL, 2, false, 'PENDIENTE'),

	('TATUAJE', 'NALGA', 'GRANDE', 'DENSO', 'COLOR', 'REALISMO', '2025-01-25', '11:30:00', 'Retrato realista en color', 240, 'retrato_ref1.jpg', NULL, NULL, NULL, 3, true, 'PENDIENTE'),

	('RETOQUE', 'BRAZO', 'PEQUEÑO', 'SENCILLO', 'COLOR', 'JAPONES', '2024-12-10', '16:00:00', NULL, 45, NULL, NULL, NULL, NULL, 4, false, 'CONFIRMADO'),

	('TATUAJE', 'CABEZA', 'PEQUEÑO', 'MEDIO', 'NEGRO', 'LETERING', '2025-02-20', '14:30:00', 'Lettering detrás de la oreja', 90, 'head_lettering.jpg', NULL, NULL, NULL, 5, true, 'PENDIENTE'),

	('COVER', 'LUMBARES', 'MUY_GRANDE', 'DENSO', 'COLOR', 'TRADICIONAL', '2025-04-10', '09:30:00', 'Cover completo zona lumbar con flores', 360, 'lumbar_cover1.jpg', 'lumbar_cover2.jpg', 'lumbar_cover3.jpg', NULL, 6, false, 'PENDIENTE'),

	('TATUAJE', 'PUBIS', 'MINI', 'SENCILLO', 'NEGRO', 'FINELINE', '2024-08-22', '18:00:00', 'Diseño íntimo minimalista', 30, NULL, NULL, NULL, NULL, 7, true, 'CONFIRMADO'),

	('ELIMINACION', 'ANTEBRAZO', 'MEDIANO', 'MEDIO', 'COLOR', 'ANIME', NULL, NULL, 'Eliminación tatuaje anime antiguo', NULL, NULL, NULL, NULL, NULL, 8, true, 'PENDIENTE'),

	('TATUAJE', 'HOMBRO', 'GRANDE', 'MEDIO', 'COLOR', 'BLACKANDGREY', '2025-01-15', '13:00:00', 'Mandala detallado en hombro', 180, 'mandala_ref1.jpg', 'mandala_ref2.jpg', NULL, NULL, 9, false, 'CONFIRMADO'),

	('RETOQUE', 'PIE', 'MINI', 'SENCILLO', 'NEGRO', 'TRADICIONAL', '2024-12-05', '17:30:00', 'Retoque líneas del pie', 20, NULL, NULL, NULL, NULL, 10, true, 'CONFIRMADO');