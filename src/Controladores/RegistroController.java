/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import App.MainApp;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.User;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author vinte
 */
public class RegistroController implements Initializable {

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
        limpiarErrores();

        imagenButton_register.setOnAction(this::seleccionarAvatar);
        registerButton.setOnAction(this::registrarUsuario);

    }

    @FXML
    private void seleccionarAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Avatar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) imagenButton_register.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String path = file.getAbsolutePath();
            fotoPath.setText(path);

            Image image = new Image(file.toURI().toString());
            logoImagen.setImage(image);
        }
    }

    @FXML
    private void registrarUsuario(ActionEvent event) {
        limpiarErrores();
        boolean formValido = true;

        String nickname = nickname_register.getText().trim();
        String password = password_register.getText();
        String email = mail_register.getText().trim();
        LocalDate birthDate;
        birthDate = date_register.getValue();
        String avatarPath = fotoPath.getText().isEmpty() ? null : fotoPath.getText();

        if (!User.checkNickName(nickname)) {
            nicknameError.setText("Debe tener entre 6 y 15 caracteres (letras, dígitos, -, _).");
            formValido = false;
        }

        if (!User.checkEmail(email)) {
            mailError.setText("Formato de correo no válido.");
            formValido = false;
        }

        if (!User.checkPassword(password)) {
            paswordError.setText("Mínimo 8-20 chars, 1 mayúscula, 1 minúscula, 1 dígito y 1 símbolo.");
            formValido = false;
        }

        if (birthDate == null) {
            dateError.setText("Debes seleccionar una fecha.");
            formValido = false;
        } else if (!User.isOlderThan(birthDate, 12)) {
            dateError.setText("Debes ser mayor de 12 años.");
            formValido = false;
        }

        if (formValido) {
            SportActivityApp app = SportActivityApp.getInstance();

            try {
                boolean exito = app.registerUser(nickname, email, password, birthDate, avatarPath);

                if (exito) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registro completado");
                    alert.setHeaderText(null);
                    
                    alert.setGraphic(null);
                    
                    Label mensaje = new Label("¡Registrado correctamente!");
                    alert.getDialogPane().setContent(mensaje);
                    
                    URL cssUrl = getClass().getResource("/EstilosVariados/estilos.css");
                    if (cssUrl != null) {
                        alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
                    }
                    
                    ButtonType btnContinuar = new ButtonType("Continuar al login");
                    alert.getButtonTypes().setAll(btnContinuar);
                    
                    alert.showAndWait().ifPresent(response -> {
                        if (response == btnContinuar) {
                            try {
                                MainApp.cargarLogin();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                } else {
                    nicknameError.setText("El nickname ya está en uso.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void limpiarErrores() {
        nicknameError.setText("");
        paswordError.setText("");
        mailError.setText("");
        dateError.setText("");
    }

    @FXML
    private void irLogin(ActionEvent event) throws Exception {
        App.MainApp.cargarLogin(); 
    }
}
