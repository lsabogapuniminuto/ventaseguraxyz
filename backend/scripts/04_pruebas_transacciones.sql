-- =====================================================================
-- Script 04: demostración manual de COMMIT y ROLLBACK en MySQL
-- Ejecute cada bloque por separado y tome un pantallazo del resultado.
-- (En MySQL Workbench: seleccione el bloque y presione Ctrl+Shift+Enter)
-- =====================================================================

USE ventas;

-- ---------------------------------------------------------------------
-- ESCENARIO A: venta exitosa -> COMMIT
-- Se venden 2 audífonos (id_producto = 3). El stock baja de 25 a 23.
-- ---------------------------------------------------------------------
SELECT id_producto, nombre, cantidad FROM productos WHERE id_producto = 3;   -- ANTES

START TRANSACTION;
  INSERT INTO ventas (id_producto, cantidad, precio_venta)
  VALUES (3, 2, 449000.00);

  SET @id_venta = LAST_INSERT_ID();

  UPDATE productos
     SET cantidad = cantidad - 2
   WHERE id_producto = 3;

  INSERT INTO asientos_contables (id_venta, cuenta_debito, cuenta_credito, valor, descripcion)
  VALUES (@id_venta, '110505', '413524', 2 * 449000.00, CONCAT('Venta #', @id_venta, ' - prueba manual'));
COMMIT;

SELECT id_producto, nombre, cantidad FROM productos WHERE id_producto = 3;   -- DESPUÉS (23)
SELECT * FROM ventas ORDER BY id_venta DESC LIMIT 1;
SELECT * FROM asientos_contables ORDER BY id_asiento DESC LIMIT 1;


-- ---------------------------------------------------------------------
-- ESCENARIO B: error durante la venta -> ROLLBACK
-- Se intenta vender 10 televisores (id_producto = 8) pero solo hay 3.
-- El UPDATE viola el CHECK (cantidad >= 0) y la operación se deshace.
-- ---------------------------------------------------------------------
SELECT id_producto, nombre, cantidad FROM productos WHERE id_producto = 8;   -- ANTES (3)
SELECT COUNT(*) AS total_ventas_antes FROM ventas;

START TRANSACTION;
  INSERT INTO ventas (id_producto, cantidad, precio_venta)
  VALUES (8, 10, 1799000.00);

  -- La venta aparece DENTRO de la transacción...
  SELECT * FROM ventas ORDER BY id_venta DESC LIMIT 1;

  -- ...pero este UPDATE falla: Error 3819 Check constraint 'ck_productos_cantidad' is violated
  UPDATE productos
     SET cantidad = cantidad - 10
   WHERE id_producto = 8;
ROLLBACK;

SELECT id_producto, nombre, cantidad FROM productos WHERE id_producto = 8;   -- DESPUÉS (sigue en 3)
SELECT COUNT(*) AS total_ventas_despues FROM ventas;                          -- igual que antes


-- ---------------------------------------------------------------------
-- ESCENARIO C: ROLLBACK explícito (el vendedor cancela la operación)
-- ---------------------------------------------------------------------
START TRANSACTION;
  UPDATE productos SET precio = 1.00 WHERE id_producto = 1;
  SELECT id_producto, nombre, precio FROM productos WHERE id_producto = 1;    -- se ve 1.00
ROLLBACK;
SELECT id_producto, nombre, precio FROM productos WHERE id_producto = 1;      -- vuelve a 2499000.00


-- ---------------------------------------------------------------------
-- Consultas de la integración contable
-- ---------------------------------------------------------------------
SELECT * FROM v_resumen_contable;
SELECT * FROM v_conciliacion;      -- vacío = ventas y contabilidad cuadran
