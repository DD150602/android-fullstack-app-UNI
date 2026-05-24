CREATE DATABASE IF NOT EXISTS shopeasy_db;

USE shopeasy_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    celular VARCHAR(15),
    correo VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE INDEX idx_correo ON users(correo);
CREATE INDEX idx_cedula ON users(cedula);
