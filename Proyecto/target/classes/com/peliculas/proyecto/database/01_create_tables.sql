USE cineverse;

-- TABLA ADMINISTRADOR
CREATE TABLE administrador (
   id_admin INT AUTO_INCREMENT PRIMARY KEY,
   usuario VARCHAR(100) NOT NULL UNIQUE,
   contrasena VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- TABLA USUARIO
CREATE TABLE usuario (
   id_usuario INT AUTO_INCREMENT PRIMARY KEY,
   nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
   correo VARCHAR(150) NOT NULL UNIQUE,
   num_tel VARCHAR(20) UNIQUE,
   contrasena VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- TABLA PELICULA
CREATE TABLE pelicula (
   id_pelicula INT AUTO_INCREMENT PRIMARY KEY,
   titulo VARCHAR(255) NOT NULL,
   anio_salida YEAR NOT NULL,
   director VARCHAR(255) NOT NULL,
   resumen TEXT,
   genero ENUM(
       'ACCION', 'AVENTURA', 'ANIMACION', 'COMEDIA', 'CRIMEN',
       'DOCUMENTAL', 'DRAMA', 'FAMILIAR', 'FANTASIA', 'HISTORIA',
       'TERROR', 'MUSICA', 'MISTERIO', 'ROMANCE', 'CIENCIA_FICCION',
       'PELICULA_DE_TV', 'SUSPENSO', 'BELICA', 'OESTE'
   ) NOT NULL,
   valoracion DECIMAL(3,1) DEFAULT 0.0 CHECK (valoracion BETWEEN 0.5 AND 5.0)
) ENGINE=InnoDB;

-- TABLA RESEÑAS
CREATE TABLE resena (
   id_resena INT AUTO_INCREMENT PRIMARY KEY,
   valoracion DECIMAL(3,1) NOT NULL CHECK (valoracion BETWEEN 0.5 AND 5.0),
   texto TEXT,
   id_usuario INT NOT NULL,
   id_pelicula INT NOT NULL,
   fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   UNIQUE (id_usuario, id_pelicula),
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
   FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
) ENGINE=InnoDB;

-- TABLA LISTA DE PELICULAS DEL USUARIO
CREATE TABLE lista (
   id_lista INT AUTO_INCREMENT PRIMARY KEY,
   id_usuario INT NOT NULL,
   nombre_lista VARCHAR(100) DEFAULT 'Mi Lista',
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- TABLA PELICULAS QUE HAY EN LA LISTA
CREATE TABLE lista_pelicula (
   id_lista INT NOT NULL,
   id_pelicula INT NOT NULL,
   PRIMARY KEY (id_lista, id_pelicula),
   FOREIGN KEY (id_lista) REFERENCES lista(id_lista) ON DELETE CASCADE,
   FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
) ENGINE=InnoDB;

-- TABLA PELICULAS ALQUILADAS
CREATE TABLE alquiler (
   id_usuario INT NOT NULL,
   id_pelicula INT NOT NULL,
   fecha_alquiler TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   fecha_devolucion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   precio DECIMAL(6,2) NOT NULL,
   PRIMARY KEY (id_usuario, id_pelicula),
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
   FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
) ENGINE=InnoDB;
