-- =====================================================================
-- Caso Empresa XYZ - Sistema de gestión de ventas e inventario
-- Script 01: creación de la base de datos "ventas" y sus tablas
-- Motor: MySQL 8.0+  (InnoDB es obligatorio para usar transacciones)
-- =====================================================================

DROP DATABASE IF EXISTS ventas;
CREATE DATABASE ventas
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;

USE ventas;

-- ---------------------------------------------------------------------
-- Tabla productos: inventario disponible en la tienda
-- ---------------------------------------------------------------------
CREATE TABLE productos (
  id_producto  INT            NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(100)   NOT NULL,
  descripcion  VARCHAR(255)   NULL,
  precio       DECIMAL(12,2)  NOT NULL,
  cantidad     INT            NOT NULL DEFAULT 0,
  CONSTRAINT pk_productos PRIMARY KEY (id_producto),
  CONSTRAINT uq_productos_nombre UNIQUE (nombre),
  CONSTRAINT ck_productos_precio CHECK (precio >= 0),
  CONSTRAINT ck_productos_cantidad CHECK (cantidad >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Tabla ventas: cada venta registrada por un vendedor
-- ---------------------------------------------------------------------
CREATE TABLE ventas (
  id_venta      INT            NOT NULL AUTO_INCREMENT,
  fecha         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  id_producto   INT            NOT NULL,
  cantidad      INT            NOT NULL,
  precio_venta  DECIMAL(12,2)  NOT NULL,
  CONSTRAINT pk_ventas PRIMARY KEY (id_venta),
  CONSTRAINT fk_ventas_productos FOREIGN KEY (id_producto)
    REFERENCES productos (id_producto)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_ventas_cantidad CHECK (cantidad > 0),
  CONSTRAINT ck_ventas_precio CHECK (precio_venta >= 0)
) ENGINE = InnoDB;

CREATE INDEX ix_ventas_fecha ON ventas (fecha);

-- Verificación rápida (útil para el pantallazo)
SHOW TABLES;
DESCRIBE productos;
DESCRIBE ventas;
