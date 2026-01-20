CREATE TABLE IF NOT EXISTS vehicle_entity (
                                              id BIGSERIAL PRIMARY KEY,
                                              brand VARCHAR(255),
    model VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS car_entity (
                                          id BIGINT PRIMARY KEY REFERENCES vehicle_entity(id) ON DELETE CASCADE,
    seating_capacity INTEGER
    );