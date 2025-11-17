CREATE DATABASE IF NOT EXISTS basepelis;
USE basepelis;

DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  apellido VARCHAR(50) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(200) NOT NULL UNIQUE,
  rol ENUM('ADMIN', 'USUARIO') DEFAULT 'USUARIO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS peliculas;

CREATE TABLE peliculas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  descripcion TEXT,
  anio_lanzamiento INT,
  duracion_minutos INT,
  disponible BOOLEAN DEFAULT TRUE,
  precio_comprar DECIMAL(10,2) CHECK (precio_comprar >= 0),
  img_frente VARCHAR(255),
  url_stream VARCHAR(1024)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS imagenes_pelicula;

CREATE TABLE imagenes_pelicula (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  pelicula_id BIGINT NOT NULL,
  url_imagen VARCHAR(255) NOT NULL,
  FOREIGN KEY (pelicula_id) REFERENCES peliculas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS generos;

CREATE TABLE generos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS pelicula_genero;

CREATE TABLE pelicula_genero (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  pelicula_id BIGINT NOT NULL,
  genero_id BIGINT NOT NULL,
  FOREIGN KEY (pelicula_id) REFERENCES peliculas(id) ON DELETE CASCADE,
  FOREIGN KEY (genero_id) REFERENCES generos(id) ON DELETE CASCADE,
  UNIQUE KEY unique_pelicula_genero (pelicula_id, genero_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS usuario_pelicula;

CREATE TABLE usuario_pelicula (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  pelicula_id BIGINT NOT NULL,
  fecha_compra DATETIME NOT NULL,
  precio_compra DECIMAL(10,2) NOT NULL CHECK (precio_compra >= 0),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (pelicula_id) REFERENCES peliculas(id) ON DELETE CASCADE,
  UNIQUE KEY unique_usuario_pelicula (usuario_id, pelicula_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS suscripciones;

CREATE TABLE suscripciones (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  fecha_inicio DATETIME NOT NULL,
  fecha_fin DATETIME,
  estado ENUM('ACTIVA', 'CANCELADA', 'EXPIRADA') DEFAULT 'ACTIVA',
  precio_pagado DECIMAL(10,2) NOT NULL CHECK (precio_pagado >= 0),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  CHECK (fecha_fin IS NULL OR fecha_inicio < fecha_fin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS orden_compra;

CREATE TABLE orden_compra (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  estado ENUM('PENDIENTE', 'COMPLETADA', 'CANCELADA') DEFAULT 'PENDIENTE',
  fecha_compra DATETIME NOT NULL,
  total_pagado DECIMAL(10,2) NOT NULL CHECK (total_pagado >= 0),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS detalle_orden;

CREATE TABLE detalle_orden (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  orden_id BIGINT NOT NULL,
  pelicula_id BIGINT NOT NULL,
  precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
  FOREIGN KEY (orden_id) REFERENCES orden_compra(id) ON DELETE CASCADE,
  FOREIGN KEY (pelicula_id) REFERENCES peliculas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DELIMITER $$

-- Prevenir múltiples suscripciones activas (INSERT)
CREATE TRIGGER trg_prevenir_multiples_suscripciones_antes_insertar
BEFORE INSERT ON suscripciones
FOR EACH ROW
BEGIN
  IF NEW.estado = 'ACTIVA' THEN
    IF (SELECT COUNT(*) FROM suscripciones s WHERE s.usuario_id = NEW.usuario_id AND s.estado = 'ACTIVA') > 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario ya tiene una suscripción ACTIVA';
    END IF;
  END IF;
END$$

-- Prevenir múltiples suscripciones activas (UPDATE)
CREATE TRIGGER trg_prevenir_multiples_suscripciones_antes_actualizar
BEFORE UPDATE ON suscripciones
FOR EACH ROW
BEGIN
  IF NEW.estado = 'ACTIVA' AND (NEW.estado <> OLD.estado) THEN
    IF (SELECT COUNT(*) FROM suscripciones s WHERE s.usuario_id = NEW.usuario_id AND s.estado = 'ACTIVA' AND s.id <> NEW.id) > 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario ya tiene otra suscripción ACTIVA';
    END IF;
  END IF;
END$$

-- Agregar a biblioteca cuando orden se completa
CREATE TRIGGER trg_agregar_a_biblioteca_al_completar_orden
AFTER UPDATE ON orden_compra
FOR EACH ROW
BEGIN
  IF NEW.estado = 'COMPLETADA' AND OLD.estado != 'COMPLETADA' THEN
    INSERT INTO usuario_pelicula (usuario_id, pelicula_id, fecha_compra, precio_compra)
    SELECT 
      NEW.usuario_id,
      do.pelicula_id,
      NEW.fecha_compra,
      do.precio
    FROM detalle_orden do
    WHERE do.orden_id = NEW.id
    ON DUPLICATE KEY UPDATE 
      fecha_compra = VALUES(fecha_compra),
      precio_compra = VALUES(precio_compra);
  END IF;
END$$

DELIMITER ;

-- ============================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- ============================================

-- Índices en USUARIOS
CREATE INDEX idx_usuario_nombre ON usuarios(nombre);
CREATE INDEX idx_usuario_apellido ON usuarios(apellido);
CREATE INDEX idx_usuario_rol ON usuarios(rol);

-- Índices en PELICULAS
CREATE INDEX idx_pelicula_titulo ON peliculas(titulo);
CREATE INDEX idx_pelicula_disponible ON peliculas(disponible);
CREATE INDEX idx_pelicula_anio ON peliculas(anio_lanzamiento);
CREATE INDEX idx_pelicula_disponible_anio ON peliculas(disponible, anio_lanzamiento);

-- Índices en GENEROS
CREATE INDEX idx_genero_nombre ON generos(nombre);

-- Índices en ÓRDENES
CREATE INDEX idx_orden_estado ON orden_compra(estado);
CREATE INDEX idx_orden_fecha ON orden_compra(fecha_compra);
CREATE INDEX idx_orden_estado_fecha ON orden_compra(estado, fecha_compra);

-- Índices en SUSCRIPCIONES
CREATE INDEX idx_suscripcion_usuario ON suscripciones(usuario_id);
CREATE INDEX idx_suscripcion_estado ON suscripciones(estado);

-- Índices en USUARIO_PELICULA
CREATE INDEX idx_usuario_pelicula_usuario ON usuario_pelicula(usuario_id);
CREATE INDEX idx_usuario_pelicula_pelicula ON usuario_pelicula(pelicula_id);

-- ============================================
-- DATOS DE PRUEBA (OPCIONAL)
-- ============================================

-- Usuarios de prueba (passwords son hash de "123456")
INSERT INTO usuarios (nombre, apellido, username, password, email, rol) VALUES
('Admin', 'Sistema', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@pelis.com', 'ADMIN'),
('Juan', 'Pérez', 'jperez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'juan@test.com', 'USUARIO'),
('María', 'García', 'mgarcia', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'maria@test.com', 'USUARIO');

-- Géneros
INSERT INTO generos (nombre) VALUES
('Acción'), ('Comedia'), ('Drama'), ('Ciencia Ficción'), 
('Terror'), ('Romance'), ('Animación'), ('Documental'),
('Thriller'), ('Aventura');

-- Películas de ejemplo
INSERT INTO peliculas (titulo, descripcion, anio_lanzamiento, duracion_minutos, precio_comprar, disponible) VALUES
('Matrix', 'Un hacker descubre la verdad sobre su realidad y su papel en la guerra contra sus controladores', 1999, 136, 9.99, TRUE),
('Inception', 'Un ladrón que roba secretos corporativos a través del uso de la tecnología de compartir sueños', 2010, 148, 12.99, TRUE),
('Parasite', 'Codicia y discriminación de clases amenazan la relación recién formada entre la adinerada familia Park y el clan pobre de los Kim', 2019, 132, 11.99, TRUE),
('Interstellar', 'Un equipo de exploradores viaja a través de un agujero de gusano en el espacio', 2014, 169, 13.99, TRUE),
('The Dark Knight', 'Cuando la amenaza conocida como el Joker emerge de su misterioso pasado', 2008, 152, 10.99, TRUE);

-- Relaciones película-género
INSERT INTO pelicula_genero (pelicula_id, genero_id) VALUES
(1, 1), (1, 4),  -- Matrix: Acción, Ciencia Ficción
(2, 1), (2, 4), (2, 9),  -- Inception: Acción, Ciencia Ficción, Thriller
(3, 3), (3, 9),  -- Parasite: Drama, Thriller
(4, 4), (4, 10), (4, 3),  -- Interstellar: Ciencia Ficción, Aventura, Drama
(5, 1), (5, 3), (5, 9);  -- The Dark Knight: Acción, Drama, Thriller


select * from usuarios;
select * from peliculas;
select * from generos;
select * from imagenes_pelicula;
select * from orden_compra;
select * from detalle_orden;
select * from suscripciones;
select * from usuario_pelicula;


INSERT INTO peliculas (titulo, descripcion, anio_lanzamiento, duracion_minutos, disponible, precio_comprar, img_frente, url_stream) VALUES

('The Jack in the Box: El despertar', 'Terror 2022', 2022, 95, true, 3.99, 'https://image.tmdb.org/t/p/w500/3Ib8vlWTrAKRrTWUrTrZPOMW4jp.jpg', 'http://208.88.245.81/nuevo/jackinthebox2.eldespertar.dual.2023.mkv');

INSERT INTO peliculas (titulo, descripcion, anio_lanzamiento, duracion_minutos, disponible, precio_comprar, img_frente, url_stream) VALUES

('Un jefe en pañales 2', 'Comedia familiar 2021', 2021, 104, true, 3.99, 'https://image.tmdb.org/t/p/w500/kv2Qk9MKFFQo4WQPaYta599HkJP.jpg', 'https://objectstorage.sa-saopaulo-1.oraclecloud.com/n/grrfff66me7t/b/Cubojoselyn/o/pelicujulio%2FVer%20El%20bebe%20jefazo-%20Negocios%20de%20familia%20Online%20Castellano%20Latino%20Subtitulada%20HD%20-%20HDFull.mp4');

INSERT INTO peliculas (titulo, descripcion, anio_lanzamiento, duracion_minutos, disponible, precio_comprar, img_frente, url_stream) VALUES

('Black Widow', 'Acción/Superhéroes 2021', 2021, 134, true, 4.99, 'https://image.tmdb.org/t/p/w500/v1Wj5lzvsvFbXHdrmYGdXL10Q73.jpg', 'https://objectstorage.sa-saopaulo-1.oraclecloud.com/n/grrfff66me7t/b/Cubojoselyn/o/pelicujulio%2FVer%20Viuda%20negra%20Online%20Castellano%20Latino%20Subtitulada%20HD%20-%20HDFull.mp4'),

('tick, tick... Boom!', 'Drama/Musical 2021', 2021, 120, true, 3.99, 'https://image.tmdb.org/t/p/w500/DPmfcuR8fh8ROYXgdjrAjSGA0o.jpg', 'https://objectstorage.sa-saopaulo-1.oraclecloud.com/n/grrfff66me7t/b/Cubojoselyn/o/pelisnuee%2Fpelisnov%2F%E2%96%B7%20Ver%20tick%2C%20tick...%20Boom!%20(2021)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4'),

('A mi altura 2', 'Comedia/Romance 2022', 2022, 109, true, 3.99, 'https://image.tmdb.org/t/p/w500/eyKkLdst2vFRjCC89C3NqGCLpNE.jpg', 'https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvrlfgimket/b/cubostudio/o/peliculas%2Fpelisene%2F%E2%96%B7%20Ver%20A%20mi%20Altura%202%20(2022)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4'),

('El violín de mi padre', 'Drama/Familia 2022', 2022, 108, true, 3.99, 'https://image.tmdb.org/t/p/w500/bwvoSRyXRRqtpvoHYhySQk2U4EM.jpg', 'https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvrlfgimket/b/cubostudio/o/peliculas%2Fpelisene%2F%E2%96%B7%20Ver%20El%20Viol%C3%ADn%20de%20mi%20Padre%20(2022)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4'),

('La última gran estafa', 'Crimen/Thriller 2020', 2020, 122, true, 3.99, 'https://image.tmdb.org/t/p/w500/ublJIJbc2NZ30ptXj4O2d2apo1t.jpg', 'https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvrlfgimket/b/cubostudio/o/peliculas%2Fpelisene%2F%E2%96%B7%20Ver%20La%20%C3%9Altima%20Estafa%20(2020)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4'),

('Luz negra', 'Acción/Thriller 2022', 2022, 104, true, 3.99, 'https://image.tmdb.org/t/p/w500/hXUgpAB6CQNTfMZ6hCUtEM8rdVS.jpg', 'https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvrlfgimket/b/cubostudio/o/peliculas%2Fpelisene%2FVer%20Blacklight%20Online%20Castellano%20Latino%20Subtitulada%20HD%20-%20HDFull.mp4'),

('Clifford, el Gran Perro Rojo', 'Comedia/Familia 2021', 2021, 104, true, 3.99, 'https://image.tmdb.org/t/p/w500/oifhfVhUcuDjE61V5bS5dfShQrm.jpg', 'https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvrlfgimket/b/cubostudio/o/peliculas%2Fpelisene%2FVer%20Clifford%2C%20el%20Gran%20Perro%20Rojo%20(2021)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4'),

('Creed: Corazón de campeón', 'Drama/Deportes 2015', 2015, 133, true, 3.99, 'https://image.tmdb.org/t/p/w500/gwqR9UY0xqBZwP2qb8ZPmf9b2lq.jpg', 'https://objectstorage.us-ashburn-1.oraclecloud.com/n/idvrlfgimket/b/cubostudio/o/peliculas%2Fpelisfeb23%2FVer%20Creed-%20Coraz%C3%B3n%20de%20campe%C3%B3n%20(2015)%20Online%20Gratis%20en%20HD%20-%20InkaPelis.mp4'),

('Doce en casa', 'Comedia/Familia 2022', 2022, 110, true, 3.99, 'https://image.tmdb.org/t/p/w500/qNRsouZh5zmhaE3n4QpLDXzy1gQ.jpg', 'https://objectstorage.us-phoenix-1.oraclecloud.com/n/axa4wow3dcia/b/bucket-20201001-1658/o/2022pelicu%2F%5BPelicula%20Doce%20en%20casa%202022%5D%20Cheaper%20by%20the%20Dozen%20Ver%20Online%20%E2%80%94%20Cuevana%20Video.mp4'),

('El Rescate de Ruby', 'Drama/Familia 2022', 2022, 89, true, 3.99, 'https://image.tmdb.org/t/p/w500/tPlJEodEn0SSV4avo8KSawtlTlN.jpg', 'https://objectstorage.us-phoenix-1.oraclecloud.com/n/axa4wow3dcia/b/bucket-20201001-1658/o/2022pelicu%2F%E2%96%B7%20Ver%20El%20Rescate%20de%20Ruby%20(2022)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4'),

('Muerte en el Nilo', 'Misterio/Thriller 2022', 2022, 125, true, 4.99, 'https://image.tmdb.org/t/p/w500/kVr5zIAFSPRQ57Y1zE7KzmhzdMQ.jpg', 'https://objectstorage.us-phoenix-1.oraclecloud.com/n/axa4wow3dcia/b/bucket-20201001-1658/o/2022pelicu%2F%E2%96%B7%20Ver%20Muerte%20en%20el%20Nilo%20(2022)%20Pel%C3%ADcula%20Completa%20Online%20Latino.mp4');

INSERT INTO peliculas (titulo, descripcion, anio_lanzamiento, duracion_minutos, disponible, precio_comprar, img_frente, url_stream) VALUES

('Avatar 2: El sentido del agua', 'Ciencia Ficción/Aventura 2022', 2022, 192, true, 5.99, 'https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg', 'https://objectstorage.us-phoenix-1.oraclecloud.com/n/axa4wow3dcia/b/bucket-20201001-1658/o/2022pelicu%2Fdici%2FVer%20Avatar-%20El%20sentido%20del%20agua%20Online%20Castellano%20Latino%20Subtitulada%20HD%20-%20HDFull.mp4'),

('Dragon Ball Super: Super Hero', 'Animación/Acción 2022', 2022, 101, true, 4.99, 'https://image.tmdb.org/t/p/w500/rugyJdeoJm7cSJL1q4jBpTNbxyU.jpg', 'https://objectstorage.us-phoenix-1.oraclecloud.com/n/axa4wow3dcia/b/bucket-20201001-1658/o/2022pelicu%2Fagosto%2FVer%20Dragon%20Ball%20Super-%20Super%20Hero%20Online%20Castellano%20Latino%20Subtitulada%20HD%20-%20HDFull_2.mp4');

