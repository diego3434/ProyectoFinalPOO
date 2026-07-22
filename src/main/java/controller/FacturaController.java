package controller;

import DAOimplents.VentaDAO;
import app.AppConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Venta;
import model.Usuario;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class FacturaController implements UsuarioInterfaz{
    @FXML
    private TableView<Venta> tablaFacturas;
    @FXML private TableColumn<Venta, String> colNumeroFactura;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, String> colPrenda;
    @FXML private TableColumn<Venta, Integer> colCantidad;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, String> colCajero;

    private final VentaDAO ventaDAO = new VentaDAO();

    @Override
    public void setUsuarioSesion(Usuario usuario) {
        // Este módulo es de solo lectura + reimpresión, no distingue por rol
    }

    @FXML
    public void initialize() {
        colNumeroFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colPrenda.setCellValueFactory(new PropertyValueFactory<>("nombrePrenda"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCajero.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));

        ObservableList<Venta> lista = FXCollections.observableArrayList(ventaDAO.listar());
        tablaFacturas.setItems(lista);
    }

    @FXML
    private void reimprimir() {
        Venta v = tablaFacturas.getSelectionModel().getSelectedItem();
        if (v == null) {
            alerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona una factura de la tabla.");
            return;
        }

        String contenido = construirFactura(v);

        Alert alertaFactura = new Alert(Alert.AlertType.INFORMATION);
        alertaFactura.setTitle("Factura " + v.getNumeroFactura());
        alertaFactura.setHeaderText(null);
        TextArea area = new TextArea(contenido);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(420);
        area.setPrefHeight(280);
        alertaFactura.getDialogPane().setContent(area);
        alertaFactura.showAndWait();

        try (FileWriter fw = new FileWriter(v.getNumeroFactura() + ".txt")) {
            fw.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String construirFactura(Venta v) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        double precioUnitario = v.getCantidad() > 0 ? v.getTotal() / v.getCantidad() : 0;
        return AppConfig.getInstancia().getNombreEmpresa() + "\n" +
                AppConfig.getInstancia().getDireccion() + "\n" +
                "Tel: " + AppConfig.getInstancia().getTelefono() + "\n" +
                "----------------------------------------\n" +
                "FACTURA: " + v.getNumeroFactura() + "\n" +
                "Fecha: " + v.getFecha().format(formato) + "\n" +
                "Atendido por: " + v.getNombreUsuario() + "\n" +
                "Cliente: " + v.getClienteNombre() +
                (v.getClienteCedula() != null && !v.getClienteCedula().isBlank() ? " (CI: " + v.getClienteCedula() + ")" : "") + "\n" +
                "----------------------------------------\n" +
                "Producto: " + v.getNombrePrenda() + "\n" +
                "Cantidad: " + v.getCantidad() + "\n" +
                "Precio unitario: $" + String.format("%.2f", precioUnitario) + "\n" +
                "TOTAL: $" + String.format("%.2f", v.getTotal()) + "\n" +
                "----------------------------------------\n" +
                "¡Gracias por su compra!";
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
