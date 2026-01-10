/*
------------------
TRIGGERS PARA RESEÑAS
------------------
*/
-- Trigger que actualiza la valoración general de la película después de crear una reseña

DELIMITER //
CREATE TRIGGER trg_after_insert_resena
AFTER INSERT ON resena
FOR EACH ROW
BEGIN
    UPDATE pelicula
    SET valoracion = LEAST(5.0, GREATEST(0.5, ROUND(AVG(valoracion)*2)/2))
    WHERE id_pelicula = NEW.id_pelicula;
END //
DELIMITER ;

-- Trigger que actualiza la valoración general de la película después de modificar una reseña
DELIMITER //
CREATE TRIGGER trg_after_update_resena
AFTER UPDATE ON resena
FOR EACH ROW
BEGIN
    UPDATE pelicula
    SET valoracion = LEAST(5.0, GREATEST(0.5, ROUND(AVG(valoracion)*2)/2))
    WHERE id_pelicula = NEW.id_pelicula;
END //
DELIMITER ;

-- Trigger que actualiza la valoración general de la película después de eliminar una reseña
DELIMITER //
CREATE TRIGGER trg_after_delete_resena
AFTER DELETE ON resena
FOR EACH ROW
BEGIN
    UPDATE pelicula
    SET valoracion = LEAST(5.0, GREATEST(0.5, ROUND(AVG(valoracion)*2)/2))
    WHERE id_pelicula = OLD.id_pelicula;
END //
DELIMITER ;

ALTER TABLE peliculas_disponibles
ADD CONSTRAINT pk_peliculas_disponibles PRIMARY KEY (id_pelicula);

DELIMITER $$

CREATE TRIGGER trg_pelicula_no_negativo_ins
BEFORE INSERT ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible < 0 THEN
        SET NEW.disponible = 0;
    END IF;
END$$

CREATE TRIGGER trg_pelicula_no_negativo_upd
BEFORE UPDATE ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible < 0 THEN
        SET NEW.disponible = 0;
    END IF;
END$$

CREATE TRIGGER trg_pelicula_sync_disp_ins
AFTER INSERT ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible > 0 THEN
        INSERT IGNORE INTO peliculas_disponibles (id_pelicula)
        VALUES (NEW.id_pelicula);
    END IF;
END$$

CREATE TRIGGER trg_pelicula_sync_disp_upd
AFTER UPDATE ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible = 0 THEN
        DELETE FROM peliculas_disponibles
        WHERE id_pelicula = NEW.id_pelicula;
    END IF;

    IF NEW.disponible > 0 THEN
        INSERT IGNORE INTO peliculas_disponibles (id_pelicula)
        VALUES (NEW.id_pelicula);
    END IF;
END$$

DELIMITER ;


DELIMITER //

-- Trigger para calcular automaticamente la fecha de devolucion
DELIMITER //

CREATE TRIGGER calcular_fecha_devolucion
BEFORE INSERT ON alquiler
FOR EACH ROW
BEGIN
    SET NEW.fecha_devolucion =
        DATE_ADD(NEW.fecha_alquiler, INTERVAL 72 HOUR);
END//

DELIMITER ;