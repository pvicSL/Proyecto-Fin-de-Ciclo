-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: estudio_tatuajes
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `apellido1` varchar(30) NOT NULL,
  `apellido2` varchar(30) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `telefono` varchar(30) NOT NULL,
  `documento_identificacion` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `ID_UNIQUE_CLIENTE` (`nombre`,`apellido1`,`telefono`),
  KEY `idx_documento_upper` ((upper(`documento_identificacion`)))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'Ana','García','López','ana.garcia@email.com','612345678','12345678A'),(2,'Carlos','Martínez',NULL,'carlos.martinez@gmail.com','654987321','87654321B'),(3,'María','Rodríguez','Fernández','maria.rodriguez@hotmail.com','698765432',NULL),(4,'Juan','Sánchez',NULL,'juan.sanchez@yahoo.com','611223344',NULL),(5,'Patricia','CorrecciónDatos','VeamosSiHayCambios','pvsanzlopez@gmail.com','600600600',''),(6,'Prueba','ConFactura','Deberiaexplotar','pruebafactura@gmail.com','600600600','77777777L'),(7,'PruebaCalendario','29dic','','pruebacalendario@gmail.com','600600600',''),(8,'PruebaIMGyLOCALIZADOR','CincoEnero26','SubidaIMG','pruebaimgylocaliz@gmail.com','600600600',''),(9,'PruebaMargenSolicitud','CincoEnero26','','pruebamargensoli@gmail.com','601601601','');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `precios_adicionales`
--

DROP TABLE IF EXISTS `precios_adicionales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios_adicionales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `categoria` enum('TIPO','ZONA','TAMANIO','DETALLE','COLORACION','ESTILO','BASE') NOT NULL,
  `valor` varchar(50) NOT NULL,
  `precio_adicional` decimal(10,2) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_categoria_valor` (`categoria`,`valor`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `precios_adicionales`
--

LOCK TABLES `precios_adicionales` WRITE;
/*!40000 ALTER TABLE `precios_adicionales` DISABLE KEYS */;
INSERT INTO `precios_adicionales` VALUES (1,'TIPO','TATUAJE',0.00,1),(2,'TIPO','ELIMINACION',80.00,1),(3,'TIPO','COVER',25.00,1),(4,'TIPO','RETOQUE',15.00,1),(5,'ZONA','BRAZO',0.00,1),(6,'ZONA','ANTEBRAZO',0.00,1),(7,'ZONA','CODO',15.00,1),(8,'ZONA','HOMBRO',5.00,1),(9,'ZONA','TÓRAX',10.00,1),(10,'ZONA','ABDOMEN',10.00,1),(11,'ZONA','PUBIS',20.00,1),(12,'ZONA','MUSLO',5.00,1),(13,'ZONA','RODILLA',15.00,1),(14,'ZONA','PANTORILLA',0.00,1),(15,'ZONA','PIE',20.00,1),(16,'ZONA','MANO',25.00,1),(17,'ZONA','CERVIAL',15.00,1),(18,'ZONA','LUMBARES',10.00,1),(19,'ZONA','NALGA',10.00,1),(20,'ZONA','CABEZA',30.00,1),(21,'TAMANIO','MINI',0.00,1),(22,'TAMANIO','PEQUEÑO',15.00,1),(23,'TAMANIO','MEDIANO',35.00,1),(24,'TAMANIO','GRANDE',60.00,1),(25,'TAMANIO','MUY_GRANDE',100.00,1),(26,'DETALLE','SENCILLO',0.00,1),(27,'DETALLE','MEDIO',20.00,1),(28,'DETALLE','DENSO',40.00,1),(29,'COLORACION','NEGRO',0.00,1),(30,'COLORACION','COLOR',25.00,1),(31,'ESTILO','ANIME',15.00,1),(32,'ESTILO','BLACKANDGREY',0.00,1),(33,'ESTILO','FINELINE',20.00,1),(34,'ESTILO','LETERING',10.00,1),(35,'ESTILO','JAPONES',25.00,1),(36,'ESTILO','REALISMO',35.00,1),(37,'ESTILO','TRADICIONAL',5.00,1);
/*!40000 ALTER TABLE `precios_adicionales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `presupuestos`
--

DROP TABLE IF EXISTS `presupuestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `presupuestos` (
  `id_presupuesto` int NOT NULL AUTO_INCREMENT,
  `id_servicio` int NOT NULL,
  `precio_base` decimal(8,2) NOT NULL,
  `iva` decimal(8,2) NOT NULL,
  `precio_final` decimal(8,2) NOT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `vigente` tinyint(1) NOT NULL,
  `estado` enum('PENDIENTE','ACEPTADO','RECHAZADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `comentarios` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_presupuesto`),
  KEY `idx_presupuesto_servicio` (`id_servicio`),
  CONSTRAINT `presupuestos_ibfk_1` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id_servicio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presupuestos`
--

LOCK TABLES `presupuestos` WRITE;
/*!40000 ALTER TABLE `presupuestos` DISABLE KEYS */;
/*!40000 ALTER TABLE `presupuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `id_servicio` int NOT NULL AUTO_INCREMENT,
  `tipo` enum('TATUAJE','ELIMINACION','COVER','RETOQUE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `zona` enum('BRAZO','ANTEBRAZO','CODO','HOMBRO','TÓRAX','ABDOMEN','PUBIS','MUSLO','RODILLA','PANTORRILLA','PIE','MANO','CERVICAL','LUMBARES','NALGA','CABEZA') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tamanio` enum('MINI','PEQUEÑO','MEDIANO','GRANDE','MUY_GRANDE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `detalle` enum('SENCILLO','MEDIO','DENSO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `coloracion` enum('COLOR','NEGRO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `estilo` enum('ANIME','BLACKANDGREY','FINELINE','LETERING','JAPONES','REALISMO','TRADICIONAL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha` date DEFAULT NULL,
  `hora` time DEFAULT NULL,
  `comentarios` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_cliente` int NOT NULL,
  `factura` tinyint(1) NOT NULL,
  `estatus` enum('PENDIENTE','CONFIRMADO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duracion_minutos` int DEFAULT '60',
  `imagen_ref_1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imagen_ref_2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imagen_ref_3` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `localizador` varchar(12) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_servicio`),
  UNIQUE KEY `ID_UNIQUE_SERVICIO` (`id_cliente`,`id_servicio`),
  UNIQUE KEY `localizador` (`localizador`),
  CONSTRAINT `servicios_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios`
--

LOCK TABLES `servicios` WRITE;
/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
INSERT INTO `servicios` VALUES (1,'TATUAJE','BRAZO','MINI','SENCILLO','NEGRO','LETERING','2024-12-15','10:00:00','Nombre en el brazo',1,1,'PENDIENTE',60,NULL,NULL,NULL,'5869F814'),(2,'TATUAJE','TÓRAX','GRANDE','DENSO','COLOR','REALISMO','2024-12-16','14:00:00','Retrato realista en pecho',2,1,'CONFIRMADO',60,NULL,NULL,NULL,'586B73C2'),(3,'COVER','HOMBRO','MEDIANO','MEDIO','COLOR','JAPONES','2024-12-17','11:30:00','Cubrir tatuaje antiguo',3,0,'PENDIENTE',60,NULL,NULL,NULL,'586B8FC0'),(4,'ELIMINACION','ANTEBRAZO','PEQUEÑO','SENCILLO','NEGRO','TRADICIONAL','2026-01-02','12:30:00',NULL,1,1,NULL,240,NULL,NULL,NULL,'586B9D70'),(5,'RETOQUE','MANO','MINI','SENCILLO','NEGRO','FINELINE','2026-01-02','11:00:00','Retoque líneas desgastadas',4,0,'PENDIENTE',60,NULL,NULL,NULL,'586BA01C'),(6,'TATUAJE','CERVICAL','MUY_GRANDE','DENSO','COLOR','JAPONES','2024-12-20','13:00:00','Manga completa estilo japonés',2,1,'PENDIENTE',60,NULL,NULL,NULL,'586BA0F4'),(7,'TATUAJE','PUBIS','PEQUEÑO','MEDIO','NEGRO','BLACKANDGREY','2024-12-21','15:30:00','Diseño íntimo discreto',3,0,'CONFIRMADO',60,NULL,NULL,NULL,'586BA18F'),(8,'COVER','RODILLA','MEDIANO','DENSO','COLOR','ANIME','2024-12-22','12:00:00','Cover en zona articular',4,1,'PENDIENTE',60,NULL,NULL,NULL,'586BA220'),(11,'TATUAJE','RODILLA','MINI','SENCILLO','COLOR','TRADICIONAL','2026-12-21','18:00:00','Nada que comentar.',5,0,'PENDIENTE',270,NULL,NULL,NULL,'586BA40F'),(12,'ELIMINACION','BRAZO','MINI','SENCILLO','NEGRO','REALISMO','2026-12-21','10:00:00',NULL,6,1,'PENDIENTE',240,NULL,NULL,NULL,'586BA4FF'),(13,'RETOQUE','CODO','MINI','SENCILLO','NEGRO','ANIME','2025-12-31','10:00:00','Test: ver si el selector de fechas real funciona. Cita el 31 a las 10, debería bloquear ese tiempo en otra solicitud.',7,0,'PENDIENTE',240,NULL,NULL,NULL,'586BA752'),(14,'TATUAJE','ANTEBRAZO','PEQUEÑO','MEDIO','COLOR','TRADICIONAL','2026-01-22','16:30:00','',8,0,'PENDIENTE',270,'38d18183-e664-429e-bd7f-c999f78ef391.jpg','5b45aeb9-96f8-4086-8827-09d68e29f605.jpg',NULL,'ABA9F1C9'),(15,'COVER','MANO','MEDIANO','DENSO','NEGRO','BLACKANDGREY','2026-01-08','11:00:00','Probamos si se ofrecen citas con un margen de 3 días respecto a la petición, para permitir que haya plazo de pagar la fianza.',9,0,'PENDIENTE',270,NULL,NULL,NULL,'8BF24502');
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trabajadores`
--

DROP TABLE IF EXISTS `trabajadores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trabajadores` (
  `id_trabajador` int NOT NULL AUTO_INCREMENT,
  `dni` varchar(15) NOT NULL,
  `numero_cuenta` varchar(30) DEFAULT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `nombre` varchar(30) NOT NULL,
  `apellido1` varchar(30) NOT NULL,
  `apellido2` varchar(30) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `telefono` varchar(30) NOT NULL,
  `rol` enum('ADMIN','TRABAJADOR') NOT NULL,
  `funciones` enum('CREACION','ELIMINACION') NOT NULL,
  PRIMARY KEY (`id_trabajador`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trabajadores`
--

LOCK TABLES `trabajadores` WRITE;
/*!40000 ALTER TABLE `trabajadores` DISABLE KEYS */;
INSERT INTO `trabajadores` VALUES (1,'12345678A','ES2100491500051234567892','admin123','Admin','Sistema',NULL,'admin@tatuajes.com','666123456','ADMIN','CREACION'),(2,'87654321B','ES6000491500022345678903','trabajador123','Juan','PÃ©rez','GarcÃ­a','juan@tatuajes.com','666789123','TRABAJADOR','CREACION'),(3,'11223344C','ES9121000418450200051332','trabajador123','MarÃ­a','LÃ³pez','MartÃ­n','maria@tatuajes.com','666456789','TRABAJADOR','ELIMINACION'),(4,'55667788D','ES4114910001234567890193','trabajador123','Carlos','Ruiz',NULL,'carlos@tatuajes.com','666321654','TRABAJADOR','CREACION'),(5,'99887766E',NULL,'trabajador123','Ana','FernÃ¡ndez','SÃ¡nchez','ana@tatuajes.com','666987654','TRABAJADOR','ELIMINACION');
/*!40000 ALTER TABLE `trabajadores` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-05 17:24:14
