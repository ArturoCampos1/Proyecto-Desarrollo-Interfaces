-- TABLA ADMINISTRADOR
CREATE TABLE administrador (
   id_admin INT AUTO_INCREMENT PRIMARY KEY,
   usuario VARCHAR(100) NOT NULL UNIQUE,
   contrasena VARCHAR(100) NOT NULL
);

-- TABLA USUARIO
CREATE TABLE usuario (
   id_usuario INT AUTO_INCREMENT PRIMARY KEY,
   nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
   correo VARCHAR(150) NOT NULL UNIQUE,
   num_tel VARCHAR(20) UNIQUE,
   contrasena VARCHAR(100) NOT NULL
);

-- TABLA PELICULA
CREATE TABLE pelicula (
   id_pelicula INT AUTO_INCREMENT PRIMARY KEY,
   titulo VARCHAR(155) NOT NULL,
   anio_salida YEAR NOT NULL,
   director VARCHAR(95) NOT NULL,
   resumen TEXT,
   genero ENUM(
       'ACCION', 'AVENTURA', 'ANIMACION', 'COMEDIA', 'CRIMEN',
       'DOCUMENTAL', 'DRAMA', 'FAMILIAR', 'FANTASIA', 'HISTORIA',
       'TERROR', 'MUSICA', 'MISTERIO', 'ROMANCE', 'CIENCIA_FICCION',
       'PELICULA_DE_TV', 'SUSPENSO', 'BELICA', 'OESTE'
   ) NOT NULL,
   disponible BOOLEAN DEFAULT TRUE,
   valoracion DECIMAL(3,1) DEFAULT 0.0 CHECK (valoracion BETWEEN 0.5 AND 5 AND (valoracion * 2) = FLOOR(valoracion * 2))
);

-- TABLA RESEÑAS
CREATE TABLE resena (
   id_resena INT AUTO_INCREMENT PRIMARY KEY,
   valoracion DECIMAL(3,1) NOT NULL CHECK (valoracion BETWEEN 0.5 AND 5 AND (valoracion * 2) = FLOOR(valoracion * 2)),
   texto TEXT,
   id_usuario INT NOT NULL,
   id_pelicula INT NOT NULL,
   fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   UNIQUE (id_usuario, id_pelicula),
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
   FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
);

-- TABLA LISTA DE PELICULAS DEL USUARIO
CREATE TABLE lista (
   id_lista INT AUTO_INCREMENT PRIMARY KEY,
   id_usuario INT NOT NULL,
   nombre_lista VARCHAR(100) DEFAULT 'Mi Lista',
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- TABLA PELICULAS QUE HAY EN LA LISTA
CREATE TABLE lista_pelicula (
   id_lista INT NOT NULL,
   id_pelicula INT NOT NULL,
   PRIMARY KEY (id_lista, id_pelicula),
   FOREIGN KEY (id_lista) REFERENCES lista(id_lista) ON DELETE CASCADE,
   FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
);

-- TABLA PELICULAS ALQUILADAS
CREATE TABLE alquiler (
   id_usuario INT NOT NULL,
   id_pelicula INT NOT NULL,
   fecha_alquiler DATETIME DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id_usuario, id_pelicula),
   FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
   FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula) ON DELETE CASCADE
);