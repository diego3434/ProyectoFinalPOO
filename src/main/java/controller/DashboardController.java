package controller;

import app.AppConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Usuario;

import java.io.IOException;
import java.util.Optional;


public class DashboardController  {

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblNombreEmpresa;
    @FXML private ImageView imgLogo;
    @FXML private BorderPane contentPane;

    @FXML private Button btnPrendas;
    @FXML private Button btnVentas;
    @FXML private Button btnFacturas;
    @FXML private Button btnComprar;
    @FXML private Button btnUsuarios;
    @FXML private Button btnReportes;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnCerrarSesion;

    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        actualizarMarca();
        // Si el Administrador cambia nombre/logo en Configuración, este
        // dashboard (y cualquier otro abierto) se actualiza al instante.
        AppConfig.getInstancia().agregarListener(() -> Platform.runLater(this::actualizarMarca));
    }

    private void actualizarMarca() {
        lblNombreEmpresa.setText(AppConfig.getInstancia().getNombreEmpresa());
        imgLogo.setImage(AppConfig.getInstancia().getLogoComoImagen());
    }

    /** Punto clave de la reutilización: el Login llama a este método. */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        lblNombreUsuario.setText(usuario.getNombre());
        lblRol.setText(usuario.getRol());

        // Estilo de color distinto según el rol (definido en styles.css)
        contentPane.getStyleClass().removeAll("tema-admin", "tema-cajero", "tema-reportes", "tema-comprador");
        switch (usuario.getRol()) {
            case Usuario.ROL_ADMIN -> contentPane.getStyleClass().add("tema-admin");
            case Usuario.ROL_CAJERO -> contentPane.getStyleClass().add("tema-cajero");
            case Usuario.ROL_REPORTES -> contentPane.getStyleClass().add("tema-reportes");
            case Usuario.ROL_COMPRADOR -> contentPane.getStyleClass().add("tema-comprador");
        }

        aplicarPermisosPorRol(usuario.getRol());
        cargarVistaInicial(usuario.getRol());
    }

    /**
     * Administrador ve todo. Cajero ve Prendas/Ventas/Facturas.
     * Reportes es de solo lectura (únicamente Reportes).
     * Comprador únicamente ve el catálogo para comprar.
     */
    private void aplicarPermisosPorRol(String rol) {
        boolean esAdmin = Usuario.ROL_ADMIN.equals(rol);
        boolean esCajero = Usuario.ROL_CAJERO.equals(rol);
        boolean esReportes = Usuario.ROL_REPORTES.equals(rol);
        boolean esComprador = Usuario.ROL_COMPRADOR.equals(rol);

        btnPrendas.setVisible(esAdmin || esCajero);
        btnPrendas.setManaged(esAdmin || esCajero);

        btnVentas.setVisible(esAdmin || esCajero);
        btnVentas.setManaged(esAdmin || esCajero);

        btnFacturas.setVisible(esAdmin || esCajero);
        btnFacturas.setManaged(esAdmin || esCajero);

        btnComprar.setVisible(esComprador);
        btnComprar.setManaged(esComprador);

        btnUsuarios.setVisible(esAdmin);
        btnUsuarios.setManaged(esAdmin);

        btnReportes.setVisible(esAdmin || esReportes);
        btnReportes.setManaged(esAdmin || esReportes);

        btnConfiguracion.setVisible(esAdmin);
        btnConfiguracion.setManaged(esAdmin);
    }


    private void cargarVistaInicial(String rol) {
        switch (rol) {
            case Usuario.ROL_REPORTES -> cargarVista("/view/reportes.fxml");
            case Usuario.ROL_COMPRADOR -> cargarVista("/view/compra.fxml");
            default -> cargarVista("/view/prendas.fxml"); // Admin y Cajero
        }
    }

    @FXML
    private void irAPrendas() {
        cargarVista("/view/prendas.fxml");
    }

    @FXML
    private void irAVentas() {
        cargarVista("/view/ventas.fxml");
    }

    @FXML
    private void irAFacturas() {
        cargarVista("/view/facturas.fxml");
    }

    @FXML
    private void irAComprar() {
        cargarVista("/view/compra.fxml");
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

    /** Carga el módulo seleccionado dentro del panel central del dashboard. */
    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();

            // Si el módulo necesita saber quién inició sesión, se lo pasamos
            Object controlador = loader.getController();
            if (controlador instanceof UsuarioInterfaz ua) {
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
                stage.setTitle(AppConfig.getInstancia().getNombreEmpresa() + " - Login");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}