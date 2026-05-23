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
                getClass().getResource("/Vistas/VistaLogin.fxml")
        );

        stage.setScene(new Scene(root));
        stage.setTitle("SaforRun");
        stage.show();
    }

    public static void cargarPrincipal() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/VistaPrincipal.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }

    public static void cargarRegistro() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/vistaRegister.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }

    public static void cargarLogin() throws Exception {

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/Vistas/vistaLogin.fxml")
        );

        primaryStage.getScene().setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static SportActivityApp getApp() {
        return app;
    }
}
