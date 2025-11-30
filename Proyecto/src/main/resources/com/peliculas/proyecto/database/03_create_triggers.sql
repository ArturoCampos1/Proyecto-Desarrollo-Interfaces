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

-- TRIGGERS PARA LISTA DE DISPONIBLES

-- Trigger que elimina de peliculas_disponibles si la película deja de estar disponible
DELIMITER $$

CREATE TRIGGER trg_quitar_pelicula_disponible
AFTER UPDATE ON pelicula
FOR EACH ROW
BEGIN
    -- Si antes estaba disponible y ahora no lo está, eliminar de la lista
    IF OLD.disponible > 0 AND NEW.disponible = 0 THEN
        DELETE FROM peliculas_disponibles
        WHERE id_pelicula = NEW.id_pelicula;
    END IF;
END$$

DELIMITER ;