package Controladores;

import App.MainApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class PrincipalController implements Initializable {

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void cargarVista(String fxml) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxml)
            );

            Node vista = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirMapas() {
        cargarVista("/Vistas/GestionDeMapasVista.fxml");
    }

    @FXML
    private void abrirGPX() {
        cargarVista("/Vistas/ImportarActividadVista.fxml");
    }

    @FXML
    private void abrirActividades() {
        cargarVista("/Vistas/ListaDeActividadesVista.fxml");
    }

    @FXML
    private void abrirHistorial() {
        cargarVista("/Vistas/HistorialSesionesVista.fxml");
    }

    @FXML
    private void abrirAcumulados() {
        cargarVista("/Vistas/AcumuladosVista.fxml");
    }
    @FXML
    private void IrPerfil() throws Exception {
        MainApp.cargarPerfil();
    }
}
