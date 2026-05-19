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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mapademo.MapaDemoApp;

/**
 * FXML Controller class
 *
 * @author vinte
 */
public class VistaLoginController implements Initializable {

    @FXML
    private TextField nickname_login;
    @FXML
    private PasswordField password_login;
    @FXML
    private Button button_login;
    @FXML
    private Label nickname_error;
    @FXML
    private Label password_error;
    @FXML
    private Hyperlink linkRegistro;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    
        @FXML
    private void irRegistro(ActionEvent event) {

        try {

            Stage stage = (Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow();

            MapaDemoApp.cargarVista(
                    "/Vistas/vistaRegister.fxml",
                    stage
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
    
