package DAOimplents;

import DB.Conexion;
import model.Venta;
import DAO.Crud;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO implements Crud<Venta> {

    @Override
    public boolean guardar(Venta v) {
        String sql = "INSERT INTO ventas (id_usuario, id_prenda, cantidad, total, fecha) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, v.getIdUsuario());
            ps.setInt(2, v.getIdPrenda());
            ps.setInt(3, v.getCantidad());
            ps.setDouble(4, v.getTotal());
            ps.setTimestamp(5, Timestamp.valueOf(
                    v.getFecha() != null ? v.getFecha() : LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Venta v) {
        String sql = "UPDATE ventas SET id_usuario=?, id_prenda=?, cantidad=?, total=? WHERE id=?";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, v.getIdUsuario());
            ps.setInt(2, v.getIdPrenda());
            ps.setInt(3, v.getCantidad());
            ps.setDouble(4, v.getTotal());
            ps.setInt(5, v.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM ventas WHERE id = ?";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();
        // JOIN para traer nombre de usuario y de prenda ya legibles para el TableView
        String sql = "SELECT v.*, u.nombre AS nombre_usuario, p.nombre AS nombre_prenda " +
                "FROM ventas v " +
                "JOIN usuarios u ON v.id_usuario = u.id " +
                "JOIN prendas p ON v.id_prenda = p.id " +
                "ORDER BY v.id";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id"));
                v.setIdUsuario(rs.getInt("id_usuario"));
                v.setIdPrenda(rs.getInt("id_prenda"));
                v.setCantidad(rs.getInt("cantidad"));
                v.setTotal(rs.getDouble("total"));
                v.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                v.setNombreUsuario(rs.getString("nombre_usuario"));
                v.setNombrePrenda(rs.getString("nombre_prenda"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }

    /** Total vendido, usado en el módulo de Reportes. */
    public double totalVentas() {
        String sql = "SELECT COALESCE(SUM(total), 0) AS suma FROM ventas";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("suma");
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular total de ventas: " + e.getMessage());
        }
        return 0;
    }
}