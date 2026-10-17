-- =====================================================================
-- Script 03: integración con el sistema de contabilidad
-- Cada venta confirmada genera un asiento contable de partida doble:
--   Débito  110505 Caja general
--   Crédito 413524 Comercio al por menor de electrodomésticos
-- El asiento se inserta en la MISMA transacción de la venta: si falla
-- la contabilidad, también se revierte la venta (ROLLBACK).
-- =====================================================================

USE ventas;

CREATE TABLE asientos_contables (
  id_asiento      INT            NOT NULL AUTO_INCREMENT,
  id_venta        INT            NOT NULL,
  fecha           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cuenta_debito   VARCHAR(10)    NOT NULL,
  cuenta_credito  VARCHAR(10)    NOT NULL,
  valor           DECIMAL(14,2)  NOT NULL,
  descripcion     VARCHAR(255)   NOT NULL,
  CONSTRAINT pk_asientos PRIMARY KEY (id_asiento),
  CONSTRAINT uq_asientos_venta UNIQUE (id_venta),
  CONSTRAINT fk_asientos_ventas FOREIGN KEY (id_venta)
    REFERENCES ventas (id_venta)
    ON DELETE CASCADE,
  CONSTRAINT ck_asientos_valor CHECK (valor > 0)
) ENGINE = InnoDB;

-- Vista que usa el sistema contable para consultar los ingresos por día
CREATE OR REPLACE VIEW v_resumen_contable AS
SELECT DATE(a.fecha)       AS dia,
       COUNT(*)            AS numero_ventas,
       SUM(a.valor)        AS total_ingresos
FROM asientos_contables a
GROUP BY DATE(a.fecha);

-- Conciliación: toda venta debe tener exactamente un asiento contable.
-- Si esta consulta devuelve filas, hay ventas sin contabilizar.
CREATE OR REPLACE VIEW v_conciliacion AS
SELECT v.id_venta,
       v.cantidad * v.precio_venta AS total_venta,
       a.valor                     AS valor_contabilizado
FROM ventas v
LEFT JOIN asientos_contables a ON a.id_venta = v.id_venta
WHERE a.id_asiento IS NULL
   OR a.valor <> v.cantidad * v.precio_venta;

DESCRIBE asientos_contables;
