package Controladores;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import javafx.scene.input.KeyCode;

import javafx.scene.layout.VBox;

import javafx.scene.text.Text;

import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;

public class ListaDeActividadesController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnCancelDelete;

    @FXML
    private Button btnConfirmDelete;

    @FXML
    private Button btnOpen;

    @FXML
    private ListView<Activity> activitiesListView;

    @FXML
    private VBox ListBox;

    private final SportActivityApp app = SportActivityApp.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // MULTI-SELECTION SIEMPRE
        activitiesListView.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);

        // Cargar actividades
        activitiesListView.getItems().setAll(
                app.getUserActivities()
        );

        // Estado inicial botones
        btnDelete.setDisable(true);
        btnOpen.setDisable(true);

        btnCancelDelete.setVisible(false);
        btnConfirmDelete.setVisible(false);

        // Listener selección
        activitiesListView.getSelectionModel()
                .getSelectedItems()
                .addListener((ListChangeListener<Activity>) c -> {
                    updateButtons();
                });

        // =========================
        // CELL FACTORY
        // =========================
        activitiesListView.setCellFactory(listView -> new ListCell<Activity>() {

            @Override
            protected void updateItem(Activity activity, boolean empty) {

                super.updateItem(activity, empty);

                if (empty || activity == null) {

                    setText(null);
                    setGraphic(null);

                } else {

                    VBox vbox = new VBox(4);

                    Text titulo = new Text(activity.getName());
                    titulo.getStyleClass().add("activity-title");

                    double km = activity.getTotalDistance() / 1000.0;

                    long h = activity.getDuration().toHours();
                    long m = activity.getDuration().toMinutesPart();

                    Text detalles = new Text(
                            String.format("%.1f km · %dh %dm", km, h, m)
                    );

                    detalles.getStyleClass().add("activity-details");

                    vbox.getChildren().addAll(
                            titulo,
                            detalles
                    );

                    setGraphic(vbox);
                }
            }
        });

        // DOBLE CLICK
        activitiesListView.setOnMouseClicked(e -> {

            if (e.getClickCount() == 2) {
                tryOpen();
            }
        });

        // ENTER
        activitiesListView.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.ENTER) {
                tryOpen();
            }
        });
    }

    // =========================
    // BOTONES
    // =========================
    private void updateButtons() {

        int size = activitiesListView.getSelectionModel()
                .getSelectedItems()
                .size();

        // Eliminar: mínimo 1
        btnDelete.setDisable(size == 0);

        // Acceder: exactamente 1
        btnOpen.setDisable(size != 1);
    }

    // =========================
    // ABRIR ACTIVIDAD
    // =========================
    @FXML
    private void accederActividad() {
        tryOpen();
    }

    private void tryOpen() {

        var seleccionadas = activitiesListView
                .getSelectionModel()
                .getSelectedItems();

        if (seleccionadas.size() == 1) {

            cargarVistaActividad(
                    seleccionadas.get(0)
            );
        }
    }

    private void cargarVistaActividad(Activity actividad) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/Vistas/VisualizarActividadVista.fxml"
                    )
            );

            Node vista = loader.load();

            VisualizarActividadController controller
                    = loader.getController();

            controller.setActivity(actividad);

            ListBox.getChildren().clear();
            ListBox.getChildren().add(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // DELETE FLOW
    // =========================
    @FXML
    private void btnDelete_Action() {

        btnCancelDelete.setVisible(true);
        btnConfirmDelete.setVisible(true);

        btnDelete.setDisable(true);
        btnOpen.setDisable(true);
    }

    @FXML
    private void btnCancelDelete_Action() {

        btnCancelDelete.setVisible(false);
        btnConfirmDelete.setVisible(false);

        updateButtons();
    }

    @FXML
    private void btnConfirmDelete_Action() {

        var seleccionadas = activitiesListView
                .getSelectionModel()
                .getSelectedItems();
        for (Activity actividad : seleccionadas) {
            app.removeActivity(actividad);
        }

        activitiesListView.getItems().setAll(
                app.getUserActivities()
        );

        activitiesListView.getSelectionModel()
                .clearSelection();

        btnCancelDelete.setVisible(false);
        btnConfirmDelete.setVisible(false);

        updateButtons();
    }
}
