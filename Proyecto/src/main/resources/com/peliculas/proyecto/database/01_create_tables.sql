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
    num_tel VARCHAR(20),
    contrasena VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- TABLA PELICULA
CREATE TABLE IF NOT EXISTS pelicula (
   id_pelicula INT AUTO_INCREMENT PRIMARY KEY,
   titulo VARCHAR(155) NOT NULL,
   anio_salida VARCHAR(20) NOT NULL,
   director VARCHAR(95) NOT NULL,
   resumen TEXT,
   genero VARCHAR(100) NOT NULL,
   disponible INT DEFAULT 0,
   url_photo VARCHAR(100) NOT NULL,
   valoracion DECIMAL(3,1) DEFAULT 0.0
) ENGINE=InnoDB;

-- TABLA LISTA
CREATE TABLE IF NOT EXISTS lista (
    id_lista INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre_lista VARCHAR(100) DEFAULT 'Mi Lista',
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- TABLA PELÍCULAS QUE HAY EN LA LISTA
CREATE TABLE IF NOT EXISTS lista_pelicula (
    id_lista INT NOT NULL,
    id_pelicula INT NOT NULL,
    PRIMARY KEY (id_lista, id_pelicula),
    FOREIGN KEY (id_lista) REFERENCES lista(id_lista) ON DELETE CASCADE,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
) ENGINE=InnoDB;

-- TABLA RESEÑAS
CREATE TABLE IF NOT EXISTS resena (
    id_resena INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_pelicula INT NOT NULL,
    texto TEXT,
    valoracion DECIMAL(3,1) NOT NULL CHECK (valoracion BETWEEN 0.5 AND 5 AND (valoracion * 2) = FLOOR(valoracion * 2)),
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE,
    UNIQUE (id_usuario, id_pelicula)
) ENGINE=InnoDB;


-- TABLA PELICULAS DISPONIBLES
CREATE TABLE IF NOT EXISTS peliculas_disponibles (
   id_lista_peliculas_disponibles INT AUTO_INCREMENT PRIMARY KEY,
   id_pelicula INT NOT NULL
);

-- TABLA ALQUILER
CREATE TABLE IF NOT EXISTS alquiler (
    id_usuario INT NOT NULL,
    id_pelicula INT NOT NULL,
    fecha_alquiler VARCHAR(50) NOT NULL,
    fecha_devolucion VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_usuario, id_pelicula),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
) ENGINE=InnoDB;

-- TABLA PELICULAS VISTAS
CREATE TABLE IF NOT EXISTS peliculas_vistas (
    id_usuario INT NOT NULL,
    id_pelicula INT NOT NULL,
    PRIMARY KEY (id_usuario, id_pelicula),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
) ENGINE=InnoDB;