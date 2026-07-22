package DAOimplents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DB.Conexion;
import model.Configuracion;
public class ConfiguracionDAO {

    public Configuracion obtener() {
        String sql = "SELECT * FROM configuracion WHERE id = 1";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Configuracion(
                        rs.getInt("id"),
                        rs.getString("nombre_empresa"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("logo_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración: " + e.getMessage());
        }
        // Valores por defecto si aún no existe la fila en la BD
        return new Configuracion(1, "Tienda de Ropa", "", "", null);
    }

    public boolean actualizar(Configuracion c) {
        String sql = "UPDATE configuracion SET nombre_empresa = ?, direccion = ?, telefono = ?, logo_path = ? WHERE id = 1";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreEmpresa());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getLogoPath());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                // Si por algún motivo no existía la fila, la creamos
                return insertar(c);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración: " + e.getMessage());
            return false;
        }
    }

    private boolean insertar(Configuracion c) {
        String sql = "INSERT INTO configuracion (id, nombre_empresa, direccion, telefono, logo_path) VALUES (1, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreEmpresa());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getLogoPath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar configuración: " + e.getMessage());
            return false;
        }
    }
}
