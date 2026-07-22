package DAOimplents;

import DAO.Crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DB.Conexion;
import model.Pedido;
public class PedidoDAO  {


    public boolean guardar(Pedido p) {
        String sql = "INSERT INTO pedidos (id_comprador, id_prenda, cantidad, cliente_nombre, cliente_cedula, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdComprador());
            ps.setInt(2, p.getIdPrenda());
            ps.setInt(3, p.getCantidad());
            ps.setString(4, p.getClienteNombre());
            ps.setString(5, p.getClienteCedula());
            ps.setString(6, Pedido.ESTADO_PENDIENTE);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar pedido: " + e.getMessage());
            return false;
        }
    }

    public List<Pedido> listarPorComprador(int idComprador) {
        String sql = "SELECT pe.*, p.nombre AS nombre_prenda, p.precio AS precio_unitario " +
                "FROM pedidos pe JOIN prendas p ON pe.id_prenda = p.id " +
                "WHERE pe.id_comprador = ? ORDER BY pe.id DESC";
        return listarConFiltro(sql, idComprador);
    }


    public List<Pedido> listarPendientes() {
        String sql = "SELECT pe.*, p.nombre AS nombre_prenda, p.precio AS precio_unitario " +
                "FROM pedidos pe JOIN prendas p ON pe.id_prenda = p.id " +
                "WHERE pe.estado = 'PENDIENTE' ORDER BY pe.id";
        return listarConFiltro(sql, null);
    }

    private List<Pedido> listarConFiltro(String sql, Integer idComprador) {
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (idComprador != null) {
                ps.setInt(1, idComprador);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("id"));
                    p.setIdComprador(rs.getInt("id_comprador"));
                    p.setIdPrenda(rs.getInt("id_prenda"));
                    p.setCantidad(rs.getInt("cantidad"));
                    p.setClienteNombre(rs.getString("cliente_nombre"));
                    p.setClienteCedula(rs.getString("cliente_cedula"));
                    p.setEstado(rs.getString("estado"));
                    p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    p.setNombrePrenda(rs.getString("nombre_prenda"));
                    p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    public boolean marcarFacturado(int idPedido) {
        String sql = "UPDATE pedidos SET estado = 'FACTURADO' WHERE id = ?";
        try (Connection con = Conexion.getInstancia().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del pedido: " + e.getMessage());
            return false;
        }
    }
}
