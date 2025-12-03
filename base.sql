CREATE DATABASE IF NOT EXISTS BDMEDIAWESECO;
USE BDMEDIAWESECO;

DROP TABLE IF EXISTS usuario_roles;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE usuarios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(200) NOT NULL UNIQUE,
  username VARCHAR(50) UNIQUE,          
  imagen_url VARCHAR(500),
  fecha_nacimiento DATE,
  password VARCHAR(255),
  verification_code VARCHAR(64),
  provider VARCHAR(20) DEFAULT 'LOCAL',
  provider_id VARCHAR(255),
  esta_activo BOOLEAN NOT NULL DEFAULT TRUE, #para ver si puede loguearse o no
  esta_verificado BOOLEAN NOT NULL DEFAULT FALSE #si su correo esta verificado
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE usuario_roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  rol_id INT NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE,
  UNIQUE KEY uk_usuario_rol (usuario_id, rol_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO roles (nombre) VALUES ('ROLE_USUARIO');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');



#-------------------------------- pasando al contenico ----------------------------------


CREATE TABLE generos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE tipos_contenido (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE contenido (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  slug VARCHAR(255) UNIQUE NOT NULL, -- Para la URL del navegador
  descripcion TEXT,
  anio_lanzamiento INT,
  
  -- Imágenes (Simplificado para rendimiento)
  img_poster VARCHAR(255), -- Vertical
  img_banner VARCHAR(255), -- Horizontal
  
  -- Negocio
  precio DECIMAL(10, 2) DEFAULT 0.00, -- Si es > 0, se puede comprar individualmente
  
  -- Relaciones y Estado
  tipo_contenido_id INT NOT NULL,
  esta_activo BOOLEAN NOT NULL DEFAULT TRUE,
  
  FOREIGN KEY (tipo_contenido_id) REFERENCES tipos_contenido(id),
  INDEX idx_titulo (titulo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Relación Contenido <-> Generos
CREATE TABLE contenido_genero (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  contenido_id BIGINT NOT NULL,
  genero_id INT NOT NULL,
  FOREIGN KEY (contenido_id) REFERENCES contenido(id) ON DELETE CASCADE,
  FOREIGN KEY (genero_id) REFERENCES generos(id) ON DELETE CASCADE,
  UNIQUE KEY uk_contenido_genero (contenido_id, genero_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO tipos_contenido (nombre) VALUES ('PELICULA');
INSERT INTO tipos_contenido (nombre) VALUES ('SERIE');
INSERT INTO tipos_contenido (nombre) VALUES ('CANAL');


CREATE TABLE peliculas (
  id BIGINT PRIMARY KEY, -- NO es auto_increment. Es el mismo ID que en 'contenido'
  duracion_minutos INT,
  url_stream VARCHAR(1024), -- URL del m3u8 o mp4
  formato_stream VARCHAR(10) DEFAULT 'HLS', -- Ej: HLS, MP4, DASH
  
  -- Este ID es la llave que conecta con el padre
  FOREIGN KEY (id) REFERENCES contenido(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#-----------------------------------series-------------------------------------------
-- 1. TABLA HIJA: SERIES (Hereda de 'contenido')
-- Solo guarda datos globales de la serie
CREATE TABLE series (
  id BIGINT PRIMARY KEY, -- Mismo ID que en 'contenido'
  estado_serie VARCHAR(20) DEFAULT 'FINALIZADA', -- Ej: 'EMISION', 'FINALIZADA', 'CANCELADA'
  total_temporadas INT DEFAULT 0, -- Campo contador útil para el frontend
  FOREIGN KEY (id) REFERENCES contenido(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. TABLA: TEMPORADAS
-- Pertenece a una Serie
CREATE TABLE temporadas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  serie_id BIGINT NOT NULL,
  numero_temporada INT NOT NULL, -- Ej: 1, 2, 3
  titulo VARCHAR(150), -- Opcional, a veces tienen nombre (ej: "Book 1: Water")
  fecha_estreno DATE,
  
  FOREIGN KEY (serie_id) REFERENCES series(id) ON DELETE CASCADE,
  -- Evitar duplicados: No puede haber dos temporadas "1" en la misma serie
  UNIQUE KEY uk_serie_temporada (serie_id, numero_temporada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. TABLA: EPISODIOS
-- Pertenece a una Temporada. Aquí está el VIDEO.
CREATE TABLE episodios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  temporada_id BIGINT NOT NULL,
  numero_episodio INT NOT NULL, -- Ej: 1, 2...
  titulo VARCHAR(200) NOT NULL,
  descripcion TEXT,
  duracion_minutos INT,
  url_stream VARCHAR(1024) NOT NULL, -- El video del capítulo
  img_miniatura VARCHAR(255), -- Thumbnail específico del episodio
  
  FOREIGN KEY (temporada_id) REFERENCES temporadas(id) ON DELETE CASCADE,
  -- Evitar duplicados: No puede haber dos episodios "1" en la misma temporada
  UNIQUE KEY uk_temporada_episodio (temporada_id, numero_episodio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE canales (
  id BIGINT PRIMARY KEY, -- Hereda de 'contenido'
  url_stream VARCHAR(1024) NOT NULL, -- El link m3u8 en vivo
  tipo_transmision VARCHAR(20) DEFAULT 'HLS', -- HLS, DASH, RTMP
  pais VARCHAR(50), -- Ej: España, México, Internacional
  FOREIGN KEY (id) REFERENCES contenido(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- 1. PLANES DE SUSCRIPCIÓN (El catálogo de planes)
CREATE TABLE planes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL, -- Ej: "Mensual", "Anual"
  precio DECIMAL(10, 2) NOT NULL,
  duracion_dias INT NOT NULL -- Ej: 30, 365
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO planes (nombre, precio, duracion_dias) VALUES ('Mensual', 9.99, 30);
INSERT INTO planes (nombre, precio, duracion_dias) VALUES ('Anual', 99.99, 365);

-- 2. SUSCRIPCIONES DE USUARIOS (Quién está suscrito)
CREATE TABLE suscripciones (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  plan_id INT NOT NULL,
  fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_fin DATETIME NOT NULL,
  esta_activa BOOLEAN DEFAULT TRUE, -- Para cancelar manualmente si hace falta
  
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id) REFERENCES planes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. CARRITO DE COMPRAS (Temporal)
CREATE TABLE items_carrito (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  contenido_id BIGINT NOT NULL,
  fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (contenido_id) REFERENCES contenido(id) ON DELETE CASCADE,
  UNIQUE KEY uk_carrito_item (usuario_id, contenido_id) -- No puedes tener la misma peli 2 veces en el carro
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. BIBLIOTECA (Propiedad Permanente)
-- Si está aquí, el usuario "es dueño" del contenido
CREATE TABLE biblioteca (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  contenido_id BIGINT NOT NULL,
  fecha_adquisicion DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (contenido_id) REFERENCES contenido(id) ON DELETE CASCADE,
  UNIQUE KEY uk_biblioteca_item (usuario_id, contenido_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. PEDIDOS (Historial de compras para auditoría)
CREATE TABLE pedidos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  total DECIMAL(10, 2) NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE detalle_pedido (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  pedido_id BIGINT NOT NULL,
  contenido_id BIGINT NOT NULL,
  precio_al_momento DECIMAL(10, 2) NOT NULL, -- Guardamos el precio histórico
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
  FOREIGN KEY (contenido_id) REFERENCES contenido(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO planes ( nombre, precio, duracion_dias) VALUES ( 'Mensual', 9.99, 30);
INSERT INTO planes ( nombre, precio, duracion_dias) VALUES ( 'Anual', 99.99, 365);


select * from usuarios;
select * from roles;
select * from usuario_roles;


DELETE FROM usuarios WHERE email = 'jacsonaulla@gmail.com';









