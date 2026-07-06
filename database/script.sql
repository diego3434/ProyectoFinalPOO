--Crear base de datos
CREATE DATABASE tienda_ropa;

--Crear tabla de usuarios

CREATE TABLE usuarios (
                          id SERIAL PRIMARY KEY,
                          usuario VARCHAR(50) NOT NULL UNIQUE,
                          clave VARCHAR(255) NOT NULL,
                          rol VARCHAR(20) NOT NULL,
                          estado BOOLEAN DEFAULT TRUE
);

--Crear tabla de clientes

CREATE TABLE clientes (
                          id SERIAL PRIMARY KEY,
                          cedula VARCHAR(13) UNIQUE,
                          nombres VARCHAR(100) NOT NULL,
                          apellidos VARCHAR(100) NOT NULL,
                          telefono VARCHAR(20),
                          correo VARCHAR(100),
                          direccion TEXT
);

--Crear categorias

CREATE TABLE categorias (
                            id SERIAL PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL UNIQUE
);

--Crwar taba productos

CREATE TABLE productos (
                           id SERIAL PRIMARY KEY,
                           codigo VARCHAR(30) UNIQUE NOT NULL,
                           nombre VARCHAR(100) NOT NULL,
                           descripcion TEXT,
                           talla VARCHAR(10),
                           color VARCHAR(30),
                           precio NUMERIC(10,2) NOT NULL,
                           stock INTEGER NOT NULL DEFAULT 0,
                           categoria_id INTEGER REFERENCES categorias(id),
                           estado BOOLEAN DEFAULT TRUE
);

--Crear ventas

CREATE TABLE ventas (
                        id SERIAL PRIMARY KEY,
                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        cliente_id INTEGER REFERENCES clientes(id),
                        usuario_id INTEGER REFERENCES usuarios(id),
                        subtotal NUMERIC(10,2) NOT NULL,
                        iva NUMERIC(10,2) NOT NULL,
                        descuento NUMERIC(10,2) DEFAULT 0,
                        total NUMERIC(10,2) NOT NULL,
                        metodo_pago VARCHAR(30)
);


--Crear detalle_venta

CREATE TABLE detalle_venta (
                               id SERIAL PRIMARY KEY,
                               venta_id INTEGER NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
                               producto_id INTEGER NOT NULL REFERENCES productos(id),
                               cantidad INTEGER NOT NULL,
                               precio NUMERIC(10,2) NOT NULL,
                               subtotal NUMERIC(10,2) NOT NULL
);


