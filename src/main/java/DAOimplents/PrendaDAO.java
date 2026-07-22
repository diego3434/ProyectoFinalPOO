package DAOimplents;

import DB.Conexion;
import model.Prenda;
import DAO.Crud;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrendaDAO implements Crud<Prenda> {

    @Override
    public boolean guardar(Prenda p) {
        String sql = "INSERT INTO prendas (nombre, categoria, talla, color, precio, stock) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getTalla());
            ps.setString(4, p.getColor());
            ps.setDouble(5, p.getPrecio());
            ps.setInt(6, p.getStock());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar prenda: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Prenda p) {
        String sql = "UPDATE prendas SET nombre=?, categoria=?, talla=?, color=?, precio=?, stock=? WHERE id=?";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getTalla());
            ps.setString(4, p.getColor());
            ps.setDouble(5, p.getPrecio());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar prenda: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM prendas WHERE id = ?";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar prenda: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Prenda> listar() {
        List<Prenda> lista = new ArrayList<>();
        String sql = "SELECT * FROM prendas ORDER BY id";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Prenda(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("talla"),
                        rs.getString("color"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prendas: " + e.getMessage());
        }
        return lista;
    }


    public List<Prenda> listarConStock() {
        List<Prenda> lista = new ArrayList<>();
        String sql = "SELECT * FROM prendas WHERE stock > 0 ORDER BY nombre";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Prenda(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("talla"),
                        rs.getString("color"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prendas con stock: " + e.getMessage());
        }
        return lista;
    }


    public boolean existeNombre(String nombre) {
        String sql = "SELECT id FROM prendas WHERE nombre = ?";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar nombre de prenda: " + e.getMessage());
            return false;
        }
    }
}