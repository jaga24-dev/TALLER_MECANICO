-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: gateway01.us-east-1.prod.aws.tidbcloud.com    Database: test
-- ------------------------------------------------------
-- Server version	8.0.11-TiDB-v8.5.3-serverless

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
  `nombre_completo` varchar(150) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `correo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=60002;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'juan','612 2250889','juan@gmail.com'),(2,'carranzo de fiesta','612 333 3333','carranzo@gmail.com'),(3,'carlos alfonso','612 444 4444','carlos@gmail.com'),(4,'Santiago de Anda','6120000000','nuevo@gmail.com');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ordenes_servicio`
--

DROP TABLE IF EXISTS `ordenes_servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordenes_servicio` (
  `id_orden` int NOT NULL AUTO_INCREMENT,
  `id_vehiculo` int NOT NULL,
  `fecha_ingreso` date NOT NULL,
  `fecha_entrega_estimada` date DEFAULT NULL,
  `tipo_requerimiento` varchar(50) NOT NULL,
  `kilometraje` int NOT NULL,
  `nivel_combustible` varchar(20) DEFAULT NULL,
  `falla_reportada` text DEFAULT NULL,
  `estado` varchar(50) NOT NULL DEFAULT 'En espera',
  `costo_refacciones` decimal(10,2) DEFAULT '0.00',
  `costo_mano_obra` decimal(10,2) DEFAULT '0.00',
  `subtotal` decimal(10,2) DEFAULT '0.00',
  `impuesto` decimal(10,2) DEFAULT '0.00',
  `monto_total` decimal(10,2) DEFAULT '0.00',
  `imagen_diagnostico` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_orden`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_ordenes_vehiculos` (`id_vehiculo`),
  CONSTRAINT `fk_ordenes_vehiculos` FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculos` (`id_vehiculo`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=90001;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ordenes_servicio`
--

LOCK TABLES `ordenes_servicio` WRITE;
/*!40000 ALTER TABLE `ordenes_servicio` DISABLE KEYS */;
INSERT INTO `ordenes_servicio` VALUES (1,1,'2026-05-27',NULL,'Reparación',20000,'3/4','NuevoNuevoNuevoNuevoNuevoNuevoNuevoNuevoNuevo | Servicio: casi casi casi casicasi casi casi casicasi casi casi casicasi casi casi casicasi casi casi casicasi casi casi casicasi casi casi casicasi casi casi casicasi casi casi casi | Condición: Casi nuevo','EN ESPERA',0.00,200.00,0.00,0.00,220.00,'C:\\Users\\setri\\Taller-Mecanico-UABCS\\TALLER_MECANICO\\imagenes_ordenes\\1779909385606_Untitled Diagram.drawio.png'),(30001,60002,'2026-05-27',NULL,'Garantía',232,'F',' | Condición: rtd','EN ESPERA',0.00,0.00,0.00,0.00,0.00,'C:\\Users\\juana\\eclipse-workspaceProgramacion3\\TALLER_MECANICO\\imagenes_ordenes\\1779918340175_car.png'),(60001,90002,'2026-05-27','2026-05-30','Reparación',0,'E','','EN ESPERA',0.00,0.00,0.00,0.00,0.00,NULL);
/*!40000 ALTER TABLE `ordenes_servicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  PRIMARY KEY (`id_usuario`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'carlos','1234');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehiculos`
--

DROP TABLE IF EXISTS `vehiculos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehiculos` (
  `id_vehiculo` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `marca` varchar(50) NOT NULL,
  `modelo` varchar(50) NOT NULL,
  `anio` int NOT NULL,
  `placas` varchar(20) NOT NULL,
  `numero_serie` varchar(50) NOT NULL,
  `imagen_vehiculo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_vehiculo`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `placas` (`placas`),
  UNIQUE KEY `numero_serie` (`numero_serie`),
  KEY `fk_vehiculos_clientes` (`id_cliente`),
  CONSTRAINT `fk_vehiculos_clientes` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=150002;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehiculos`
--

LOCK TABLES `vehiculos` WRITE;
/*!40000 ALTER TABLE `vehiculos` DISABLE KEYS */;
INSERT INTO `vehiculos` VALUES (1,3,'WOLSVAGEN','SUBURBAN',2006,'200123cs','12345','C:\\Users\\setri\\Taller-Mecanico-UABCS\\TALLER_MECANICO\\imagenes_vehiculos\\1779727875433_Untitled Diagram.drawio.png'),(60002,4,'honda','crv',2010,'sxx-sds-23s-23d','232342423342342343242','C:\\Users\\rod\\eclipse-workspace\\TALLER_MECANICO-branch-sebas\\imagenes_vehiculos\\1779917920331_hq720.jpg'),(90002,1,'totori','totori2',2010,'qwerty','435345','C:\\Users\\juana\\eclipse-workspaceProgramacion3\\TALLER_MECANICO\\imagenes_vehiculos\\1779922002226_car.png'),(120002,1,'misubishi','mic',2020,'','','C:\\Users\\juana\\eclipse-workspaceProgramacion3\\TALLER_MECANICO\\imagenes_vehiculos\\1779925985249_car.png');
/*!40000 ALTER TABLE `vehiculos` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-27 20:51:23
