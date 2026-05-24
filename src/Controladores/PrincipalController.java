package Controladores;

import App.MainApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class PrincipalController implements Initializable {

    @FXML
    private StackPane contentArea;
    @FXML
    private HBox btnGpx;
    @FXML
    private HBox btnAct;
    @FXML
    private HBox btnMapas;
    @FXML
    private HBox btnHist;
    @FXML
    private HBox btnAcum;

    private HBox botónActivo = null;
    
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
        cambiarBotónActivo(btnMapas);
    }

    @FXML
    private void abrirGPX() {
        cargarVista("/Vistas/ImportarActividadVista.fxml");
        cambiarBotónActivo(btnGpx);
    }

    @FXML
    private void abrirActividades() {
        cargarVista("/Vistas/ListaDeActividadesVista.fxml");
        cambiarBotónActivo(btnAct);
    }

    @FXML
    private void abrirHistorial() {
        cargarVista("/Vistas/HistorialSesionesVista.fxml");
        cambiarBotónActivo(btnHist);
    }

    @FXML
    private void abrirAcumulados() {
        cargarVista("/Vistas/AcumuladosVista.fxml");
        cambiarBotónActivo(btnAcum);
    }

    @FXML
    private void IrPerfil() throws Exception {
        MainApp.cargarPerfil();
    }

    private void cambiarBotónActivo(HBox nuevoBoton) {
        // Remover estilo del botón anterior
        if (botónActivo != null) {
            botónActivo.getStyleClass().remove("side-item-selected");
        }

        // Añadir estilo al nuevo botón
        nuevoBoton.getStyleClass().add("side-item-selected");
        botónActivo = nuevoBoton;
    }
}
