/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import App.MainApp;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author vinte
 */
public class HistorialSesionesController implements Initializable {

    @FXML
    private Button volver_historial;
    @FXML
    private Label sesionesLabel;
    @FXML
    private Label importadasLabel;
    @FXML
    private Label visualizadasLabel;
    @FXML
    private Label anotacionesLabel;
    @FXML
    private Button cerrarSesionButton;
    @FXML
    private TableView<Session> tableView_historial;
    @FXML
    private TableColumn<Session, String> inicioColumn;
    @FXML
    private TableColumn<Session, String> finColumn;
    @FXML
    private TableColumn<Session, String> fechaColumn;
    @FXML
    private TableColumn<Session, String> duracionColumn;
    @FXML
    private TableColumn<Session, String> tiempoColumn;
    @FXML
    private TableColumn<Session, String> importadasColumn;
    @FXML
    private TableColumn<Session, String> vistasColumn;
    @FXML
    private TableColumn<Session, String> anotacionesColumn;

    private SportActivityApp app;
    private User currentUser;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        app = SportActivityApp.getInstance();
        currentUser = app.getCurrentUser();
        
        
        if (currentUser != null) {
            cargarDatosSesiones();
        }
    }    

    private void cargarDatosSesiones() {
        List<Session> sesiones = currentUser.getSessions();
        ObservableList<Session> obsSesiones = FXCollections.observableArrayList(sesiones);
        
        int totalImportadas = 0;
        int totalVistas = 0;
        int totalAnotaciones = 0;
        
        for (Session s : sesiones) {
            totalImportadas += s.getImportedActivities();
            totalVistas += s.getViewedActivities();
            totalAnotaciones += s.getAnnotationsCreated();
        }
        
        sesionesLabel.setText(String.valueOf(sesiones.size()));
        importadasLabel.setText(String.valueOf(totalImportadas));
        visualizadasLabel.setText(String.valueOf(totalVistas));
        anotacionesLabel.setText(String.valueOf(totalAnotaciones));
        
        fechaColumn.setCellValueFactory(cellData -> {
            LocalDateTime start = cellData.getValue().getStartTime();
            return new SimpleStringProperty(start != null ? start.format(dateFormatter) : "N/A");
        });
        
        inicioColumn.setCellValueFactory(cellData -> {
            LocalDateTime start = cellData.getValue().getStartTime();
            return new SimpleStringProperty(start != null ? start.format(timeFormatter) : "N/A");
        });
        
        finColumn.setCellValueFactory(cellData -> {
            LocalDateTime end = cellData.getValue().getEndTime();
            return new SimpleStringProperty(end != null ? end.format(timeFormatter) : "En curso");
        });
        
        duracionColumn.setCellValueFactory(cellData -> {
            Duration duration = cellData.getValue().getDuration();
            return new SimpleStringProperty(formatearDuracion(duration));
        });
        
        if (tiempoColumn != null) {
            tiempoColumn.setCellValueFactory(cellData -> {
                Duration duration = cellData.getValue().getDuration();
                return new SimpleStringProperty(formatearDuracion(duration));
            });
        }
        
        importadasColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getImportedActivities()))
        );
        
        vistasColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getViewedActivities()))
        );
        
        anotacionesColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getAnnotationsCreated()))
        );
        
        tableView_historial.setItems(obsSesiones);
    }
    private String formatearDuracion(Duration duration) {
        if (duration == null || duration.isZero()) return "00:00:00";
        long s = duration.getSeconds();
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

    
    @FXML
    private void cerrarSesion(ActionEvent event) throws Exception {
        app.logout();
        MainApp.cargarLogin();
    }
    
    
}
