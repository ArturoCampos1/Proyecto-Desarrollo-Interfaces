/*
------------------
TRIGGERS PARA RESEÑAS
------------------
*/

DELIMITER //

CREATE TRIGGER trg_after_insert_resena
AFTER INSERT ON resena
FOR EACH ROW
BEGIN
    UPDATE pelicula
    SET valoracion = (
        SELECT LEAST(5.0, GREATEST(0.5, ROUND(AVG(r.valoracion)*2)/2))
        FROM resena r
        WHERE r.id_pelicula = NEW.id_pelicula
    )
    WHERE id_pelicula = NEW.id_pelicula;
END//

CREATE TRIGGER trg_after_update_resena
AFTER UPDATE ON resena
FOR EACH ROW
BEGIN
    UPDATE pelicula
    SET valoracion = (
        SELECT LEAST(5.0, GREATEST(0.5, ROUND(AVG(r.valoracion)*2)/2))
        FROM resena r
        WHERE r.id_pelicula = NEW.id_pelicula
    )
    WHERE id_pelicula = NEW.id_pelicula;
END//

CREATE TRIGGER trg_after_delete_resena
AFTER DELETE ON resena
FOR EACH ROW
BEGIN
    UPDATE pelicula
    SET valoracion = (
        SELECT LEAST(5.0, GREATEST(0.5, ROUND(AVG(r.valoracion)*2)/2))
        FROM resena r
        WHERE r.id_pelicula = OLD.id_pelicula
    )
    WHERE id_pelicula = OLD.id_pelicula;
END//

DELIMITER ;



/*
------------------
CLAVE PRIMARIA TABLA DISPONIBLES
------------------
*/

ALTER TABLE peliculas_disponibles
ADD CONSTRAINT pk_peliculas_disponibles PRIMARY KEY (id_pelicula);



/*
------------------
TRIGGERS CONTROL DISPONIBLE
------------------
*/

DELIMITER $$

-- Evitar valores negativos al insertar
CREATE TRIGGER trg_pelicula_no_negativo_ins
BEFORE INSERT ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible < 0 THEN
        SET NEW.disponible = 0;
    END IF;
END$$

-- Evitar valores negativos al actualizar
CREATE TRIGGER trg_pelicula_no_negativo_upd
BEFORE UPDATE ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible < 0 THEN
        SET NEW.disponible = 0;
    END IF;
END$$

-- Al insertar película → si disponible > 0 → añadir a peliculas_disponibles
CREATE TRIGGER trg_pelicula_sync_disp_ins
AFTER INSERT ON pelicula
FOR EACH ROW
BEGIN
    IF NEW.disponible > 0 THEN
        INSERT IGNORE INTO peliculas_disponibles (id_pelicula)
        VALUES (NEW.id_pelicula);
    END IF;
END$$

-- Al actualizar película → sincronizar disponibles
CREATE TRIGGER trg_pelicula_sync_disp_upd
AFTER UPDATE ON pelicula
FOR EACH ROW
BEGIN
    -- Si ya no hay stock → eliminar de disponibles
    IF NEW.disponible = 0 THEN
        DELETE FROM peliculas_disponibles
        WHERE id_pelicula = NEW.id_pelicula;
    END IF;

    -- Si hay stock → asegurar que exista en disponibles
    IF NEW.disponible > 0 THEN
        INSERT IGNORE INTO peliculas_disponibles (id_pelicula)
        VALUES (NEW.id_pelicula);
    END IF;
END$$

DELIMITER ;



/*
------------------
TRIGGER FECHA DEVOLUCIÓN ALQUILER
------------------
*/

DELIMITER //

CREATE TRIGGER calcular_fecha_devolucion
BEFORE INSERT ON alquiler
FOR EACH ROW
BEGIN
    SET NEW.fecha_devolucion =
        DATE_ADD(NEW.fecha_alquiler, INTERVAL 72 HOUR);
END//

DELIMITER ;
