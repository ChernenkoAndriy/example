
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



INSERT INTO vehicle_entity (brand, model)
SELECT
    split_part(upper_name, ' ', 1),
    split_part(upper_name, ' ', 2)
FROM vehicle_names_processed;


INSERT INTO car_entity (id, seating_capacity)
SELECT id, 5 FROM vehicle_entity;


DROP TABLE vehicle_names_processed;
DROP TABLE legacy_vehicles;