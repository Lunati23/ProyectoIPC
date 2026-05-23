package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        SportActivityApp.getInstance(); 
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
    public static void main(String[] args) {
        launch(args);
    }
}