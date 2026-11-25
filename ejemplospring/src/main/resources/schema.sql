-- ============================================================
-- Esquema: Módulo Beneficios y Convocatorias
-- Motor: MySQL InnoDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS ejemplospring_bd
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ejemplospring_bd;

CREATE TABLE IF NOT EXISTS beneficios (
    id_beneficio   INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(150) NOT NULL,
    descripcion    TEXT         NOT NULL,
    categoria      VARCHAR(100) NOT NULL,
    estado         ENUM('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS convocatorias (
    id_convocatoria INT AUTO_INCREMENT PRIMARY KEY,
    id_beneficio    INT            NOT NULL,
    nombre          VARCHAR(150)   NOT NULL,
    fecha_inicio    DATE           NOT NULL,
    fecha_cierre    DATE           NOT NULL,
    cupos           INT            NOT NULL,
    valor_pago      DECIMAL(12,2)  NOT NULL,
    estado          ENUM('Activa','Inactiva','Cerrada') NOT NULL DEFAULT 'Inactiva',
    fecha_creacion  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_convocatorias_beneficios
        FOREIGN KEY (id_beneficio) REFERENCES beneficios (id_beneficio)
) ENGINE=InnoDB;
