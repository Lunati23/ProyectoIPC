package Controladores;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

public class ImportarActividadesController implements Initializable {

    @FXML
    private Button btnImportar;

    @FXML
    private Button btnSeleccionar;

    @FXML
    private Button btnCancel;

    @FXML
    private Label lblName;

    @FXML
    private Label lblDistance;

    @FXML
    private Label lblDuration;

    @FXML
    private Label lblDrop;

    @FXML
    private StackPane previewArea;

    @FXML
    private ScrollPane map_scrollpane;

    @FXML
    private Pane mapPane;

    @FXML
    private VBox addInfo;

    private File archivoSeleccionado;
    private Activity actividadImportada;
    private MapProjection proj;

    private final SportActivityApp app = SportActivityApp.getInstance();
    @FXML
    private VBox ImportBox;
    @FXML
    private ImageView Mapa;
    @FXML
    private ImageView imgPreview;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnImportar.setDisable(true);

        // Estado inicial
        map_scrollpane.setVisible(false);
        map_scrollpane.setManaged(false);

        addInfo.setVisible(true);
        addInfo.setManaged(true);
    }

    // =====================================================
    // SELECCIONAR ARCHIVO
    // =====================================================
    @FXML
    private void seleccionarArchivo() {

        // Evitar volver a importar encima
        if (actividadImportada != null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccionar archivo GPX");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GPX files", "*.gpx")
        );

        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Downloads")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            return;
        }

        archivoSeleccionado = file;

        try {

            actividadImportada = app.importActivity(file);

            if (actividadImportada == null) {

                lblDrop.setText("Error: actividad inválida");
                btnImportar.setDisable(true);

                return;
            }

            // ======================================
            // CAMBIAR A MODO PREVIEW
            // ======================================
            addInfo.setVisible(false);
            addInfo.setManaged(false);

            map_scrollpane.setVisible(true);
            map_scrollpane.setManaged(true);

            // Ya no permitir más clicks
            previewArea.setOnMouseClicked(null);

            buildMap();

            // ======================================
            // INFO
            // ======================================
            lblName.setText(actividadImportada.getName());

            double km = actividadImportada.getTotalDistance() / 1000.0;

            lblDistance.setText(
                    String.format("%.2f km", km)
            );

            Duration d = actividadImportada.getDuration();

            lblDuration.setText(
                    String.format(
                            "%02d:%02d:%02d",
                            d.toHours(),
                            d.toMinutesPart(),
                            d.toSecondsPart()
                    )
            );

            btnImportar.setDisable(false);

        } catch (Exception e) {

            lblDrop.setText("Error importando GPX");
            e.printStackTrace();
        }
    }

    // =====================================================
    // CONSTRUIR MAPA
    // =====================================================
    private void buildMap() {

        MapRegion region = actividadImportada.getSuggestedMap();

        File file = new File(region.getImagePath());

        if (!file.exists()) {

            lblDrop.setText(
                    "Imagen no encontrada: " + file.getPath()
            );

            return;
        }

        Image img = new Image(file.toURI().toString());

        double W = img.getWidth();
        double H = img.getHeight();

        proj = new MapProjection(region, W, H);

        mapPane = new Pane();

        mapPane.setPrefSize(W, H);
        mapPane.setMinSize(W, H);
        mapPane.setMaxSize(W, H);

        map_scrollpane.setContent(mapPane);

        // Fondo mapa
        ImageView iv = new ImageView(img);

        iv.setFitWidth(W);
        iv.setFitHeight(H);

        mapPane.getChildren().add(iv);

        pintarRuta();
    }

    // =====================================================
    // PINTAR RUTA
    // =====================================================
    private void pintarRuta() {

        if (actividadImportada == null || proj == null) {
            return;
        }

        List<Point2D> puntosPixeles
                = proj.projectActivity(actividadImportada);

        if (puntosPixeles == null || puntosPixeles.isEmpty()) {
            return;
        }

        Polyline camino = new Polyline();

        camino.setStroke(Color.web("#3B82F6"));
        camino.setStrokeWidth(3.5);

        camino.setStrokeLineCap(
                javafx.scene.shape.StrokeLineCap.ROUND
        );

        camino.setStrokeLineJoin(
                javafx.scene.shape.StrokeLineJoin.ROUND
        );

        for (Point2D punto : puntosPixeles) {

            camino.getPoints().addAll(
                    punto.getX(),
                    punto.getY()
            );
        }

        mapPane.getChildren().add(camino);

        centrarRuta(puntosPixeles);
    }

    // =====================================================
    // CENTRAR RUTA
    // =====================================================
    private void centrarRuta(List<Point2D> puntosPixeles) {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point2D p : puntosPixeles) {

            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());

            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;

        Platform.runLater(() -> {

            double viewportW
                    = map_scrollpane.getViewportBounds().getWidth();

            double viewportH
                    = map_scrollpane.getViewportBounds().getHeight();

            double contentW = mapPane.getWidth();
            double contentH = mapPane.getHeight();

            double hValue
                    = (centerX - viewportW / 2)
                    / (contentW - viewportW);

            double vValue
                    = (centerY - viewportH / 2)
                    / (contentH - viewportH);

            hValue = Math.max(0, Math.min(hValue, 1));
            vValue = Math.max(0, Math.min(vValue, 1));

            map_scrollpane.setHvalue(hValue);
            map_scrollpane.setVvalue(vValue);
        });
    }

    // =====================================================
    // IMPORTAR ACTIVIDAD
    // =====================================================
    @FXML
    private void importarActividad() {

        if (actividadImportada == null) {
            return;
        }

        System.out.println(
                "Actividad importada: "
                + actividadImportada.getName()
        );

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Vistas/VisualizarActividadVista.fxml")
            );

            Node vista = loader.load();

            // Obtener el controlador del FXML cargado
            VisualizarActividadController controller = loader.getController();

            // Pasar la actividad al nuevo controlador
            controller.setActivity(actividadImportada);

            ImportBox.getChildren().clear();
            ImportBox.getChildren().add(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnImportar.setDisable(true);
    }

    // =====================================================
    // CANCELAR
    // =====================================================
    @FXML
    private void cancelarImportacion() {

        // =====================================
        // ELIMINAR ACTIVIDAD TEMPORAL
        // =====================================
        try {

            if (actividadImportada != null) {
                app.removeActivity(actividadImportada);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        archivoSeleccionado = null;
        actividadImportada = null;
        proj = null;

        // =====================================
        // LIMPIAR UI
        // =====================================
        lblName.setText("--");
        lblDistance.setText("--");
        lblDuration.setText("--");

        lblDrop.setText(
                "Haz click para seleccionar un archivo GPX"
        );

        btnImportar.setDisable(true);

        // =====================================
        // VOLVER A MODO IMPORTACIÓN
        // =====================================
        map_scrollpane.setVisible(false);
        map_scrollpane.setManaged(false);

        addInfo.setVisible(true);
        addInfo.setManaged(true);

        map_scrollpane.setContent(null);

        // Reactivar click
        previewArea.setOnMouseClicked(
                event -> seleccionarArchivo()
        );
    }
}
