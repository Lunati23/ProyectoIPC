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
    private TextField txtNombre; // ¡Variable vital añadida!
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
        // 1. Instanciar la app (la librería de la asignatura)
        SportActivityApp app = SportActivityApp.getInstance(); 
        
        // 2. Obtener la lista de mapas registrados
        List<MapRegion> listaMapas = app.getMapRegions(); 
        
        // 3. Convertirla a un formato que entienda JavaFX
        ObservableList<MapRegion> mapasObservable = FXCollections.observableArrayList(listaMapas);
        mapListView.setItems(mapasObservable);
        
        // 4. Decirle al ListView que muestre el nombre del mapa
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
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de Imagen JPG", "*.jpg", "*.jpeg")
        );
        
        File archivoElegido = fileChooser.showOpenDialog(null);
        
        if (archivoElegido != null) {
            rutaImagenSeleccionada = archivoElegido;
            lblMapFile.setText(archivoElegido.getName());
        }
    }

    @FXML
    private void guardarMapa(ActionEvent event) {
        if (txtNombre.getText().trim().isEmpty() || rutaImagenSeleccionada == null) {
            System.out.println("Error: Falta el nombre o la imagen del mapa.");
            return;
        }

        try {
            double latMin = Double.parseDouble(txtLatMin.getText().trim());
            double latMax = Double.parseDouble(txtLatmax.getText().trim());
            double lonMin = Double.parseDouble(txtlonMin.getText().trim());
            double lonMax = Double.parseDouble(txtLonMax.getText().trim());

            SportActivityApp app = SportActivityApp.getInstance();
            app.addMapRegion(txtNombre.getText().trim(), rutaImagenSeleccionada, latMin, latMax, lonMin, lonMax);
            System.out.println("¡Mapa guardado con éxito!");

            txtNombre.clear();
            txtLatMin.clear();
            txtLatmax.clear();
            txtlonMin.clear();
            txtLonMax.clear();
            lblMapFile.setText("Ningún archivo...");
            rutaImagenSeleccionada = null;

            mapListView.getItems().clear();
            mapListView.getItems().addAll(app.getMapRegions());

        } catch (NumberFormatException e) {
            System.out.println("Error: Las coordenadas deben ser números decimales válidos.");
        }
    }

    @FXML
    private void eliminarMapa(ActionEvent event) {
        MapRegion mapaSeleccionado = mapListView.getSelectionModel().getSelectedItem();

        if (mapaSeleccionado == null) {
            System.out.println("Aviso: Selecciona un mapa de la lista para poder borrarlo.");
            return;
        }

        SportActivityApp app = SportActivityApp.getInstance();
        // Borrar pasándole el objeto directamente (¡corregido!)
        app.removeMapRegion(mapaSeleccionado);
        System.out.println("¡Mapa eliminado con éxito!");

        mapListView.setItems(FXCollections.observableArrayList(app.getMapRegions()));
    }
}
