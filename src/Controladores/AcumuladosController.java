package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import upv.ipc.sportlib.Activity;

public class AcumuladosController implements Initializable {

    @FXML
    private Text txtDistancia;
    @FXML
    private Text txtTiempo;
    @FXML
    private Text txtAscenso;
    @FXML
    private Text txtDescenso;

    private final SportActivityApp app = SportActivityApp.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        calcularAcumulados();
    }

    private void calcularAcumulados() {
        User usuarioActual = app.getCurrentUser();

        if (usuarioActual == null) {
            return;
        }

        var actividades = app.getUserActivities();

        double distanciaTotal = 0;
        long tiempoTotal = 0;
        double ascensoTotal = 0;
        double descendoTotal = 0;

        for (Activity actividad : actividades) {
            distanciaTotal += actividad.getTotalDistance();
            tiempoTotal += actividad.getDuration().toSeconds();
            ascensoTotal += actividad.getElevationGain();
            descendoTotal += actividad.getElevationLoss();
        }

        // Convertir a unidades apropiadas
        double distanciaKm = distanciaTotal / 1000.0;

        long horas = tiempoTotal / 3600;
        long minutos = (tiempoTotal % 3600) / 60;
        long segundos = tiempoTotal % 60;

        // Mostrar en los Text
        txtDistancia.setText(String.format("%.2f km", distanciaKm));
        txtTiempo.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));
        txtAscenso.setText(String.format("%.0f m", ascensoTotal));
        txtDescenso.setText(String.format("%.0f m", descendoTotal));
    }
}
