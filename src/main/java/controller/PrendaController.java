package controller;

import DAOimplents.PrendaDAO;
import model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Prenda;

import java.util.Optional;

public class PrendaController implements UsuarioInterfaz {

    @FXML
    private TextField txtNombre;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbTalla;
    @FXML private TextField txtColor;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private Label lblTituloFormulario;

    @FXML private TableView<Prenda> tablaPrendas;
    @FXML private TableColumn<Prenda, Integer> colId;
    @FXML private TableColumn<Prenda, String> colNombre;
    @FXML private TableColumn<Prenda, String> colCategoria;
    @FXML private TableColumn<Prenda, String> colTalla;
    @FXML private TableColumn<Prenda, String> colColor;
    @FXML private TableColumn<Prenda, Double> colPrecio;
    @FXML private TableColumn<Prenda, Integer> colStock;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    private final PrendaDAO prendaDAO = new PrendaDAO();
    private final ObservableList<Prenda> listaPrendas = FXCollections.observableArrayList();
    private Prenda prendaSeleccionada;
    private Usuario usuarioSesion;

    @Override
    public void setUsuarioSesion(Usuario usuario) {
        this.usuarioSesion = usuario;

        boolean soloLectura = Usuario.ROL_REPORTES.equals(usuario.getRol());
        btnGuardar.setDisable(soloLectura);
        btnEliminar.setDisable(soloLectura);
    }

    @FXML
    public void initialize() {
        cbCategoria.setItems(FXCollections.observableArrayList(
                "Camisas", "Pantalones", "Vestidos", "Zapatos", "Chaquetas", "Accesorios"));
        cbTalla.setItems(FXCollections.observableArrayList("XS", "S", "M", "L", "XL", "XXL"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colTalla.setCellValueFactory(new PropertyValueFactory<>("talla"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        tablaPrendas.setItems(listaPrendas);
        tablaPrendas.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionada) -> {
                    if (seleccionada != null) {
                        cargarEnFormulario(seleccionada);
                    }
                });

        cargarTabla();
    }

    private void cargarTabla() {
        listaPrendas.setAll(prendaDAO.listar());
    }

    private void cargarEnFormulario(Prenda p) {
        prendaSeleccionada = p;
        lblTituloFormulario.setText("Editar prenda");
        txtNombre.setText(p.getNombre());
        cbCategoria.setValue(p.getCategoria());
        cbTalla.setValue(p.getTalla());
        txtColor.setText(p.getColor());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String categoria = cbCategoria.getValue();
        String talla = cbTalla.getValue();
        String color = txtColor.getText().trim();
        String precioTexto = txtPrecio.getText().trim();
        String stockTexto = txtStock.getText().trim();

        // Validación: campos vacíos
        if (nombre.isEmpty() || categoria == null || talla == null
                || color.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Campos incompletos",
                    "Todos los campos son obligatorios.");
            return;
        }

        // Validación: tipo de dato / solo números positivos
        double precio;
        int stock;
        try {
            precio = Double.parseDouble(precioTexto);
            stock = Integer.parseInt(stockTexto);
        } catch (NumberFormatException e) {
            alerta(Alert.AlertType.ERROR, "Dato inválido",
                    "Precio y stock deben ser valores numéricos.");
            return;
        }

        if (precio <= 0 || stock < 0) {
            alerta(Alert.AlertType.WARNING, "Valor inválido",
                    "El precio debe ser mayor a 0 y el stock no puede ser negativo.");
            return;
        }

        // Validación: no duplicados (solo al crear una nueva prenda)
        if (prendaSeleccionada == null && prendaDAO.existeNombre(nombre)) {
            alerta(Alert.AlertType.WARNING, "Prenda duplicada",
                    "Ya existe una prenda registrada con ese nombre.");
            return;
        }

        boolean exito;
        if (prendaSeleccionada == null) {
            Prenda nueva = new Prenda(0, nombre, categoria, talla, color, precio, stock);
            exito = prendaDAO.guardar(nueva);
        } else {
            prendaSeleccionada.setNombre(nombre);
            prendaSeleccionada.setCategoria(categoria);
            prendaSeleccionada.setTalla(talla);
            prendaSeleccionada.setColor(color);
            prendaSeleccionada.setPrecio(precio);
            prendaSeleccionada.setStock(stock);
            exito = prendaDAO.actualizar(prendaSeleccionada);
        }

        if (exito) {
            alerta(Alert.AlertType.INFORMATION, "Éxito", "La prenda se guardó correctamente.");
            limpiar();
            cargarTabla();
        } else {
            alerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la prenda.");
        }
    }

    @FXML
    private void eliminar() {
        Prenda seleccionada = tablaPrendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            alerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona una prenda de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar la prenda \"" + seleccionada.getNombre() + "\"?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (prendaDAO.eliminar(seleccionada.getId())) {
                alerta(Alert.AlertType.INFORMATION, "Eliminado", "Prenda eliminada correctamente.");
                limpiar();
                cargarTabla();
            } else {
                alerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la prenda.");
            }
        }
    }

    @FXML
    private void limpiar() {
        prendaSeleccionada = null;
        lblTituloFormulario.setText("Nueva prenda");
        txtNombre.clear();
        cbCategoria.setValue(null);
        cbTalla.setValue(null);
        txtColor.clear();
        txtPrecio.clear();
        txtStock.clear();
        tablaPrendas.getSelectionModel().clearSelection();
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
