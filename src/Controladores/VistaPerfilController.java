/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mapademo.MapaDemoApp;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
/**
 * FXML Controller class
 *
 * @author vinte
 */
public class VistaPerfilController implements Initializable {

    @FXML
    private Button returnButton_profile;
    @FXML
    private Button changesButton_profile;
    @FXML
    private GridPane mail_profile;
    @FXML
    private Text nickname_profile;
    @FXML
    private Text correo_profile;
    @FXML
    private Text date_profile;
    @FXML
    private PasswordField password_perfil;
    @FXML
    private Label passwordError;
    @FXML
    private TextField mailField_profile;
    @FXML
    private Label mailError;
    @FXML
    private DatePicker datePicker_profile;
    @FXML
    private Label dateError;
    @FXML
    private ImageView logoImagen_profile; 
    @FXML
    private Button imagenButton_profile; 


    private SportActivityApp app;
    private User currentUser;
    private String nuevoAvatarPath = null;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        app = SportActivityApp.getInstance();
        currentUser = app.getCurrentUser();
        
        limpiarErrores();
        cargarDatosUsuario();
        
        changesButton_profile.setOnAction(this::guardarCambios);
        returnButton_profile.setOnAction(this::volver);
        if (imagenButton_profile != null) {
            imagenButton_profile.setOnAction(this::seleccionarNuevoAvatar);
        }
    }    
    
    private void cargarDatosUsuario() {
        if (currentUser != null) {
            nickname_profile.setText(currentUser.getNickName()); 
            correo_profile.setText(currentUser.getEmail());
            if (currentUser.getBirthDate() != null) {
                date_profile.setText(currentUser.getBirthDate().toString());
            }
            
            mailField_profile.setText("");
            password_perfil.setText("");
            datePicker_profile.setValue(null);
            
            if (logoImagen_profile != null) {
                Image avatarActual = currentUser.getAvatar();
                if (avatarActual != null) {
                    logoImagen_profile.setImage(avatarActual);
                }
            }
        }
    }

    @FXML
    private void seleccionarNuevoAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cambiar Imagen de Avatar");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) imagenButton_profile.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            nuevoAvatarPath = file.getAbsolutePath();
            Image image = new Image(file.toURI().toString());
            logoImagen_profile.setImage(image);
        }
    }

    @FXML
    private void guardarCambios(ActionEvent event) {
        limpiarErrores();
        boolean formValido = true;

        String emailInput = mailField_profile.getText().trim();
        String emailFinal = currentUser.getEmail(); 
        if (!emailInput.isEmpty()) {
            if (!User.checkEmail(emailInput)) {
                mailError.setText("Formato de correo no válido.");
                formValido = false;
            } else {
                emailFinal = emailInput;
            }
        }

        String nuevaPassword = password_perfil.getText();
        String passwordFinal = currentUser.getPassword(); 
        if (!nuevaPassword.isEmpty()) {
            if (!User.checkPassword(nuevaPassword)) {
                passwordError.setText("Mínimo 8-20 chars, 1 mayúscula, 1 minúscula, 1 dígito y 1 símbolo.");
                formValido = false;
            } else {
                passwordFinal = nuevaPassword;
            }
        }

        LocalDate birthDateInput = datePicker_profile.getValue();
        LocalDate birthDateFinal = currentUser.getBirthDate();
        if (birthDateInput != null) {
            if (!User.isOlderThan(birthDateInput, 12)) {
                dateError.setText("Debes ser mayor de 12 años.");
                formValido = false;
            } else {
                birthDateFinal = birthDateInput;
            }
        }

        String avatarFinalPath = (nuevoAvatarPath != null) ? nuevoAvatarPath : currentUser.getAvatarPath();

        if (formValido) {
            try {
                boolean exito = app.updateCurrentUser(
                        emailFinal, 
                        passwordFinal, 
                        birthDateFinal, 
                        avatarFinalPath 
                );

                if (exito) {
                    volver(event);
                } else {
                    mailError.setText("Error al actualizar los datos en el sistema.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
     @FXML
    private void volver(ActionEvent event) {
        try {
            Stage stage = (Stage) returnButton_profile.getScene().getWindow();
            
            MapaDemoApp.cargarVista("/Vistas/vistaPerfil.fxml", stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiarErrores() {
        mailError.setText("");
        passwordError.setText("");
        dateError.setText("");
    }
}
