package controller;

import DAOimplents.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Usuario;

import java.util.Optional;

public class UsuarioController implements UsuarioAware {

    @FXML
    private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<String> cbRol;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> lista = FXCollections.observableArrayList();

    @Override
    public void setUsuarioSesion(Usuario usuario) {
        // Este módulo solo lo ve el administrador (controlado desde el Dashboard)
    }

    @FXML
    public void initialize() {
        cbRol.setItems(FXCollections.observableArrayList(
                Usuario.ROL_ADMIN, Usuario.ROL_CAJERO, Usuario.ROL_REPORTES));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        tablaUsuarios.setItems(lista);
        cargarTabla();
    }

    private void cargarTabla() {
        lista.setAll(usuarioDAO.listar());
    }

    @FXML
    private void crearUsuario() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String rol = cbRol.getValue();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || rol == null) {
            alerta(Alert.AlertType.WARNING, "Campos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        // Validación: mínimo de caracteres en la contraseña
        if (contrasena.length() < 6) {
            alerta(Alert.AlertType.WARNING, "Contraseña débil",
                    "La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        // Validación: no duplicados
        if (usuarioDAO.existeNombre(nombre)) {
            alerta(Alert.AlertType.WARNING, "Usuario duplicado",
                    "Ya existe un usuario registrado con ese nombre.");
            return;
        }

        Usuario nuevo = new Usuario(0, nombre, correo, contrasena, rol);
        if (usuarioDAO.guardar(nuevo)) {
            alerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado correctamente.");
            limpiar();
            cargarTabla();
        } else {
            alerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario.");
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona un usuario de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar al usuario \"" + seleccionado.getNombre() + "\"?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (usuarioDAO.eliminar(seleccionado.getId())) {
                alerta(Alert.AlertType.INFORMATION, "Eliminado", "Usuario eliminado.");
                limpiar();
                cargarTabla();
            } else {
                alerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el usuario.");
            }
        }
    }

    @FXML
    private void limpiar() {
        txtNombre.clear();
        txtCorreo.clear();
        txtContrasena.clear();
        cbRol.setValue(null);
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
