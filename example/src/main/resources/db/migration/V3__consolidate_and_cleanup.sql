-- 1. Створюємо фінальні таблиці
CREATE TABLE vehicle_entity (
                                id BIGSERIAL PRIMARY KEY,
                                brand VARCHAR(255),
                                model VARCHAR(255)
);

CREATE TABLE car_entity (
                            id BIGINT PRIMARY KEY REFERENCES vehicle_entity(id) ON DELETE CASCADE,
                            seating_capacity INTEGER
);

CREATE TABLE truck_entity (
                              id BIGINT PRIMARY KEY REFERENCES vehicle_entity(id) ON DELETE CASCADE,
                              payload_capacity DOUBLE PRECISION
);

-- 2. Перенесення даних: спочатку в vehicle_entity, потім у car_entity
-- Використовуємо імена з V2, щоб зробити ці записи доступними як Car сутності
INSERT INTO vehicle_entity (brand, model)
SELECT
    split_part(upper_name, ' ', 1),
    split_part(upper_name, ' ', 2)
FROM vehicle_names_processed;

-- Для кожного створеного авто додаємо запис у car_entity (наприклад, 5 місць за замовчуванням)
INSERT INTO car_entity (id, seating_capacity)
SELECT id, 5 FROM vehicle_entity;

-- 3. Очищення старих таблиць
DROP TABLE vehicle_names_processed;
DROP TABLE legacy_vehicles;