package DAOimplents;

import DB.Conexion;
import model.Venta;
import DAO.Crud;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO implements Crud<Venta> {

    @Override
    public boolean guardar(Venta v) {
        String sql = "INSERT INTO ventas (id_usuario, id_prenda, cantidad, total, fecha, cliente_nombre, cliente_cedula) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, v.getIdUsuario());
            ps.setInt(2, v.getIdPrenda());
            ps.setInt(3, v.getCantidad());
            ps.setDouble(4, v.getTotal());
            ps.setTimestamp(5, Timestamp.valueOf(
                    v.getFecha() != null ? v.getFecha() : LocalDateTime.now()));
            ps.setString(6, v.getClienteNombre());
            ps.setString(7, v.getClienteCedula());
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
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }


    public Venta registrarVentaConFactura(Venta v) {
        String sqlInsert = "INSERT INTO ventas (id_usuario, id_prenda, cantidad, total, fecha, cliente_nombre, cliente_cedula) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlFactura = "UPDATE ventas SET numero_factura = ? WHERE id = ?";
        String sqlStock = "UPDATE prendas SET stock = stock - ? WHERE id = ? AND stock >= ?";

        Connection con = null;
        try {
            con = Conexion.getInstancia().getConnection();
            con.setAutoCommit(false);

            int idGenerado;
            try (PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, v.getIdUsuario());
                ps.setInt(2, v.getIdPrenda());
                ps.setInt(3, v.getCantidad());
                ps.setDouble(4, v.getTotal());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(6, v.getClienteNombre());
                ps.setString(7, v.getClienteCedula());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        con.rollback();
                        return null;
                    }
                    idGenerado = keys.getInt(1);
                }
            }

            // Descontar stock; si no hay suficiente, se revierte todo
            try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
                ps.setInt(1, v.getCantidad());
                ps.setInt(2, v.getIdPrenda());
                ps.setInt(3, v.getCantidad());
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    con.rollback();
                    return null; // no había stock suficiente
                }
            }

            String numeroFactura = "FAC-" + String.format("%05d", idGenerado);
            try (PreparedStatement ps = con.prepareStatement(sqlFactura)) {
                ps.setString(1, numeroFactura);
                ps.setInt(2, idGenerado);
                ps.executeUpdate();
            }

            con.commit();
            v.setId(idGenerado);
            v.setNumeroFactura(numeroFactura);
            v.setFecha(LocalDateTime.now());
            return v;
        } catch (SQLException e) {
            System.err.println("Error al registrar la venta/factura: " + e.getMessage());
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return null;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error al restaurar autoCommit: " + ex.getMessage());
            }
        }
    }


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

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id"));
        v.setIdUsuario(rs.getInt("id_usuario"));
        v.setIdPrenda(rs.getInt("id_prenda"));
        v.setCantidad(rs.getInt("cantidad"));
        v.setTotal(rs.getDouble("total"));
        v.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        v.setNombreUsuario(rs.getString("nombre_usuario"));
        v.setNombrePrenda(rs.getString("nombre_prenda"));
        v.setClienteNombre(rs.getString("cliente_nombre"));
        v.setClienteCedula(rs.getString("cliente_cedula"));
        v.setNumeroFactura(rs.getString("numero_factura"));
        return v;
    }
}