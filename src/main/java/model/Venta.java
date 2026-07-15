package model;



import java.time.LocalDateTime;

public class Venta  {

    private int id;
    private int idUsuario;
    private int idPrenda;
    private int cantidad;
    private double total;
    private LocalDateTime fecha;

    // Campos de apoyo para mostrar en pantalla (vienen del JOIN, no se guardan)
    private String nombreUsuario;
    private String nombrePrenda;

    public Venta() {
    }

    public Venta(int id, int idUsuario, int idPrenda, int cantidad,
                 double total, LocalDateTime fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idPrenda = idPrenda;
        this.cantidad = cantidad;
        this.total = total;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPrenda() {
        return idPrenda;
    }

    public void setIdPrenda(int idPrenda) {
        this.idPrenda = idPrenda;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombrePrenda() {
        return nombrePrenda;
    }

    public void setNombrePrenda(String nombrePrenda) {
        this.nombrePrenda = nombrePrenda;
    }
    
}
