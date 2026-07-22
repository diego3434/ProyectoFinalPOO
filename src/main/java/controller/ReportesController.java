package controller;

import DAOimplents.PrendaDAO;
import DAOimplents.VentaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Usuario;
import model.Venta;

public class ReportesController implements  UsuarioInterfaz {

    @FXML
    private Label lblTotalVentas;
    @FXML private Label lblTotalPrendas;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String> colUsuario;
    @FXML private TableColumn<Venta, String> colPrenda;
    @FXML private TableColumn<Venta, Integer> colCantidad;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private BarChart<String, Number> chartVentas;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final PrendaDAO prendaDAO = new PrendaDAO();

    @Override
    public void setUsuarioSesion(Usuario usuario) {

    }

    @FXML
    public void initialize() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colPrenda.setCellValueFactory(new PropertyValueFactory<>("nombrePrenda"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        ObservableList<Venta> ventas = FXCollections.observableArrayList(ventaDAO.listar());
        tablaVentas.setItems(ventas);

        lblTotalVentas.setText(String.format("$ %.2f", ventaDAO.totalVentas()));
        lblTotalPrendas.setText(String.valueOf(prendaDAO.listar().size()));

        cargarGrafico(ventas);
    }

    private void cargarGrafico(ObservableList<Venta> ventas) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Total por prenda");
        for (Venta v : ventas) {
            serie.getData().add(new XYChart.Data<>(v.getNombrePrenda(), v.getTotal()));
        }
        chartVentas.getData().add(serie);
    }


    @FXML
    private void descargarReporte() {
        StringBuilder sb = new StringBuilder("Usuario,Prenda,Cantidad,Total\n");
        for (Venta v : ventaDAO.listar()) {
            sb.append(v.getNombreUsuario()).append(",")
                    .append(v.getNombrePrenda()).append(",")
                    .append(v.getCantidad()).append(",")
                    .append(v.getTotal()).append("\n");
        }

        try (java.io.FileWriter fw = new java.io.FileWriter("reporte_ventas.csv")) {
            fw.write(sb.toString());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
