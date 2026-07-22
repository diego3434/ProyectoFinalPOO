package controller;

import app.AppConfig;
import DAOimplents.PedidoDAO;
import DAOimplents.PrendaDAO;
import DAOimplents.VentaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Pedido;
import model.Prenda;
import model.Usuario;
import model.Venta;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class VentaController implements UsuarioInterfaz {
    @FXML
    private ComboBox<Prenda> cbPrenda;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteCedula;
    @FXML private Label lblTotal;

    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, String> colCliente;
    @FXML private TableColumn<Pedido, String> colPrenda;
    @FXML private TableColumn<Pedido, Integer> colCantidad;
    @FXML private TableColumn<Pedido, String> colEstado;

    private final PrendaDAO prendaDAO = new PrendaDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private Usuario usuarioSesion;

    @Override
    public void setUsuarioSesion(Usuario usuario) {
        this.usuarioSesion = usuario;
    }

    @FXML
    public void initialize() {
        cargarCatalogo();

        cbPrenda.valueProperty().addListener((obs, anterior, seleccionada) -> calcularTotal());
        txtCantidad.textProperty().addListener((obs, anterior, actual) -> calcularTotal());

        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        colPrenda.setCellValueFactory(new PropertyValueFactory<>("nombrePrenda"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaPedidos.setItems(listaPedidos);
        cargarPedidosPendientes();
    }

    private void cargarCatalogo() {
        cbPrenda.setItems(FXCollections.observableArrayList(prendaDAO.listarConStock()));
    }

    private void cargarPedidosPendientes() {
        listaPedidos.setAll(pedidoDAO.listarPendientes());
    }

    private void calcularTotal() {
        Prenda seleccionada = cbPrenda.getValue();
        if (seleccionada == null || txtCantidad.getText().trim().isEmpty()) {
            lblTotal.setText("$ 0.00");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            lblTotal.setText(String.format("$ %.2f", seleccionada.getPrecio() * cantidad));
        } catch (NumberFormatException e) {
            lblTotal.setText("$ 0.00");
        }
    }

    @FXML
    private void registrarVenta() {
        Prenda seleccionada = cbPrenda.getValue();
        String cantidadTexto = txtCantidad.getText().trim();
        String clienteNombre = txtClienteNombre.getText().trim();
        String clienteCedula = txtClienteCedula.getText().trim();

        // Validación: campos vacíos
        if (seleccionada == null || cantidadTexto.isEmpty() || clienteNombre.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Campos incompletos",
                    "Selecciona una prenda, indica la cantidad y el nombre del cliente.");
            return;
        }

        // Validación: tipo de dato / número positivo
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException e) {
            alerta(Alert.AlertType.ERROR, "Dato inválido", "La cantidad debe ser un número entero.");
            return;
        }
        if (cantidad <= 0) {
            alerta(Alert.AlertType.WARNING, "Valor inválido", "La cantidad debe ser mayor a 0.");
            return;
        }
        if (cantidad > seleccionada.getStock()) {
            alerta(Alert.AlertType.WARNING, "Stock insuficiente",
                    "Solo hay " + seleccionada.getStock() + " unidades disponibles de esta prenda.");
            return;
        }

        Venta venta = new Venta();
        venta.setIdUsuario(usuarioSesion.getId());
        venta.setIdPrenda(seleccionada.getId());
        venta.setCantidad(cantidad);
        venta.setTotal(seleccionada.getPrecio() * cantidad);
        venta.setClienteNombre(clienteNombre);
        venta.setClienteCedula(clienteCedula.isEmpty() ? null : clienteCedula);

        Venta registrada = ventaDAO.registrarVentaConFactura(venta);
        if (registrada == null) {
            alerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar la venta (verifica el stock).");
            return;
        }

        mostrarFactura(registrada, seleccionada.getNombre(), seleccionada.getPrecio());
        limpiar();
        cargarCatalogo();
    }

    @FXML
    private void facturarPedidoSeleccionado() {
        Pedido pedido = tablaPedidos.getSelectionModel().getSelectedItem();
        if (pedido == null) {
            alerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona un pedido pendiente de la tabla.");
            return;
        }
        if (Pedido.ESTADO_FACTURADO.equals(pedido.getEstado())) {
            alerta(Alert.AlertType.INFORMATION, "Ya facturado", "Ese pedido ya fue facturado anteriormente.");
            return;
        }

        Venta venta = new Venta();
        venta.setIdUsuario(usuarioSesion.getId());
        venta.setIdPrenda(pedido.getIdPrenda());
        venta.setCantidad(pedido.getCantidad());
        venta.setTotal(pedido.getPrecioUnitario() * pedido.getCantidad());
        venta.setClienteNombre(pedido.getClienteNombre());
        venta.setClienteCedula(pedido.getClienteCedula());

        Venta registrada = ventaDAO.registrarVentaConFactura(venta);
        if (registrada == null) {
            alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo facturar el pedido (revisa que aún haya stock suficiente).");
            return;
        }

        pedidoDAO.marcarFacturado(pedido.getId());
        mostrarFactura(registrada, pedido.getNombrePrenda(), pedido.getPrecioUnitario());
        cargarPedidosPendientes();
        cargarCatalogo();
    }

    private void mostrarFactura(Venta v, String nombrePrenda, double precioUnitario) {
        String contenido = construirFactura(v, nombrePrenda, precioUnitario);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Factura generada");
        alerta.setHeaderText(v.getNumeroFactura());
        TextArea area = new TextArea(contenido);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefWidth(420);
        area.setPrefHeight(280);
        alerta.getDialogPane().setContent(area);
        alerta.showAndWait();

        try (FileWriter fw = new FileWriter(v.getNumeroFactura() + ".txt")) {
            fw.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String construirFactura(Venta v, String nombrePrenda, double precioUnitario) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return AppConfig.getInstancia().getNombreEmpresa() + "\n" +
                AppConfig.getInstancia().getDireccion() + "\n" +
                "Tel: " + AppConfig.getInstancia().getTelefono() + "\n" +
                "----------------------------------------\n" +
                "FACTURA: " + v.getNumeroFactura() + "\n" +
                "Fecha: " + v.getFecha().format(formato) + "\n" +
                "Cliente: " + v.getClienteNombre() +
                (v.getClienteCedula() != null && !v.getClienteCedula().isBlank() ? " (CI: " + v.getClienteCedula() + ")" : "") + "\n" +
                "----------------------------------------\n" +
                "Producto: " + nombrePrenda + "\n" +
                "Cantidad: " + v.getCantidad() + "\n" +
                "Precio unitario: $" + String.format("%.2f", precioUnitario) + "\n" +
                "TOTAL: $" + String.format("%.2f", v.getTotal()) + "\n" +
                "----------------------------------------\n" +
                "¡Gracias por su compra!";
    }

    @FXML
    private void limpiar() {
        cbPrenda.setValue(null);
        txtCantidad.clear();
        txtClienteNombre.clear();
        txtClienteCedula.clear();
        lblTotal.setText("$ 0.00");
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
