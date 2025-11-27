-- TABLA ADMINISTRADOR
CREATE TABLE IF NOT EXISTS administrador (
id_admin INT AUTO_INCREMENT PRIMARY KEY,
usuario VARCHAR(100) NOT NULL UNIQUE,
contrasena VARCHAR(255) NOT NULL
) ENGINE=InnoDB;


-- TABLA USUARIO
CREATE TABLE IF NOT EXISTS usuario (
id_usuario INT AUTO_INCREMENT PRIMARY KEY,
nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
correo VARCHAR(150) NOT NULL UNIQUE,
num_tel VARCHAR(20) UNIQUE,
contrasena VARCHAR(255) NOT NULL
) ENGINE=InnoDB;


-- TABLA CATEGORIA
CREATE TABLE IF NOT EXISTS categoria (
id_categoria INT AUTO_INCREMENT PRIMARY KEY,
nombre_categoria VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;


-- TABLA PELICULA
CREATE TABLE IF NOT EXISTS pelicula (
id_pelicula INT AUTO_INCREMENT PRIMARY KEY,
titulo VARCHAR(200) NOT NULL,
descripcion TEXT,
year YEAR,
id_categoria INT,
disponible INT DEFAULT 0, -- 1 disponible, 0 no disponible
FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE=InnoDB;


-- TABLA FAVORITOS
CREATE TABLE IF NOT EXISTS favoritos (
id_favorito INT AUTO_INCREMENT PRIMARY KEY,
id_usuario INT NOT NULL,
id_pelicula INT NOT NULL,
fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula)
) ENGINE=InnoDB;


-- TABLA RESEÑA
CREATE TABLE IF NOT EXISTS resena (
id_resena INT AUTO_INCREMENT PRIMARY KEY,
id_usuario INT NOT NULL,
id_pelicula INT NOT NULL,
comentario TEXT,
puntuacion INT CHECK (puntuacion BETWEEN 1 AND 5),
fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula)
) ENGINE=InnoDB;

-- TABLA PELICULAS DISPONIBLES
CREATE TABLE IF NOT EXISTS peliculas_disponibles (
   id_lista_peliculas_disponibles INT AUTO_INCREMENT PRIMARY KEY,
   id_pelicula INT NOT NULL
) ENGINE=InnoDB;