package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

public class MainApp extends Application {

    private static SportActivityApp app = SportActivityApp.getInstance();

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Parent root = FXMLLoader.load(
                getClass().getResource("/Vistas/LoginVista.fxml")
        );

        stage.setScene(new Scene(root));
        stage.setTitle("SaforRun");
        stage.show();
    }

    public static void cargarPrincipal() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/PrincipalVista.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }

    public static void cargarRegistro() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/RegistroVista.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }

    public static void cargarLogin() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/LoginVista.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }
    
    public static void cargarPerfil() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/PerfilVista.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
