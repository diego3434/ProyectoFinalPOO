# Sistema Tienda de Ropa — Proyecto JavaFX (POO)
**EPN – ESFOT | Programación Orientada a Objetos**


Aplicación de escritorio JavaFX para venta de ropa, con 4 roles de usuario
(Administrador, Cajero, Reportes, Comprador), pantalla única de dashboard
reutilizada, conexión a PostgreSQL y CRUD completo. Cubre los numerales
1 al 8 de la especificación. El numeral 9 (Base de Datos) se entrega
como script SQL en la carpeta `/sql`.

## Novedades de esta versión

- **Configuración del sistema con persistencia real**: nombre de empresa,
  dirección, teléfono y logo se guardan en la tabla `configuracion` y se
  reflejan automáticamente en el Login, en la barra superior del
  Dashboard y en las facturas generadas — sin necesidad de reiniciar la
  aplicación (gracias al singleton `AppConfig`).
- **Selector de logo**: en Configuración, el Administrador puede subir
  una imagen (`.png`/`.jpg`) que reemplaza el logo en todo el sistema.
- **Nuevas opciones para el Cajero**: además de `Prendas`, ahora tiene
  `Vender` (registrar una venta con datos simples de cliente y generar
  su factura) y `Facturas` (historial de facturas, con opción de
  reimprimir).
- **Reportes ahora entra directo a su pantalla de solo lectura**
  (ya no pasa primero por Prendas).
- **Nuevo rol Comprador**: solo ve el catálogo con stock disponible,
  elige una prenda y cantidad, ingresa datos simples (nombre/cédula) y
  genera un **pedido**. Ese pedido queda "PENDIENTE" hasta que un
  Cajero lo revisa (pantalla `Vender`) y genera la factura real,
  descontando el stock en ese momento.

## Estructura de paquetes

```
com.ropa.model       -> Persona (abstracta), Usuario, Prenda, Venta,
                         Configuracion, Pedido
com.ropa.dao         -> ICRUD<T>, UsuarioDAO, PrendaDAO, VentaDAO,
                         ConfiguracionDAO, PedidoDAO
com.ropa.controller  -> LoginController, DashboardController,
                         PrendaController, UsuarioController,
                         ReportesController, ConfiguracionController,
                         VentaController, FacturaController,
                         CompraController, UsuarioAware (interfaz de apoyo)
com.ropa.db          -> Conexion (Singleton)
com.ropa.app         -> Main (entry point), AppConfig (Singleton de marca)
```

## Cómo aplican los 4 pilares de POO

- **Encapsulamiento**: todos los atributos de `model` son privados, acceso
  solo por getters/setters.
- **Herencia**: `Usuario extends Persona`.
- **Polimorfismo**: `Usuario` sobreescribe `describir()`; cada DAO
  implementa `ICRUD<T>` a su manera.
- **Abstracción**: `Persona` es abstracta, `ICRUD<T>` es una interfaz
  (contrato) que cada DAO debe cumplir.

## Requisitos previos

1. **JDK 17+** instalado.
2. **IntelliJ IDEA** con soporte Maven.
3. **PostgreSQL** instalado y corriendo (puerto 5432 por defecto).
4. Plugin de JavaFX para IntelliJ no es necesario si usas Maven
   (las dependencias están en `pom.xml`).

   ## Flujo Comprador → Cajero (pedidos y facturación)

1. `comprador1` inicia sesión, ve el catálogo, selecciona una prenda con
   stock disponible, indica cantidad y sus datos, y presiona
   **Generar Pedido**. Esto crea una fila en la tabla `pedidos` con
   estado `PENDIENTE` (no descuenta stock todavía).
2. `cajero1` inicia sesión, entra a **Vender**, y en la tabla de
   "Pedidos pendientes" ve ese pedido. Lo selecciona y presiona
   **Facturar pedido seleccionado**.
3. En ese momento se genera la factura real (tabla `ventas`, con su
   número `FAC-XXXXX`), se descuenta el stock de la prenda y el pedido
   pasa a estado `FACTURADO`.
4. Tanto el Cajero como el Administrador pueden ver el historial
   completo en **Facturas**, con opción de **Reimprimir**.

   ## Grafica de estructura de codigo

   
```
C:.
│   ProyectoFinalPOO.zip
│   VentaRopaAppmejor.zip
│   
├───proyectofinal
│   │   FAC-00001.txt
│   │   FAC-00003.txt
│   │   FAC-00004.txt
│   │   pom.xml
│   │   README.md
│   │   
│   ├───.idea
│   │       .gitignore
│   │       .name
│   │       compiler.xml
│   │       encodings.xml
│   │       jarRepositories.xml
│   │       misc.xml
│   │       workspace.xml
│   │       
│   ├───sql
│   │       script_tienda_ropa_postgres.sql
│   │       
│   ├───src
│   │   └───main
│   │       ├───java
│   │       │   └───com
│   │       │       └───ropa
│   │       │           ├───app
│   │       │           │       AppConfig.java
│   │       │           │       Main.java
│   │       │           │       
│   │       │           ├───controller
│   │       │           │       CompraController.java
│   │       │           │       ConfiguracionController.java
│   │       │           │       DashboardController.java
│   │       │           │       FacturaController.java
│   │       │           │       LoginController.java
│   │       │           │       PrendaController.java
│   │       │           │       ReportesController.java
│   │       │           │       UsuarioAware.java
│   │       │           │       UsuarioController.java
│   │       │           │       VentaController.java
│   │       │           │       
│   │       │           ├───dao
│   │       │           │       ConfiguracionDAO.java
│   │       │           │       ICRUD.java
│   │       │           │       PedidoDAO.java
│   │       │           │       PrendaDAO.java
│   │       │           │       UsuarioDAO.java
│   │       │           │       VentaDAO.java
│   │       │           │       
│   │       │           ├───db
│   │       │           │       Conexion.java
│   │       │           │       
│   │       │           └───model
│   │       │                   Configuracion.java
│   │       │                   Pedido.java
│   │       │                   Persona.java
│   │       │                   Prenda.java
│   │       │                   Usuario.java
│   │       │                   Venta.java
│   │       │                   
│   │       └───resources
│   │           ├───css
│   │           │       styles.css
│   │           │       
│   │           ├───img
│   │           │       logo.png
│   │           │       
│   │           └───view
│   │                   compra.fxml
│   │                   configuracion.fxml
│   │                   dashboard.fxml
│   │                   facturas.fxml
│   │                   login.fxml
│   │                   prendas.fxml
│   │                   reportes.fxml
│   │                   usuarios.fxml
│   │                   ventas.fxml
│   │                   
│   └───target
│       ├───classes
│       │   ├───com
│       │   │   └───ropa
│       │   │       ├───app
│       │   │       │       AppConfig.class
│       │   │       │       Main.class
│       │   │       │       
│       │   │       ├───controller
│       │   │       │       CompraController.class
│       │   │       │       ConfiguracionController.class
│       │   │       │       DashboardController.class
│       │   │       │       FacturaController.class
│       │   │       │       LoginController.class
│       │   │       │       PrendaController.class
│       │   │       │       ReportesController.class
│       │   │       │       UsuarioAware.class
│       │   │       │       UsuarioController.class
│       │   │       │       VentaController.class
│       │   │       │       
│       │   │       ├───dao
│       │   │       │       ConfiguracionDAO.class
│       │   │       │       ICRUD.class
│       │   │       │       PedidoDAO.class
│       │   │       │       PrendaDAO.class
│       │   │       │       UsuarioDAO.class
│       │   │       │       VentaDAO.class
│       │   │       │       
│       │   │       ├───db
│       │   │       │       Conexion.class
│       │   │       │       
│       │   │       └───model
│       │   │               Configuracion.class
│       │   │               Pedido.class
│       │   │               Persona.class
│       │   │               Prenda.class
│       │   │               Usuario.class
│       │   │               Venta.class
│       │   │               
│       │   ├───css
│       │   │       styles.css
│       │   │       
│       │   ├───img
│       │   │       logo.png
│       │   │       
│       │   └───view
│       │           compra.fxml
│       │           configuracion.fxml
│       │           dashboard.fxml
│       │           facturas.fxml
│       │           login.fxml
│       │           prendas.fxml
│       │           reportes.fxml
│       │           usuarios.fxml
│       │           ventas.fxml
│       │           
│       ├───generated-sources
│       │   └───annotations
│       └───maven-status
│           └───maven-compiler-plugin
│               └───compile
│                   └───default-compile
│                           createdFiles.lst
│                           inputFiles.lst
│                           
└───ProyectoFinalPOO
    │   .gitattributes
    │   dependency-reduced-pom.xml
    │   FAC-00001.txt
    │   FAC-00003.txt
    │   FAC-00004.txt
    │   pom.xml
    │   README.md
    │   reporte_ventas.csv
    │   
    ├───.idea
    │       .gitignore
    │       .name
    │       compiler.xml
    │       encodings.xml
    │       jarRepositories.xml
    │       misc.xml
    │       workspace.xml
    │       
    ├───sql
    │       script_tienda_ropa_postgres.sql
    │       
    ├───src
    │   └───main
    │       ├───java
    │       │   └───com
    │       │       └───ropa
    │       │           ├───app
    │       │           │       AppConfig.java
    │       │           │       Launcher.java
    │       │           │       Main.java
    │       │           │       
    │       │           ├───controller
    │       │           │       CompraController.java
    │       │           │       ConfiguracionController.java
    │       │           │       DashboardController.java
    │       │           │       FacturaController.java
    │       │           │       LoginController.java
    │       │           │       PrendaController.java
    │       │           │       ReportesController.java
    │       │           │       UsuarioAware.java
    │       │           │       UsuarioController.java
    │       │           │       VentaController.java
    │       │           │       
    │       │           ├───dao
    │       │           │       ConfiguracionDAO.java
    │       │           │       ICRUD.java
    │       │           │       PedidoDAO.java
    │       │           │       PrendaDAO.java
    │       │           │       UsuarioDAO.java
    │       │           │       VentaDAO.java
    │       │           │       
    │       │           ├───db
    │       │           │       Conexion.java
    │       │           │       
    │       │           └───model
    │       │                   Configuracion.java
    │       │                   Pedido.java
    │       │                   Persona.java
    │       │                   Prenda.java
    │       │                   Usuario.java
    │       │                   Venta.java
    │       │                   
    │       └───resources
    │           ├───css
    │           │       styles.css
    │           │       
    │           ├───img
    │           │       logo.png
    │           │       
    │           └───view
    │                   compra.fxml
    │                   configuracion.fxml
    │                   dashboard.fxml
    │                   facturas.fxml
    │                   login.fxml
    │                   prendas.fxml
    │                   reportes.fxml
    │                   usuarios.fxml
    │                   ventas.fxml
    │                   
    └───target
        │   original-VentaRopaApp.jar
        │   VentaRopaApp.jar
        │   
        ├───classes
        │   ├───com
        │   │   └───ropa
        │   │       ├───app
        │   │       │       AppConfig.class
        │   │       │       Launcher.class
        │   │       │       Main.class
        │   │       │       
        │   │       ├───controller
        │   │       │       CompraController.class
        │   │       │       ConfiguracionController.class
        │   │       │       DashboardController.class
        │   │       │       FacturaController.class
        │   │       │       LoginController.class
        │   │       │       PrendaController.class
        │   │       │       ReportesController.class
        │   │       │       UsuarioAware.class
        │   │       │       UsuarioController.class
        │   │       │       VentaController.class
        │   │       │       
        │   │       ├───dao
        │   │       │       ConfiguracionDAO.class
        │   │       │       ICRUD.class
        │   │       │       PedidoDAO.class
        │   │       │       PrendaDAO.class
        │   │       │       UsuarioDAO.class
        │   │       │       VentaDAO.class
        │   │       │       
        │   │       ├───db
        │   │       │       Conexion.class
        │   │       │       
        │   │       └───model
        │   │               Configuracion.class
        │   │               Pedido.class
        │   │               Persona.class
        │   │               Prenda.class
        │   │               Usuario.class
        │   │               Venta.class
        │   │               
        │   ├───css
        │   │       styles.css
        │   │       
        │   ├───img
        │   │       logo.png
        │   │       
        │   └───view
        │           compra.fxml
        │           configuracion.fxml
        │           dashboard.fxml
        │           facturas.fxml
        │           login.fxml
        │           prendas.fxml
        │           reportes.fxml
        │           usuarios.fxml
        │           ventas.fxml
        │           
        ├───generated-sources
        │   └───annotations
        ├───maven-archiver
        │       pom.properties
        │       
        └───maven-status
            └───maven-compiler-plugin
                └───compile
                    └───default-compile
                            createdFiles.lst
                            inputFiles.lst
```
    
