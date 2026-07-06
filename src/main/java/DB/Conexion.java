package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL     = "jdbc:postgresql://localhost:5433/natacion_db";
    private static final String USUARIO = "postgres";
    private static final String CLAVE   = "postgres";

    // Única instancia (Singleton)
    private static Conexion instancia;

    private Connection connection;

    // Constructor PRIVADO
    private Conexion() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("✔ Conexión exitosa a PostgreSQL.");
        } catch (ClassNotFoundException e) {
            System.err.println("✘ Driver PostgreSQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✘ Error al conectar con la BD: " + e.getMessage());
        }
    }

    // Retorna la única instancia
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConnection() {
        return connection;
    }

}


