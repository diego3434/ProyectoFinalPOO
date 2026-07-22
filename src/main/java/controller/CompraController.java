package controller;

import DAOimplents.PedidoDAO;
import DAOimplents.PrendaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Pedido;
import model.Prenda;
import model.Usuario;


public class CompraController implements UsuarioInterfaz {
    @FXML
    private TableView<Prenda> tablaCatalogo;
    @FXML private TableColumn<Prenda, String> colNombre;
    @FXML private TableColumn<Prenda, String> colCategoria;
    @FXML private TableColumn<Prenda, String> colTalla;
    @FXML private TableColumn<Prenda, String> colColor;
    @FXML private TableColumn<Prenda, Double> colPrecio;
    @FXML private TableColumn<Prenda, Integer> colStock;

    @FXML private Label lblSeleccion;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteCedula;
    @FXML private Label lblTotal;

    @FXML private TableView<Pedido> tablaMisPedidos;
    @FXML private TableColumn<Pedido, String> colPedidoPrenda;
    @FXML private TableColumn<Pedido, Integer> colPedidoCantidad;
    @FXML private TableColumn<Pedido, String> colPedidoEstado;

    private final PrendaDAO prendaDAO = new PrendaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final ObservableList<Prenda> catalogo = FXCollections.observableArrayList();
    private final ObservableList<Pedido> misPedidos = FXCollections.observableArrayList();
    private Usuario usuarioSesion;

    @Override

    public void setUsuarioSesion(Usuario usuario) {
        this.usuarioSesion = usuario;
        txtClienteNombre.setText(usuario.getNombre()); // dato simple pre-llenado, editable
        cargarMisPedidos();
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colTalla.setCellValueFactory(new PropertyValueFactory<>("talla"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colPedidoPrenda.setCellValueFactory(new PropertyValueFactory<>("nombrePrenda"));
        colPedidoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPedidoEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaCatalogo.setItems(catalogo);
        tablaMisPedidos.setItems(misPedidos);

        tablaCatalogo.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            lblSeleccion.setText(seleccionada != null ? "Seleccionaste: " + seleccionada.getNombre() : "");
            calcularTotal();
        });
        txtCantidad.textProperty().addListener((obs, anterior, actual) -> calcularTotal());

        cargarCatalogo();
    }

    private void cargarCatalogo() {
        catalogo.setAll(prendaDAO.listarConStock());
    }

    private void cargarMisPedidos() {
        if (usuarioSesion != null) {
            misPedidos.setAll(pedidoDAO.listarPorComprador(usuarioSesion.getId()));
        }
    }

    private void calcularTotal() {
        Prenda seleccionada = tablaCatalogo.getSelectionModel().getSelectedItem();
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
    private void generarPedido() {
        Prenda seleccionada = tablaCatalogo.getSelectionModel().getSelectedItem();
        String cantidadTexto = txtCantidad.getText().trim();
        String clienteNombre = txtClienteNombre.getText().trim();
        String clienteCedula = txtClienteCedula.getText().trim();

        // Validación: campos vacíos
        if (seleccionada == null) {
            alerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona una prenda del catálogo.");
            return;
        }
        if (cantidadTexto.isEmpty() || clienteNombre.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Campos incompletos",
                    "Indica la cantidad y tu nombre para generar el pedido.");
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
                    "Solo hay " + seleccionada.getStock() + " unidades disponibles.");
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setIdComprador(usuarioSesion.getId());
        pedido.setIdPrenda(seleccionada.getId());
        pedido.setCantidad(cantidad);
        pedido.setClienteNombre(clienteNombre);
        pedido.setClienteCedula(clienteCedula.isEmpty() ? null : clienteCedula);

        if (pedidoDAO.guardar(pedido)) {
            alerta(Alert.AlertType.INFORMATION, "Pedido registrado",
                    "Tu pedido fue registrado correctamente. Un cajero lo revisará y generará tu factura.");
            limpiar();
            cargarMisPedidos();
        } else {
            alerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el pedido.");
        }
    }

    @FXML
    private void limpiar() {
        txtCantidad.clear();
        txtClienteCedula.clear();
        tablaCatalogo.getSelectionModel().clearSelection();
        lblTotal.setText("$ 0.00");
        lblSeleccion.setText("");
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
