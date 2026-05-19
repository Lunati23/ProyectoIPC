package mapademo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MapaDemoApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        cargarVista("/Vistas/VisualizarActividad.fxml", stage);

        stage.setTitle("SaforRun");
       
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void cargarVista(String fxml, Stage stage) throws Exception {
        FXMLLoader loader =
                new FXMLLoader(MapaDemoApp.class.getResource(fxml));

        Parent root = loader.load();

        Scene scene = new Scene(root, 850, 650);
 
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}

