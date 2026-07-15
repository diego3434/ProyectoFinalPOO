package model;

public class Usuario extends Persona {


    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_CAJERO = "CAJERO";
    public static final String ROL_REPORTES = "REPORTES";

    private String contrasena;
    private String rol;

    public Usuario() {
        super();
    }

    public Usuario(int id, String nombre, String correo, String contrasena, String rol) {
        super(id, nombre, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }


    @Override
    public String describir() {
        return getNombre() + " (" + rol + ")";
    }

    @Override
    public String toString() {
        return getNombre() + " - " + rol;
    }
}
