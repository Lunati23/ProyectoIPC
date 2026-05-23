package Controladores;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.Activity;

import upv.ipc.sportlib.SportActivityApp;

public class VistaImportarActividadesController implements Initializable {

    @FXML private Button btnImportar;

    @FXML private Label lblName;
    @FXML private Label lblDistance;
    @FXML private Label lblDuration;
    @FXML private Label lblDrop;

    private File archivoSeleccionado;
    private Activity actividadImportada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnImportar.setDisable(true);
    }

    // =========================
    // SELECCIONAR GPX
    // =========================
    @FXML
    private void seleccionarArchivo() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo GPX");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("GPX files", "*.gpx")
        );

        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home") + "/Downloads")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file == null) return;

        archivoSeleccionado = file;

        try {

            SportActivityApp app = SportActivityApp.getInstance();

            actividadImportada = app.importActivity(file);

            if (actividadImportada == null) {
                lblDrop.setText("Error: actividad inválida");
                btnImportar.setDisable(true);
                return;
            }

            // =========================
            // MOSTRAR INFO REAL
            // =========================

            lblDrop.setText("Archivo cargado correctamente");

            lblName.setText(
                actividadImportada.getName()
            );

            // metros → km
            double km = actividadImportada.getTotalDistance() / 1000.0;

            lblDistance.setText(String.format("%.2f km", km));

            // Duration → HH:mm:ss
            Duration d = actividadImportada.getDuration();

            long h = d.toHours();
            long m = d.toMinutesPart();
            long s = d.toSecondsPart();

            lblDuration.setText(
                String.format("%02d:%02d:%02d", h, m, s)
            );

            btnImportar.setDisable(false);

        } catch (Exception e) {

            lblDrop.setText("Error importando GPX");
            e.printStackTrace();
        }
    }

    // =========================
    // IMPORTAR FINAL
    // =========================
    @FXML
    private void importarActividad() {

        if (actividadImportada == null) return;

        System.out.println("Actividad importada: " +
            actividadImportada.getName()
        );

        btnImportar.setDisable(true);
    }

    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void cancelarImportacion() {

        archivoSeleccionado = null;
        actividadImportada = null;

        lblName.setText("--");
        lblDistance.setText("--");
        lblDuration.setText("--");
        lblDrop.setText("Haz click para seleccionar un archivo GPX");

        btnImportar.setDisable(true);
    }
}