-- Очищення перед вставкою (опціонально)
DELETE FROM car_entity;
DELETE FROM vehicle_entity;

-- Вставка тестових даних
INSERT INTO vehicle_entity (id, brand, model) VALUES (100, 'Toyota', 'Camry');
INSERT INTO car_entity (id, seating_capacity) VALUES (100, 5);

INSERT INTO vehicle_entity (id, brand, model) VALUES (101, 'Honda', 'Civic');
INSERT INTO car_entity (id, seating_capacity) VALUES (101, 5);

-- Скидання лічильника ID (для PostgreSQL)
SELECT setval('vehicle_entity_id_seq', 101);