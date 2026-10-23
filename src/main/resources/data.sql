-- ==========================================
-- DATOS POR DEFECTO PARA CLIENTES
-- ==========================================
INSERT INTO cliente_entity (id, nombre, documento, telefono, email) VALUES (1, 'Carlos Pérez', '123456789', '3001234567', 'carlos@email.com');
INSERT INTO cliente_entity (id, nombre, documento, telefono, email) VALUES (2, 'Maria Gomez', '987654321', '3109876543', 'maria@email.com');
INSERT INTO cliente_entity (id, nombre, documento, telefono, email) VALUES (3, 'Juan Rodríguez', '1122334455', '3201122334', 'juan.rodriguez@email.com');
INSERT INTO cliente_entity (id, nombre, documento, telefono, email) VALUES (4, 'Ana Martínez', '5544332211', '3155544332', 'ana.martinez@email.com');
INSERT INTO cliente_entity (id, nombre, documento, telefono, email) VALUES (5, 'Luis Fernández', '9988776655', '3189988776', 'luis.fernandez@email.com');
INSERT INTO cliente_entity (id, nombre, documento, telefono, email) VALUES (6, 'Diana López', '3344556677', '3013344556', 'diana.lopez@email.com');

-- ==========================================
-- DATOS POR DEFECTO PARA AUTOS
-- Estados válidos: DISPONIBLE, ALQUILADO, MANTENIMIENTO
-- ==========================================
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (1, 'TOY125', 'Toyota', 'Corolla', 2025, 150000, 500000, 'DISPONIBLE');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (2, 'ONX223', 'Chevrolet', 'Onix', 2023, 120000, 400000, 'DISPONIBLE');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (3, 'VER322', 'Nissan', 'Versa', 2022, 100000, 300000, 'MANTENIMIENTO');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (4, 'PIC424', 'Kia', 'Picanto', 2024, 90000, 300000, 'DISPONIBLE');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (5, 'MAZ523', 'Mazda', '3', 2023, 160000, 600000, 'ALQUILADO');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (6, 'LOG621', 'Renault', 'Logan', 2021, 85000, 250000, 'DISPONIBLE');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (7, 'FIE722', 'Ford', 'Fiesta', 2022, 110000, 350000, 'DISPONIBLE');
INSERT INTO auto_entity (id, placa, marca, modelo, anio, precio_por_dia, deposito, estado) VALUES (8, 'TUC825', 'Hyundai', 'Tucson', 2025, 200000, 800000, 'ALQUILADO');

ALTER TABLE cliente_entity ALTER COLUMN id RESTART WITH 7;
ALTER TABLE auto_entity ALTER COLUMN id RESTART WITH 9;