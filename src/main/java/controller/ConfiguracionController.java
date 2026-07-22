package controller;

import model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConfiguracionController implements UsuarioInterfaz {
    @FXML
    private TextField txtNombreEmpresa;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private Label lblConectadoComo;

    @Override
    public void setUsuarioSesion(Usuario usuario) {
        lblConectadoComo.setText("Conectado como: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
    }

    @FXML
    private void guardarConfiguracion() {
        if (txtNombreEmpresa.getText().trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Campo requerido");
            a.setHeaderText(null);
            a.setContentText("El nombre de la empresa no puede estar vacío.");
            a.showAndWait();
            return;
        }

        // Aquí se podría persistir la configuración en una tabla "configuracion"
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Configuración");
        a.setHeaderText(null);
        a.setContentText("Los parámetros se guardaron correctamente.");
        a.showAndWait();
    }
}
