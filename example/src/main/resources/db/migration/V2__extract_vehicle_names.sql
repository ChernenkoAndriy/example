CREATE TABLE vehicle_names_processed (
                                         legacy_id INT REFERENCES legacy_vehicles(id),
                                         upper_name VARCHAR(255)
);

INSERT INTO vehicle_names_processed (legacy_id, upper_name)
SELECT id, UPPER(name) FROM legacy_vehicles;