package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import App.MainApp;
import upv.ipc.sportlib.SportActivityApp;

public class VistaLoginController implements Initializable {

    @FXML
    private TextField nickname_login;
    @FXML
    private PasswordField password_login;
    @FXML
    private Label nickname_error;
    @FXML
    private Label password_error;
    @FXML
    private Button button_login;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void login(ActionEvent event) throws Exception {

        String user = nickname_login.getText();
        String pass = password_login.getText();

        SportActivityApp app = SportActivityApp.getInstance();

        boolean ok = app.login(user, pass);

        if (ok) {
            MainApp.cargarPrincipal(); // cambiar vista
        }
    }

    @FXML
    private void irRegistro(ActionEvent event) {
        System.out.println("Ir a registro (pendiente de implementar)");
    }
}
