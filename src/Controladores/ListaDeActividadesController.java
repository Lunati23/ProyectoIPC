package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;

public class ListaDeActividadesController implements Initializable {

    @FXML
    private Button btnDeleteMode;
    @FXML
    private Button btnCancelDelete;
    @FXML
    private Button btnConfirmDelete;
    @FXML
    private ListView<Activity> activitiesListView; // Añade esto al FXML

    private final SportActivityApp app = SportActivityApp.getInstance();
    @FXML
    private VBox ListBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Cargar actividades de la BD
        var actividades = app.getUserActivities();
        activitiesListView.getItems().setAll(actividades);

        // Personalizar cómo se muestra cada actividad
        activitiesListView.setCellFactory(listView -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                if (empty || activity == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Crear el layout del item manualmente
                    VBox vbox = new VBox(4);
                    vbox.setStyle("-fx-padding: 12; -fx-border-color: #ddd; -fx-border-radius: 5;");

                    Text titulo = new Text(activity.getName());
                    titulo.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

                    double km = activity.getTotalDistance() / 1000.0;
                    long h = activity.getDuration().toHours();
                    long m = activity.getDuration().toMinutesPart();

                    Text detalles = new Text(
                            String.format("%.1f km · %dh %dm", km, h, m)
                    );
                    detalles.setStyle("-fx-font-size: 12; -fx-fill: #666;");

                    vbox.getChildren().addAll(titulo, detalles);
                    setGraphic(vbox);
                }
            }
        });

        // Click en una actividad → cargar vista
        activitiesListView.setOnMouseClicked(event -> {
            Activity actividadSeleccionada = activitiesListView.getSelectionModel().getSelectedItem();
            if (actividadSeleccionada != null) {
                cargarVistaActividad(actividadSeleccionada);
            }
        });
    }

    private void cargarVistaActividad(Activity actividad) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Vistas/VisualizarActividadVista.fxml")
            );

            Node vista = loader.load();

            VisualizarActividadController controller = loader.getController();
            controller.setActivity(actividad); // Pasarle la actividad

            // Cambiar contenido del contenedor padre (ajusta según tu estructura)
            ListBox.getChildren().clear();
            ListBox.getChildren().add(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
