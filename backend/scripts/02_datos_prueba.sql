-- =====================================================================
-- Script 02: datos de prueba del inventario
-- Ejecutar después de 01_crear_bd.sql
-- =====================================================================

USE ventas;

INSERT INTO productos (nombre, descripcion, precio, cantidad) VALUES
  ('Portátil Lenovo IdeaPad 3',   'Ryzen 5, 8 GB RAM, SSD 512 GB, 15.6"', 2499000.00, 10),
  ('Smartphone Samsung Galaxy A55','128 GB, 8 GB RAM, cámara 50 MP',      1599000.00, 15),
  ('Audífonos Sony WH-CH720N',    'Inalámbricos con cancelación de ruido', 449000.00, 25),
  ('Monitor LG 24" IPS',          'Full HD, 75 Hz, HDMI',                  599000.00,  8),
  ('Teclado mecánico Redragon',   'Switches rojos, retroiluminado RGB',    189000.00, 30),
  ('Mouse Logitech M185',         'Inalámbrico, receptor USB',              59000.00, 50),
  ('Tablet Xiaomi Pad 6',         '11", 128 GB, Wi-Fi',                    1399000.00,  5),
  ('Smart TV TCL 50" 4K',         'Google TV, HDR10',                      1799000.00,  3);

SELECT * FROM productos;
