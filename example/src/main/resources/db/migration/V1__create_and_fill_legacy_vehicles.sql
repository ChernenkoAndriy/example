CREATE TABLE legacy_vehicles (
                                 id SERIAL PRIMARY KEY,
                                 name VARCHAR(255),
                                 production_year INT
);

INSERT INTO legacy_vehicles (name, production_year) VALUES ('Toyota Camry', 2020);
INSERT INTO legacy_vehicles (name, production_year) VALUES ('Honda Civic', 2018);
INSERT INTO legacy_vehicles (name, production_year) VALUES ('Ford Focus', 2015);