package app;

import DAOimplents.ConfiguracionDAO;
import javafx.scene.image.Image;
import model.Configuracion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppConfig {

    private static AppConfig instancia;

    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();
    private final List<Runnable> listeners = new ArrayList<>();

    private String nombreEmpresa = "Tienda de Ropa";
    private String direccion = "";
    private String telefono = "";
    private String logoPath;

    private AppConfig() {
    }

    public static AppConfig getInstancia() {
        if (instancia == null) {
            instancia = new AppConfig();
        }
        return instancia;
    }


    public void cargarDesdeBD() {
        Configuracion c = configuracionDAO.obtener();
        this.nombreEmpresa = c.getNombreEmpresa();
        this.direccion = c.getDireccion();
        this.telefono = c.getTelefono();
        this.logoPath = c.getLogoPath();
    }


    public boolean actualizar(String nombreEmpresa, String direccion, String telefono, String logoPath) {
        Configuracion c = new Configuracion(1, nombreEmpresa, direccion, telefono, logoPath);
        boolean exito = configuracionDAO.actualizar(c);
        if (exito) {
            this.nombreEmpresa = nombreEmpresa;
            this.direccion = direccion;
            this.telefono = telefono;
            this.logoPath = logoPath;
            notificarCambios();
        }
        return exito;
    }


    public void agregarListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notificarCambios() {
        for (Runnable r : listeners) {
            r.run();
        }
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getLogoPath() {
        return logoPath;
    }


    public Image getLogoComoImagen() {
        if (logoPath != null && !logoPath.isBlank()) {
            File f = new File(logoPath);
            if (f.exists()) {
                return new Image(f.toURI().toString());
            }
        }
        return new Image(getClass().getResourceAsStream("/img/logo.png"));
    }
}
