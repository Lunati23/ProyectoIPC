/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author marco
 */
public class GestionDeMapasController implements Initializable {

    @FXML
    private ListView<MapRegion> mapListView;
    @FXML
    private Button btnDeleteMap;
    @FXML
    private Button lblBrowseMap;
    @FXML
    private Label lblMapFile;
    @FXML
    private TextField txtLatMin;
    @FXML
    private TextField txtLatmax;
    @FXML
    private TextField txtlonMin;
    @FXML
    private TextField txtLonMax;
    @FXML
    private Button btnSaveMap;
    

    private File rutaImagenSeleccionada; 

  
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // 1. Instanciar la app (la librería mágica)
     SportActivityApp app = SportActivityApp.getInstance(); 
    
    // 2. Obtener la lista de mapas registrados
     List<MapRegion> listaMapas = app.getMapRegions(); 
    
    // 3. Convertirla a un formato que entienda JavaFX
     ObservableList<MapRegion> mapasObservable = FXCollections.observableArrayList(listaMapas);
     mapListView.setItems(mapasObservable);
    
    // 4. Decirle al ListView que muestre el nombre del mapa (si no, mostrará un código raro de memoria)
     mapListView.setCellFactory(param -> new ListCell<MapRegion>() {
         @Override
         protected void updateItem(MapRegion map, boolean empty) {
             super.updateItem(map, empty);
            if (empty || map == null) {
                setText(null);
            } else {
                setText(map.getName()); 
            }
        }
    });

    }    
    @FXML
private void buscarImagen(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar imagen de mapa");
    // Filtramos para que solo deje elegir JPGs, como pide la práctica
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Archivos de Imagen JPG", "*.jpg", "*.jpeg")
    );
    
    // Abre la ventana de selección
    File archivoElegido = fileChooser.showOpenDialog(null);
    
    if (archivoElegido != null) {
        // Si elige un archivo, guardamos la ruta y mostramos el nombre en la etiqueta
        rutaImagenSeleccionada = archivoElegido;
        lblMapFile.setText(archivoElegido.getName());
    }
}
}
