package controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import app.AppConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfiguracionController implements UsuarioInterfaz {

    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private Label lblConectadoComo;
    @FXML private ImageView imgLogoPreview;

    // Ruta del logo elegido en esta sesión de edición (aún no guardado)
    private String rutaLogoSeleccionado;

    @Override
    public void setUsuarioSesion(Usuario usuario) {
        lblConectadoComo.setText("Conectado como: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
    }

    @FXML
    public void initialize() {
        // Se carga lo que ya existe, para que el admin edite sobre los valores actuales
        txtNombreEmpresa.setText(AppConfig.getInstancia().getNombreEmpresa());
        txtDireccion.setText(AppConfig.getInstancia().getDireccion());
        txtTelefono.setText(AppConfig.getInstancia().getTelefono());
        rutaLogoSeleccionado = AppConfig.getInstancia().getLogoPath();
        imgLogoPreview.setImage(AppConfig.getInstancia().getLogoComoImagen());
    }

    @FXML
    private void seleccionarLogo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona el nuevo logo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File archivo = chooser.showOpenDialog(imgLogoPreview.getScene().getWindow());
        if (archivo == null) {
            return; // el usuario canceló el diálogo
        }

        try {
            // Se copia el archivo elegido a una carpeta fija de la app,
            // para que el logo siga existiendo aunque el usuario borre el original.
            Path carpetaApp = Path.of(System.getProperty("user.home"), ".tienda_ropa");
            Files.createDirectories(carpetaApp);

            String extension = obtenerExtension(archivo.getName());
            Path destino = carpetaApp.resolve("logo_actual" + extension);
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            rutaLogoSeleccionado = destino.toAbsolutePath().toString();
            imgLogoPreview.setImage(new Image(destino.toUri().toString()));
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen seleccionada.");
            e.printStackTrace();
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        return punto >= 0 ? nombreArchivo.substring(punto) : ".png";
    }

    @FXML
    private void guardarConfiguracion() {
        String nombre = txtNombreEmpresa.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validación: campo obligatorio
        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido",
                    "El nombre de la empresa no puede estar vacío.");
            return;
        }

        boolean exito = AppConfig.getInstancia().actualizar(nombre, direccion, telefono, rutaLogoSeleccionado);

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Configuración",
                    "Los parámetros se guardaron correctamente.\nEl nombre y el logo ya se actualizaron en todo el sistema.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la configuración.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
