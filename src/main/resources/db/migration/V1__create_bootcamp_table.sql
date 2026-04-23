CREATE SCHEMA IF NOT EXISTS ms_bootcamp;

CREATE TABLE IF NOT EXISTS ms_bootcamp.bootcamp
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(90) NOT NULL,
    launch_date DATE NOT NULL,
    duration_months INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ms_bootcamp.bootcamp_capacity
(
    bootcamp_id BIGINT NOT NULL REFERENCES ms_bootcamp.bootcamp(id),
    capacity_id BIGINT NOT NULL,

    PRIMARY KEY(bootcamp_id, capacity_id)
);