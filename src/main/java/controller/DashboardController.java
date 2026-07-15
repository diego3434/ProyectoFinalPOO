package controller;

import model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;


public class DashboardController {

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRol;
    @FXML private BorderPane contentPane;

    @FXML private Button btnPrendas;
    @FXML private Button btnUsuarios;
    @FXML private Button btnReportes;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        lblNombreUsuario.setText(usuario.getNombre());
        lblRol.setText(usuario.getRol());

        contentPane.getStyleClass().removeAll("tema-admin", "tema-cajero", "tema-reportes");
        switch (usuario.getRol()) {
            case Usuario.ROL_ADMIN -> contentPane.getStyleClass().add("tema-admin");
            case Usuario.ROL_CAJERO -> contentPane.getStyleClass().add("tema-cajero");
            case Usuario.ROL_REPORTES -> contentPane.getStyleClass().add("tema-reportes");
        }

        aplicarPermisosPorRol(usuario.getRol());
        cargarVista("/view/prendas.fxml");
    }

    private void aplicarPermisosPorRol(String rol) {
        boolean esAdmin = Usuario.ROL_ADMIN.equals(rol);
        boolean esCajero = Usuario.ROL_CAJERO.equals(rol);
        boolean esReportes = Usuario.ROL_REPORTES.equals(rol);

        btnPrendas.setVisible(esAdmin || esCajero);
        btnPrendas.setManaged(esAdmin || esCajero);

        btnUsuarios.setVisible(esAdmin);
        btnUsuarios.setManaged(esAdmin);

        btnReportes.setVisible(esAdmin || esReportes);
        btnReportes.setManaged(esAdmin || esReportes);

        btnConfiguracion.setVisible(esAdmin);
        btnConfiguracion.setManaged(esAdmin);
    }

    @FXML
    private void irAPrendas() {
        cargarVista("/view/prendas.fxml");
    }

    @FXML
    private void irAUsuarios() {
        cargarVista("/view/usuarios.fxml");
    }

    @FXML
    private void irAReportes() {
        cargarVista("/view/reportes.fxml");
    }

    @FXML
    private void irAConfiguracion() {
        cargarVista("/view/configuracion.fxml");
    }


    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();

            Object controlador = loader.getController();
            if (controlador instanceof UsuarioAware ua) {
                ua.setUsuarioSesion(usuarioActual);
            }

            contentPane.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar sesión");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas cerrar sesión?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Tienda de Ropa - Login");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}