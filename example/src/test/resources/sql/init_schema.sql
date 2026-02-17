DROP TABLE IF EXISTS car_entity;
DROP TABLE IF EXISTS truck_entity;
DROP TABLE IF EXISTS vehicle_entity;

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