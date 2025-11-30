-- Desactivar temporalmente las comprobaciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- Primero, truncar tablas que dependen de otras
TRUNCATE TABLE lista_pelicula;
TRUNCATE TABLE resena;
TRUNCATE TABLE alquiler;
TRUNCATE TABLE lista;

-- Tablas independientes
TRUNCATE TABLE peliculas_disponibles;
TRUNCATE TABLE pelicula;
TRUNCATE TABLE usuario;
TRUNCATE TABLE administrador;

-- Volver a activar las comprobaciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;
