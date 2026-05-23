/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import upv.ipc.sportlib.Annotation;
import upv.ipc.sportlib.AnnotationType;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author paola
 */
public class AnadirAnotacionController implements Initializable {

    @FXML
    private TextField descripcion;
    @FXML
    private ColorPicker colorPicker;
    private AnnotationType tipoAsignado;
    private Annotation nuevaAnotacion = null;
    private boolean okPressed = false;
    @FXML
    private Button guardar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // inicializa con color de la APP 
        colorPicker.setValue(Color.web("#7C3AED"));
        guardar.disableProperty().bind(descripcion.textProperty().isEmpty());
    }    

    @FXML
    private void accionCancelar(ActionEvent event) {
        okPressed = false;
        cerrarVentana(event);
    }

    @FXML
    private void accionAceptar(ActionEvent event) {
        // Transformar color a formato hexadecimal
        Color c = colorPicker.getValue();
        String colorHex = String.format("#%02X%02X%02X", 
            (int)(c.getRed() * 255), 
            (int)(c.getGreen() * 255), 
            (int)(c.getBlue() * 255));

        // Creamos nueva anotacion (sin geopoints -> se ponen al volver a la otra)
        nuevaAnotacion = new Annotation(
            tipoAsignado,
            descripcion.getText().trim(),
            colorHex,
            3.0,
            null
        );

        okPressed = true;
        cerrarVentana(event);
    }

    void setTipoAnotacion(AnnotationType tipo) {
        this.tipoAsignado = tipo;
    }

    Annotation getNuevaAnotacion() {
        return nuevaAnotacion;
    }

    boolean isOKPressed() {
        return okPressed;
    }
    
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
