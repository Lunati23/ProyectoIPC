package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import App.MainApp;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

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
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void login(ActionEvent event) throws Exception {

        String user = nickname_login.getText();
        String pass = password_login.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            password_error.setText("Por favor, rellena ambos campos.");
            return;
        }

        SportActivityApp app = MainApp.getApp();
        boolean ok = app.login(user, pass);

        if (ok) {
            MainApp.cargarPrincipal(); // cambiar vista
        } else {
            password_error.setText("Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void irRegistro(ActionEvent event) throws Exception {
        MainApp.cargarRegistro();
    }
    
    
}   
