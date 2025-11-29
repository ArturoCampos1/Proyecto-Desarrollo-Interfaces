/*
----------------------------------
PROCEDIMIENTOS CRUD PARA USUARIOS
----------------------------------
*/

-- Crear usuario
DELIMITER $$
DROP PROCEDURE IF EXISTS crear_usuario$$
CREATE PROCEDURE crear_usuario (
    IN p_nombre_usuario VARCHAR(100),
    IN p_correo VARCHAR(150),
    IN p_num_tel VARCHAR(20),
    IN p_contrasena VARCHAR(255)
)
BEGIN
    INSERT INTO usuario (nombre_usuario, correo, num_tel, contrasena)
    VALUES (p_nombre_usuario, p_correo, p_num_tel, p_contrasena);
END$$
DELIMITER ;

-- Buscar usuario por nombre de usuario
DELIMITER $$
DROP PROCEDURE IF EXISTS buscar_usuario_por_nombre$$
CREATE PROCEDURE buscar_usuario_por_nombre (
    IN p_nombre_usuario VARCHAR(100)
)
BEGIN
    SELECT * FROM usuario
    WHERE nombre_usuario = p_nombre_usuario;
END$$
DELIMITER ;

-- Modificar usuario
DELIMITER $$
DROP PROCEDURE IF EXISTS modificar_usuario$$
CREATE PROCEDURE modificar_usuario (
    IN p_id_usuario INT,
    IN p_nombre_usuario VARCHAR(100),
    IN p_correo VARCHAR(150),
    IN p_num_tel VARCHAR(20),
    IN p_contrasena VARCHAR(255)
)
BEGIN
    UPDATE usuario
    SET nombre_usuario = p_nombre_usuario,
        correo = p_correo,
        num_tel = p_num_tel,
        contrasena = p_contrasena
    WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

-- Eliminar usuario
DELIMITER $$
DROP PROCEDURE IF EXISTS eliminar_usuario$$
CREATE PROCEDURE eliminar_usuario (
    IN p_id_usuario INT
)
BEGIN
    DELETE FROM usuario
    WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

-- Obtener todos los usuarios
DELIMITER $$
DROP PROCEDURE IF EXISTS obtener_todos_usuarios$$
CREATE PROCEDURE obtener_todos_usuarios()
BEGIN
    SELECT * FROM usuario;
END$$
DELIMITER ;

/*
----------------------------------
PROCEDIMIENTOS CRUD PARA PELICULAS
----------------------------------
*/

-- Crear película
DELIMITER $$

DROP PROCEDURE IF EXISTS crear_pelicula$$
CREATE PROCEDURE crear_pelicula (
    IN p_titulo VARCHAR(255),
    IN p_anio_salida YEAR,
    IN p_director VARCHAR(255),
    IN p_resumen TEXT,
    IN p_genero VARCHAR(100),
    IN p_url_photo VARCHAR(100),
    IN p_valoracion DECIMAL(3,1),
    IN p_disponible INT
)
BEGIN
    IF p_disponible IS NULL THEN
        SET p_disponible = 0;
    END IF;

    INSERT INTO pelicula (titulo, anio_salida, director, resumen, genero, url_photo, valoracion, disponible)
    VALUES (p_titulo, p_anio_salida, p_director, p_resumen, p_genero, p_url_photo, p_valoracion, p_disponible);

    SELECT LAST_INSERT_ID() AS id_pelicula;
END$$

DELIMITER ;

-- Buscar películas por nombre
DELIMITER $$
DROP PROCEDURE IF EXISTS buscar_peliculas_por_nombre$$
CREATE PROCEDURE buscar_peliculas_por_nombre (
    IN p_nombre VARCHAR(255)
)
BEGIN
    SELECT *
    FROM pelicula
    WHERE titulo LIKE CONCAT('%', p_nombre, '%');
END$$
DELIMITER ;

-- Modificar película
DELIMITER $$

DROP PROCEDURE IF EXISTS modificar_pelicula$$
CREATE PROCEDURE modificar_pelicula (
    IN p_id_pelicula INT,
    IN p_titulo VARCHAR(255),
    IN p_anio_salida YEAR,
    IN p_director VARCHAR(255),
    IN p_resumen TEXT,
    IN p_genero VARCHAR(100),
    IN p_valoracion DECIMAL(3,1),
    IN p_disponible INT
)
BEGIN
    UPDATE pelicula
    SET titulo = p_titulo,
        anio_salida = p_anio_salida,
        director = p_director,
        resumen = p_resumen,
        genero = p_genero,
        valoracion = p_valoracion,
        disponible = p_disponible
    WHERE id_pelicula = p_id_pelicula;
END$$

DELIMITER ;


-- Eliminar película
DELIMITER $$
DROP PROCEDURE IF EXISTS eliminar_pelicula$$
CREATE PROCEDURE eliminar_pelicula (
    IN p_id_pelicula INT
)
BEGIN
    DELETE FROM pelicula
    WHERE id_pelicula = p_id_pelicula;
END$$
DELIMITER ;


DELIMITER $$
DROP PROCEDURE IF EXISTS obtener_peliculas_disponibles$$
CREATE PROCEDURE obtener_peliculas_disponibles()
BEGIN
    SELECT *
    FROM pelicula
    WHERE disponible > 0;
END$$
DELIMITER ;

/*
----------------------------------
PROCEDIMIENTOS CRUD PARA RESEÑAS
----------------------------------
*/

-- Crear reseña
DELIMITER $$
DROP PROCEDURE IF EXISTS crear_resena$$
CREATE PROCEDURE crear_resena (
    IN p_valoracion DECIMAL(3,1),
    IN p_texto TEXT,
    IN p_id_usuario INT,
    IN p_id_pelicula INT
)
BEGIN
    INSERT INTO resena (valoracion, texto, id_usuario, id_pelicula)
    VALUES (p_valoracion, p_texto, p_id_usuario, p_id_pelicula);
END$$
DELIMITER ;

-- Buscar reseñas por película
DELIMITER $$
DROP PROCEDURE IF EXISTS buscar_resenas_por_pelicula$$
CREATE PROCEDURE buscar_resenas_por_pelicula (
    IN p_id_pelicula INT
)
BEGIN
    SELECT r.*
    FROM resena r
    WHERE r.id_pelicula = p_id_pelicula;
END$$
DELIMITER ;

-- Modificar reseña
DELIMITER $$
DROP PROCEDURE IF EXISTS modificar_resena$$
CREATE PROCEDURE modificar_resena (
    IN p_id_resena INT,
    IN p_valoracion DECIMAL(3,1),
    IN p_texto TEXT
)
BEGIN
    UPDATE resena
    SET valoracion = p_valoracion,
        texto = p_texto
    WHERE id_resena = p_id_resena;
END$$
DELIMITER ;

-- Eliminar reseña
DELIMITER $$
DROP PROCEDURE IF EXISTS eliminar_resena$$
CREATE PROCEDURE eliminar_resena (
    IN p_id_resena INT
)
BEGIN
    DELETE FROM resena
    WHERE id_resena = p_id_resena;
END$$
DELIMITER ;

/*
----------------------------------
PROCEDIMIENTOS CRUD PARA LISTA
----------------------------------
*/

-- Crear lista
DELIMITER $$
DROP PROCEDURE IF EXISTS crear_lista$$
CREATE PROCEDURE crear_lista (
    IN p_id_usuario INT,
    IN p_nombre_lista VARCHAR(100)
)
BEGIN
    INSERT INTO lista (id_usuario, nombre_lista)
    VALUES (p_id_usuario, p_nombre_lista);
END$$
DELIMITER ;

-- Obtener listas de un usuario
DELIMITER $$
DROP PROCEDURE IF EXISTS obtener_listas_por_nombre_usuario$$
CREATE PROCEDURE obtener_listas_por_nombre_usuario (
    IN p_nombre_usuario VARCHAR(100)
)
BEGIN
    SELECT l.*
    FROM lista l
    JOIN usuario u ON l.id_usuario = u.id_usuario
    WHERE u.nombre_usuario = p_nombre_usuario;
END$$
DELIMITER ;

-- Modificar lista
DELIMITER $$
DROP PROCEDURE IF EXISTS modificar_lista$$
CREATE PROCEDURE modificar_lista (
    IN p_id_lista INT,
    IN p_nombre_lista VARCHAR(100)
)
BEGIN
    UPDATE lista
    SET nombre_lista = p_nombre_lista
    WHERE id_lista = p_id_lista;
END$$
DELIMITER ;

-- Eliminar lista
DELIMITER $$
DROP PROCEDURE IF EXISTS eliminar_lista$$
CREATE PROCEDURE eliminar_lista (
    IN p_id_lista INT
)
BEGIN
    DELETE FROM lista
    WHERE id_lista = p_id_lista;
END$$
DELIMITER ;

/*
----------------------------------
PROCEDIMIENTOS CRUD PARA ALQUILERES
----------------------------------
*/

-- Crear alquiler
DELIMITER $$
DROP PROCEDURE IF EXISTS crear_alquiler$$
CREATE PROCEDURE crear_alquiler (
    IN p_id_usuario INT,
    IN p_id_pelicula INT,
    IN p_precio DECIMAL(6,2)
)
BEGIN
    INSERT INTO alquiler (id_usuario, id_pelicula, precio)
    VALUES (p_id_usuario, p_id_pelicula, p_precio);
END$$
DELIMITER ;

-- Obtener alquileres por usuario
DELIMITER $$
DROP PROCEDURE IF EXISTS obtener_alquileres_por_usuario$$
CREATE PROCEDURE obtener_alquileres_por_usuario (
    IN p_id_usuario INT
)
BEGIN
    SELECT a.*, p.titulo
    FROM alquiler a
    JOIN pelicula p ON a.id_pelicula = p.id_pelicula
    WHERE a.id_usuario = p_id_usuario;
END$$
DELIMITER ;

-- Modificar alquiler (fecha de devolución y precio)
DELIMITER $$
DROP PROCEDURE IF EXISTS modificar_alquiler$$
CREATE PROCEDURE modificar_alquiler (
    IN p_id_usuario INT,
    IN p_id_pelicula INT,
    IN p_precio DECIMAL(6,2),
    IN p_fecha_devolucion TIMESTAMP
)
BEGIN
    UPDATE alquiler
    SET precio = p_precio,
        fecha_devolucion = p_fecha_devolucion
    WHERE id_usuario = p_id_usuario
      AND id_pelicula = p_id_pelicula;
END$$
DELIMITER ;

-- Eliminar alquiler
DELIMITER $$
DROP PROCEDURE IF EXISTS eliminar_alquiler$$
CREATE PROCEDURE eliminar_alquiler (
    IN p_id_usuario INT,
    IN p_id_pelicula INT
)
BEGIN
    DELETE FROM alquiler
    WHERE id_usuario = p_id_usuario
      AND id_pelicula = p_id_pelicula;
END$$
DELIMITER ;

/* ---------------------------------- PROCEDIMIENTOS CRUD PARA LISTA_PELICULA ---------------------------------- */

-- Agregar película a una lista
DELIMITER $$
DROP PROCEDURE IF EXISTS agregar_pelicula_a_lista$$
CREATE PROCEDURE agregar_pelicula_a_lista (
    IN p_id_lista INT,
    IN p_id_pelicula INT
)
BEGIN
    INSERT IGNORE INTO lista_pelicula (id_lista, id_pelicula)
    VALUES (p_id_lista, p_id_pelicula);
END$$
DELIMITER ;

-- Quitar película de una lista
DELIMITER $$
DROP PROCEDURE IF EXISTS quitar_pelicula_de_lista$$
CREATE PROCEDURE quitar_pelicula_de_lista (
    IN p_id_lista INT,
    IN p_id_pelicula INT
)
BEGIN
    DELETE FROM lista_pelicula
    WHERE id_lista = p_id_lista
      AND id_pelicula = p_id_pelicula;
END$$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS obtener_peliculas_de_lista$$
CREATE PROCEDURE obtener_peliculas_de_lista (
    IN p_id_lista INT
)
BEGIN
    SELECT p.id_pelicula, p.titulo, p.anio_salida, p.director, p.resumen, p.genero
    FROM pelicula p
    INNER JOIN lista_pelicula lp ON p.id_pelicula = lp.id_pelicula
    WHERE lp.id_lista = p_id_lista;
END$$
DELIMITER ;


--LISTA DISPONIBLES

DELIMITER $$

DROP PROCEDURE IF EXISTS obtener_peliculas_disponibles$$
CREATE PROCEDURE obtener_peliculas_disponibles()
BEGIN
    SELECT pd.id_pelicula, p.titulo, p.genero, p.disponible, p.url_photo
    FROM peliculas_disponibles pd
    JOIN pelicula p ON p.id_pelicula = pd.id_pelicula;
END$$



/* ---------------------------------- PROCEDIMIENTOS CRUD PARA LISTAs-PUBLICAS ---------------------------------- */

DELIMITER $$
DROP PROCEDURE IF EXISTS obtener_usuarios_excepto$$
CREATE PROCEDURE obtener_usuarios_excepto(IN p_nombre_actual VARCHAR(100))
BEGIN
    SELECT id_usuario, nombre_usuario
    FROM usuario
    WHERE nombre_usuario != p_nombre_actual;
END$$
DELIMITER ;