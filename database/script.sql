CREATE TYPE rol_usuario AS ENUM ('ADMIN', 'CAJERO', 'REPORTES');

CREATE TABLE usuarios (
                          id SERIAL PRIMARY KEY,
                          nombre VARCHAR(80) NOT NULL UNIQUE,
                          correo VARCHAR(120) NOT NULL,
                          contrasena VARCHAR(100) NOT NULL,
                          rol rol_usuario NOT NULL
);


CREATE TABLE prendas (
                         id SERIAL PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL UNIQUE,
                         categoria VARCHAR(50) NOT NULL,
                         talla VARCHAR(10) NOT NULL,
                         color VARCHAR(30) NOT NULL,
                         precio NUMERIC(10,2) NOT NULL CHECK (precio > 0),
                         stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0)
);


CREATE TABLE ventas (
                        id SERIAL PRIMARY KEY,
                        id_usuario INT NOT NULL REFERENCES usuarios(id),
                        id_prenda INT NOT NULL REFERENCES prendas(id),
                        cantidad INT NOT NULL CHECK (cantidad > 0),
                        total NUMERIC(10,2) NOT NULL,
                        fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO usuarios (nombre, correo, contrasena, rol) VALUES
                                                           ('admin',     'admin@tiendaropa.com',     'admin123',    'ADMIN'),
                                                           ('cajero1',   'cajero1@tiendaropa.com',   'cajero123',   'CAJERO'),
                                                           ('reportes1', 'reportes1@tiendaropa.com', 'reportes123', 'REPORTES');

INSERT INTO prendas (nombre, categoria, talla, color, precio, stock) VALUES
                                                                         ('Camisa Casual Slim',       'Camisas',    'M',  'Blanco',    18.50, 30),
                                                                         ('Pantalón Jean Clásico',    'Pantalones', 'L',  'Azul',      25.00, 20),
                                                                         ('Vestido Floral Verano',    'Vestidos',   'S',  'Estampado', 32.00, 15),
                                                                         ('Zapatos Deportivos Urban', 'Zapatos',    '42', 'Negro',     45.00, 12),
                                                                         ('Chaqueta Impermeable',     'Chaquetas',  'XL', 'Verde',     55.00, 8);


INSERT INTO ventas (id_usuario, id_prenda, cantidad, total, fecha) VALUES
                                                                       (2, 1, 2, 37.00, NOW()),
                                                                       (2, 2, 1, 25.00, NOW()),
                                                                       (2, 3, 3, 96.00, NOW());
