/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mapademo.MapaDemoApp;

/**
 * FXML Controller class
 *
 * @author vinte
 */
public class VistaHistorialSesionesController implements Initializable {

    @FXML
    private Button volver_historial;
    @FXML
    private Label sesionesLabel;
    @FXML
    private Label importadasLabel;
    @FXML
    private Label visualizadasLabel;
    @FXML
    private Label anotacionesLabel;
    @FXML
    private Button cerrarSesionButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void irLogin(ActionEvent event) {
        try {

            Stage stage = (Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow();

            MapaDemoApp.cargarVista(
                    "/Vistas/vistaLogin.fxml",
                    stage
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
}
