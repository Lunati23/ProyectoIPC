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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mapademo.MapaDemoApp;

/**
 * FXML Controller class
 *
 * @author vinte
 */
public class VistaRegisterController implements Initializable {

    @FXML
    private Button returnButton;
    @FXML
    private Label nicknameError;
    @FXML
    private TextField nickname_register;
    @FXML
    private PasswordField password_register;
    @FXML
    private Label paswordError;
    @FXML
    private TextField mail_register;
    @FXML
    private Label mailError;
    @FXML
    private DatePicker date_register;
    @FXML
    private Label dateError;
    @FXML
    private Button imagenButton_register;
    @FXML
    private ImageView logoImagen;
    @FXML
    private Label fotoPath;
    @FXML
    private Button registerButton;

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
    
