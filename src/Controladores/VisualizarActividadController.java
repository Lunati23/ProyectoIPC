/*
 * ============================================================
 *  PROYECTO EJEMPLO – IPC 2026
 *  Asignatura: Interfaces Persona-Computador
 *  Universitat Politècnica de València
 * ============================================================
 *
 *  DESCRIPCIÓN GENERAL
 *  -------------------
 *  Este controlador gestiona la vista principal de la aplicación
 *  de puntos de interés (POI) sobre un mapa.
 *
 *  Funcionalidades implementadas:
 *   1. Carga y visualización de una imagen de mapa.
 *   2. Zoom interactivo mediante un Slider.
 *   3. Añadir POIs (texto) y anotaciones (círculos) con clic derecho.
 *   4. Listado de POIs en un ListView con CellFactory personalizada.
 *   5. Centrado animado del mapa al seleccionar un POI de la lista.
 *   6. Modo inserción: activar con botón y colocar POI con siguiente clic.
 *
 *  PATRÓN UTILIZADO: MVC (Model-View-Controller)
 *   - Modelo : clase Poi  (datos del punto de interés)
 *   - Vista  : FXMLDocument.fxml  (layout declarativo)
 *   - Control: esta clase (lógica de interacción)
 *
 * ============================================================
 */
package Controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.Annotation;
import upv.ipc.sportlib.AnnotationType;
import upv.ipc.sportlib.GeoPoint;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.SportActivityApp;

/**
 * Controlador principal de la aplicación de mapa con POIs.
 *
 * La anotación @FXML conecta automáticamente los campos de esta clase
 * con los elementos declarados en el fichero FXML mediante su atributo fx:id.
 *
 * Implementa {@link Initializable} para poder ejecutar código de
 * inicialización una vez que el FXML ha sido cargado completamente.
 */
public class VisualizarActividadController implements Initializable {

    // =========================================================
    //  ESTRUCTURA DE NODOS PARA ZOOM
    // =========================================================
    //
    //  El zoom se consigue escalando un Group (zoomGroup).
    //  Escalar un Group NO desplaza los nodos que contiene,
    //  lo que evita el "salto" visual al hacer zoom.
    //
    //  Jerarquía de nodos:
    //
    //  ScrollPane (map_scrollpane)
    //   └─ contentGroup          ← Group raíz dentro del ScrollPane
    //       └─ zoomGroup         ← se escala para el zoom
    //           └─ mapPane       ← Pane con la imagen y los POIs
    //               ├─ ImageView ← imagen del mapa
    //               ├─ Text      ← etiquetas de POIs
    //               └─ Circle    ← anotaciones circulares
    //
    // =========================================================

    /** Group que se escala para aplicar el zoom. */
    private Group zoomGroup;

    /**
     * Pane que actúa como lienzo del mapa.
     * Contiene la imagen de fondo y todos los elementos superpuestos
     * (textos, círculos, etc.). Sus dimensiones coinciden con las de
     * la imagen cargada.
     */
    private Pane mapPane;

    
    /** Menú contextual reutilizable para el clic derecho sobre el mapa. */
    private ContextMenu mapContextMenu;


    /**
     * Indica si el controlador está en modo inserción de POI.
     * {@code true} → el próximo clic izquierdo sobre el mapa abre el diálogo.
     */
    private boolean insertionMode = false;

    // =========================================================
    //  ELEMENTOS FXML  (inyectados automáticamente por el cargador)
    // =========================================================

    /** Lista lateral que muestra todos los POIs añadidos al mapa. */
    @FXML
    private ListView<Annotation> map_listview;

    /** ScrollPane que envuelve el mapa y permite desplazarlo. */
    @FXML
    private ScrollPane map_scrollpane;

    /**
     * Slider de zoom.
     * Rango: [0.5 – 1.5]. Valor inicial: 1.0 (sin zoom).
     * Cada cambio de valor llama al método zoom().
     */
    @FXML
    private Slider zoom_slider;

    /**
     * Botón de pin visible sobre el mapa.
     * Se desplaza hasta la posición del POI seleccionado en la lista.
     */
    private MenuButton map_pin;

    // FIX 5 — Eliminadas las variables sin uso:
    //   · 'mousePosistion' (errata + duplicado de mousePosition)
    //   · 'pin_info'       (inyectada pero nunca actualizada)

    /** Etiqueta en la barra de estado que muestra las coordenadas del ratón. */
    @FXML
    private Label mousePosition;
    @FXML
    private Label nombreActividadLabel;
    @FXML
    private Label distancia;
    @FXML
    private Label duracion;
    @FXML
    private Label velocidad;
    @FXML
    private Label ritmo;
    @FXML
    private Label ascenso;
    @FXML
    private Label descenso;
    @FXML
    private Label altitudMin;
    @FXML
    private Label altitudMax;
 
    private Activity actividadActual;
    
    private MapProjection proyeccionMapa;

    // Para 2 puntos en modo CIRCLE y LINE 
    private AnnotationType tipoCapturaActual = null;
    private double primerClickX = -1;
    private double primerClickY = -1;
    
 
    // =========================================================
    //  MANEJADORES DE ZOOM
    // =========================================================

    /**
     * Aumenta el zoom en 0.1 unidades al pulsar el botón "+".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }

    /**
     * Reduce el zoom en 0.1 unidades al pulsar el botón "–".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    /**
     * Aplica el factor de escala al {@code zoomGroup}.
     *
     * Este método es invocado automáticamente cada vez que cambia el
     * valor del slider, gracias al listener registrado en {@link #initialize}.
     *
     * Truco: guardamos y restauramos los valores de scroll para que el
     * contenido visible no salte al cambiar la escala.
     *
     * @param scaleValue nuevo factor de escala (p. ej. 1.2 → 120 %)
     */
    private void zoom(double scaleValue) {
        // Guardamos la posición del scroll antes de escalar
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        // Aplicamos el zoom escalando el Group en ambos ejes
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        // Restauramos la posición del scroll para que el centro visual
        // permanezca estable durante el zoom
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    // =========================================================
    //  SELECCIÓN EN EL LISTVIEW → CENTRADO EN EL MAPA
    // =========================================================

    /**
     * Se ejecuta cuando el usuario hace clic en un elemento del ListView.
     *
     * Objetivo: centrar el ScrollPane sobre la posición del POI seleccionado
     * con una animación suave de 500 ms, y mover el pin al punto.
     *
     * Cálculo del scroll
     * ------------------
     * El ScrollPane expresa su posición como valores normalizados [0, 1]:
     *   · hValue = 0 → extremo izquierdo
     *   · hValue = 1 → extremo derecho
     *
     * Para centrar el POI necesitamos:
     *
     *   scrollH = (poiX_escalado - viewportAncho / 2)
     *             ─────────────────────────────────────
     *             (mapaAncho_escalado - viewportAncho)
     *
     * Aplicamos clamp para no salir del rango [0, 1].
     *
     * @param event evento de ratón sobre el ListView
     */
    
    
    @FXML
    void listClicked(MouseEvent event) {
        // Obtenemos la anotacion seleccionada; si no hay ninguno, salimos
        Annotation itemSelected = map_listview.getSelectionModel().getSelectedItem();
        if (itemSelected == null) return;
        if (itemSelected.getGeoPoints() == null || itemSelected.getGeoPoints().isEmpty()) {
            return;
        }

        // ── Dimensiones del mapa con el zoom actual aplicado ──────────
        double mapWidth  = mapPane.getWidth()  * zoomGroup.getScaleX();
        double mapHeight = mapPane.getHeight() * zoomGroup.getScaleY();

        // ── Posición escalada ──────────────────────────────────
        // 1. Proyectamos el GeoPoint de la marca a píxeles X e Y reales en la pantalla
        Point2D posPixel = proyeccionMapa.project(itemSelected.getGeoPoints().get(0));
        // getPosition() devuelve las coordenadas en el sistema local del
        // mapPane (sin zoom). Las multiplicamos por el factor de escala
        // para obtener la posición real en pantalla.
        double poiX = posPixel.getX() * zoomGroup.getScaleX();
        double poiY = posPixel.getY() * zoomGroup.getScaleY();

        // ── Tamaño visible del ScrollPane (viewport) ───────────────────
        double viewW = map_scrollpane.getViewportBounds().getWidth();
        double viewH = map_scrollpane.getViewportBounds().getHeight();

        // ── Cálculo del scroll normalizado [0, 1] ─────────────────────
        // Restamos la mitad del viewport para que el POI quede centrado
        // y no en la esquina superior-izquierda del área visible.
        double scrollH = (poiX - viewW / 2) / (mapWidth  - viewW);
        double scrollV = (poiY - viewH / 2) / (mapHeight - viewH);

        // Garantizamos que el valor esté dentro del rango válido [0, 1]
        scrollH = Math.max(0, Math.min(1, scrollH));
        scrollV = Math.max(0, Math.min(1, scrollV));

        // ── Animación suave con Timeline ──────────────────────────────
        // Timeline interpola los valores de las propiedades a lo largo
        // del tiempo. KeyValue define qué propiedad animar y hasta qué
        // valor; KeyFrame define en qué instante se alcanza ese valor.
        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
        final KeyFrame kf  = new KeyFrame(Duration.millis(500), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play(); // Inicia la animación (no bloquea el hilo de la UI)

    }
    
    
    // =========================================================
    //  CONSTRUCCIÓN DEL MAPA
    // =========================================================

    /**
     * Carga una imagen y construye la jerarquía de nodos del mapa.
     *
     * Este método puede llamarse varias veces (p. ej. al cambiar el mapa),
     * ya que sustituye completamente el contenido del ScrollPane.
     *
     * @param imgFile fichero de imagen a cargar como fondo del mapa
     */
    private void buildMap(File imgFile) {
        // Comprobación defensiva: si el fichero no existe mostramos un aviso
        if (!imgFile.exists()) {
            map_scrollpane.setContent(
                new Label("Imagen no encontrada: " + imgFile.getPath()));
            return;
        }

        // Cargamos la imagen y obtenemos sus dimensiones reales en píxeles
        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();

        // ── mapPane: lienzo del mapa ───────────────────────────────────
        // Usamos un Pane (y no un Group) para poder posicionar los nodos
        // hijos con coordenadas absolutas (setLayoutX / setLayoutY).
        mapPane = new Pane();
        mapPane.setPrefSize(W, H); // tamaño preferido = tamaño de la imagen
        mapPane.setMinSize(W, H);  // impedimos que el layout lo encoja
        mapPane.setMaxSize(W, H);  // impedimos que el layout lo agrande

        // Añadimos la imagen como fondo del Pane
        ImageView iv = new ImageView(img);
        iv.setFitWidth(W);
        iv.setFitHeight(H);
        mapPane.getChildren().add(iv);

        // ── Manejador de clics sobre el mapa ──────────────────────────
        // Gestionamos el clic derecho (menú contextual) y el clic izquierdo
        // en modo inserción (FIX 2).
        /*mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // Clic derecho → mostrar menú contextual
                onMapRightClick(e.getX(), e.getY());
                e.consume();

            } else if (e.getButton() == MouseButton.PRIMARY && insertionMode) {
                // FIX 2: clic izquierdo en modo inserción → añadir POI y desactivar modo
                insertionMode = false;
                mapPane.setStyle(""); // Restauramos el cursor normal
                //addPoi(e.getX(), e.getY());
                
            }
        });
        */
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume(); // Evita interferencias con el scroll
                // click derecho manejador
                clickDerecho(e);
            } else if (e.getButton() == MouseButton.PRIMARY) {
                // click izquierdo manejador
                clickIzquierdo(e);
            }
        });
        

        // ── Jerarquía de Groups para el zoom ──────────────────────────
        // contentGroup es el nodo raíz que recibe el ScrollPane.
        // zoomGroup es el que se escala; anidar un Group dentro de otro
        // evita que el ScrollPane reajuste su contenido durante el escalado.
        zoomGroup = new Group();
        Group contentGroup = new Group();
        zoomGroup.getChildren().add(mapPane);
        contentGroup.getChildren().add(zoomGroup);

        // Aplicamos el zoom actual (valor actual del slider)
        double zoom = zoom_slider.getValue();
        zoomGroup.setScaleX(zoom);
        zoomGroup.setScaleY(zoom);

        // Asignamos el contentGroup como contenido del ScrollPane
        map_scrollpane.setContent(contentGroup);

    }
    
    // =========================================================
    //  CLICK DERECHO - manejador
    // =========================================================
    
    private void clickDerecho(MouseEvent event) {
        // Si se estaban capturando 2 clics y se pulsa click derecho, cancelamos
        if (primerClickX != -1) {
            primerClickX = -1;
            primerClickY = -1;
            tipoCapturaActual = null;
            mapPane.setCursor(javafx.scene.Cursor.DEFAULT);
            mapPane.getChildren().removeIf(nodo -> "nodoTemporalUX".equals(nodo.getId()));
            return;
        }

        // Coordenadas quitando zoom
        double xReal = event.getX() / zoomGroup.getScaleX();
        double yReal = event.getY() / zoomGroup.getScaleY();

        onMapRightClick(xReal, yReal);
    }
    
    // =========================================================
    //  CLICK IZQUIERDO - manejador
    // =========================================================

    
    private void clickIzquierdo(MouseEvent event) {
        // Si hay un primer clic guardado, significa que este es el segundo click
        if (primerClickX != -1 && primerClickY != -1) {
            event.consume();

            double segundoClickX = event.getX() / zoomGroup.getScaleX();
            double segundoClickY = event.getY() / zoomGroup.getScaleY();

            // Quitar puntos de guía visual de la pantalla y cursor default en lugar de X
            mapPane.setCursor(javafx.scene.Cursor.DEFAULT);
            mapPane.getChildren().removeIf(nodo -> "nodoTemporalUX".equals(nodo.getId()));

            // Guardar primer click y tipo anotacion
            double x1 = primerClickX;
            double y1 = primerClickY;
            AnnotationType tipoAnotacion = tipoCapturaActual;

            // Reseteamos al primer click = -1 y anotacion nula , para proxima anotacion
            primerClickX = -1;
            primerClickY = -1;
            tipoCapturaActual = null;

            // Abrimos ventana de anotacion despues de guardar los clicks
            abrirVentanaAnotacion(x1, y1, segundoClickX, segundoClickY, tipoAnotacion);

        } else if (insertionMode) {
            insertionMode = false;
            mapPane.setStyle(""); // Restauramos el cursor normal
        }
    }
    

    // =========================================================
    //  MENÚ CONTEXTUAL (clic derecho sobre el mapa)
    // =========================================================

    /**
     * Muestra el menú contextual reutilizable en la posición del clic.
     *
     * Las acciones de los MenuItem se actualizan con las coordenadas
     * del clic actual antes de mostrar el menú.
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     */
    private void onMapRightClick(double x, double y) {
        // FIX 6: cerramos el menú si ya estaba visible (evita instancias flotantes)
        mapContextMenu.hide();

        // Actualizamos las acciones de los items con las coordenadas actuales.
        // Usamos variables final para que el lambda pueda capturarlas.
        final double clickX = x;
        final double clickY = y;
        // punto - no hace falta segundo click, directamente abre ventana
        mapContextMenu.getItems().get(0).setOnAction(e -> abrirVentanaAnotacion(x, y, -1, -1, AnnotationType.POINT));
        // circulo - prepara para segundo click
        mapContextMenu.getItems().get(1).setOnAction(e -> segundoClick(clickX, clickY, AnnotationType.CIRCLE));
        // texto - no hace falta segundo click, directamente abre ventana
        mapContextMenu.getItems().get(2).setOnAction(e -> abrirVentanaAnotacion(x, y, -1, -1, AnnotationType.TEXT));
        // linea - prepara para segundo click
        mapContextMenu.getItems().get(3).setOnAction(e -> segundoClick(clickX, clickY, AnnotationType.LINE));

        // Mostramos el menú en coordenadas de pantalla
        mapContextMenu.show(
            mapPane.getScene().getWindow(),
            mapPane.localToScreen(x, y).getX(),
            mapPane.localToScreen(x, y).getY()
        );
    }
    
    // =========================================================
    //  INDICA QUE SE ESPERA EL SEGUNDO CLICK
    // =========================================================
    
    private void segundoClick(double x, double y, AnnotationType a) {
        this.tipoCapturaActual = a;
        this.primerClickX = x;
        this.primerClickY = y;
        // Cursor en X
        mapPane.setCursor(javafx.scene.Cursor.CROSSHAIR); 
        colocarPuntoGuiaTemporal(x, y);
    }
    
    // =========================================================
    //  PUNTO DONDE SE HIZO PRIMER CLICK PARA AYUDAR PARA SEGUNDO
    // =========================================================

    
    // Método auxiliar de feedback visual
    private void colocarPuntoGuiaTemporal(double x, double y) {
        Circle temporal = new Circle(3, Color.BLACK);
        temporal.setCenterX(x);
        temporal.setCenterY(y);
        temporal.setId("nodoTemporalUX");
        mapPane.getChildren().add(temporal);
    }

    // =========================================================
    //  INICIALIZACIÓN DEL CONTROLADOR
    // =========================================================

    /**
     * Método llamado automáticamente por el FXMLLoader tras inyectar
     * todos los elementos {@code @FXML}.
     *
     * Aquí configuramos:
     *  - El slider de zoom y su listener.
     *  - El ContextMenu reutilizable (FIX 6).
     *  - La CellFactory del ListView (FIX 4).
     *  - La carga del mapa inicial.
     *
     * @param url  URL del documento FXML (no usado aquí)
     * @param rb   paquete de recursos de internacionalización (no usado aquí)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ── Configuración del slider de zoom ──────────────────────────
        zoom_slider.setMin(0.5);   // zoom mínimo: 50 %
        zoom_slider.setMax(1.5);   // zoom máximo: 150 %
        zoom_slider.setValue(1.0); // valor inicial: 100 %

        // Listener que invoca zoom() cada vez que el slider cambia de valor.
        // Usamos una expresión lambda en lugar de una clase anónima por brevedad.
        zoom_slider.valueProperty().addListener(
            (observable, oldVal, newVal) -> zoom((Double) newVal)
        );

        // Los items se crean aquí sin acción; las acciones se asignan
        // en onMapRightClick() con las coordenadas correctas de cada clic.
        MenuItem miPoint  = new MenuItem("📍 Añadir punto");      
        MenuItem miCircle = new MenuItem("⭕ Añadir círculo");    
        MenuItem miText   = new MenuItem("📝 Añadir texto");      
        MenuItem miLine   = new MenuItem("➖ Añadir línea");      

        mapContextMenu = new ContextMenu(miPoint, miCircle, miText, miLine);

        //  setCellFactory() define cómo se renderiza cada celda
        //  de forma independiente al modelo Poi.
        //  Aquí mostramos "CÓDIGO – Nombre" en cada fila.
        /*
        map_listview.setCellFactory(listView -> new ListCell<Poi>() {
            @Override
            protected void updateItem(Poi poi, boolean empty) {
                // Siempre llamar a super primero (requerido por JavaFX)
                super.updateItem(poi, empty);

                if (empty || poi == null) {
                    // Celda vacía: limpiamos texto y gráfico
                    setText(null);
                    setGraphic(null);
                } else {
                    // Mostramos código y nombre separados por un guión largo
                    setText(poi.getCode() + " – " + poi.getPosition());
                }
            }
        });
        */
        
        // Cambiamos Poi por Annotation y añadimos los tipos 
        map_listview.setCellFactory(listView -> new ListCell<Annotation>() {
            @Override
            protected void updateItem(Annotation anotacion, boolean empty) {
                super.updateItem(anotacion, empty);
                if (empty || anotacion == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String icono = "📝 ";
                    if (anotacion.getType() == AnnotationType.CIRCLE) icono = "⭕ ";
                    if (anotacion.getType() == AnnotationType.LINE) icono = "➖ ";
                    if (anotacion.getType() == AnnotationType.POINT) icono = "📍 ";

                    setText(icono + anotacion.getText());
                }
            }
        });
        

        // ── Carga del mapa inicial ─────────────────────────────────────
        // El fichero se busca relativo al directorio de trabajo del proyecto.
        buildMap(new File("maps/upv.jpg"));
        
    }

    // =========================================================
    //  INDICADOR DE POSICIÓN DEL RATÓN
    // =========================================================

    /**
     * Actualiza la etiqueta {@code mousePosition} con las coordenadas
     * actuales del ratón, tanto en el sistema de la escena como en el
     * sistema local del nodo sobre el que se mueve.
     *
     * Útil para depuración y para que los alumnos comprendan la diferencia
     * entre coordenadas de escena y coordenadas locales.
     *
     * @param event evento de movimiento del ratón
     */
    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText(
            "sceneX: " + (int) event.getSceneX() +
            ", sceneY: " + (int) event.getSceneY() + "\n" +
            "         X: " + (int) event.getX() +
            ",          Y: " + (int) event.getY()
        );
    }

    // =========================================================
    //  DIÁLOGO "ACERCA DE"
    // =========================================================

    /**
     * Muestra un diálogo informativo con datos de la asignatura.
     *
     * Nota: accedemos al Stage del diálogo para poder personalizar
     * su icono, ya que Alert no expone directamente esa propiedad.
     *
     * @param event evento de acción del menú
     */
    /*
    private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);

        // Personalizamos el icono de la ventana del diálogo
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/resources/logo.png"))
        );

        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2026");
        mensaje.showAndWait(); // Bloquea hasta que el usuario cierra el diálogo
    }
    
    // =========================================================
    //  AÑADIR UN POI (texto) AL MAPA
    // =========================================================

    /**
     * Muestra un diálogo para introducir el nombre del nuevo POI,
     * lo añade al ListView y dibuja su etiqueta sobre el mapa.
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     */
    
    /*
    private void addPoi(double x, double y) {

        // ── Construcción del diálogo personalizado ────────────────────
        Dialog<Poi> poiDialog = new Dialog<>();
        poiDialog.setTitle("Nuevo POI");
        poiDialog.setHeaderText("Introduce un nuevo POI");

        // Personalizamos el icono de la ventana del diálogo
        Stage dialogStage = (Stage) poiDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/resources/logo.png"))
        );

        // Botones del diálogo: Aceptar y Cancelar
        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        poiDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        // Campo de texto para el nombre del POI
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del POI");

        // Layout del contenido del diálogo (VBox con espaciado de 10 px)
        VBox vbox = new VBox(10, new Label("Nombre:"), nameField);
        poiDialog.getDialogPane().setContent(vbox);

        // ResultConverter: transforma la selección del botón en un objeto Poi.
        // FIX 1: ya no usamos coordenadas provisionales (0,0); pasamos (x,y)
        // directamente al constructor para que el modelo sea coherente desde el inicio.
        poiDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Poi(nameField.getText().trim(), x, y);
            }
            return null;
        });

        // Mostramos el diálogo y esperamos la respuesta del usuario
        Optional<Poi> result = poiDialog.showAndWait();

        if (result.isPresent()) {
            Poi poi = result.get();

            // FIX 1: confirmamos la posición como Point2D para compatibilidad
            // con getPosition(), usando las mismas coordenadas (x, y).
            poi.setPosition(new Point2D(x, y));

            // Añadimos el POI al ListView (la CellFactory mostrará nombre y código)
            map_listview.getItems().add(poi);

            // FIX 1: usamos (x, y) tanto para el modelo como para el Text,
            // garantizando que la etiqueta aparezca exactamente donde se hizo clic.
            Text text = new Text(poi.getCode());
            text.setX(x);
            text.setY(y);
            mapPane.getChildren().add(text);
        }
    }
    */

    // =========================================================
    //  CAMBIAR EL MAPA (selector de fichero)
    // =========================================================

    /**
     * Abre un selector de fichero para que el usuario elija una imagen
     * diferente como mapa y reconstruye toda la vista.
     *
     * FIX 3: se comprueba que imgFile no sea null antes de usarlo,
     * evitando NullPointerException cuando el usuario cierra el FileChooser
     * sin seleccionar ningún fichero.
     *
     * @param event evento de acción del menú
     * @throws IOException si hay un problema al obtener la ruta canónica
     */
    @FXML
    private void cambiarMapa(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(".")); // Empezamos en el directorio del proyecto

        File imgFile = fc.showOpenDialog(zoom_slider.getScene().getWindow());

        // FIX 3: showOpenDialog() devuelve null si el usuario cancela la selección
        if (imgFile != null) {
            System.out.println("Mapa seleccionado: " + imgFile.getCanonicalPath());
            buildMap(imgFile); // Reconstruimos la vista con la nueva imagen
            map_listview.getItems().clear(); // Borramos los datos del mapa anterior
        }
    }

    // =========================================================
    //  AÑADIR UN CÍRCULO
    // =========================================================

    private void addCircle(double centroX, double centroY, double bordeX, double bordeY, Color color) {
        Circle circulo = new Circle();
        circulo.setCenterX(centroX);
        circulo.setCenterY(centroY);
        
        // Calcula radio por dist cartesiana
        double radio = Math.hypot(bordeX - centroX, bordeY - centroY);
        circulo.setRadius(radio);
        
        // circulo transparente solo con borde = color picker
        circulo.setFill(Color.TRANSPARENT);      
        circulo.setStroke(color);                
        circulo.setStrokeWidth(2.5);
        
        mapPane.getChildren().add(circulo);
    }
    // =========================================================
    //  AÑADIR UN PUNTO
    // =========================================================
    private void addPoint(double x, double y, Color color) {
        Circle point = new Circle(5, color); // radio = 5 px, color = rojo
        point.setCenterX(x);
        point.setCenterY(y);
        mapPane.getChildren().add(point); // Se añade sobre el mapa como cualquier nodo
    }
    
    // =========================================================
    //  AÑADIR LINEA
    // =========================================================
    private void addLine(double startX, double startY, double endX, double endY, Color color) {
        javafx.scene.shape.Line linea = new Line();
        linea.setStartX(startX);
        linea.setStartY(startY);
        linea.setEndX(endX);
        linea.setEndY(endY);
        
        linea.setStroke(color);
        linea.setStrokeWidth(3.5);
        
        mapPane.getChildren().add(linea);
    }
    
    // =========================================================
    //  AÑADIR TEXTO
    // =========================================================
    private void addText(double x, double y, Color color, String mensaje) {
        Text texto = new Text(mensaje); // radio = 10 px, color = rojo
        texto.setFill(color);
        texto.setX(x); 
        texto.setY(y);
        mapPane.getChildren().add(texto); // Se añade sobre el mapa como cualquier nodo
    }
    
    // =========================================================
    //  AÑADIR ESTADÍSTICAS, ANOTACIONES Y RUTA EN MAPA
    // =========================================================

    public void setActivity(Activity actividad) {
        this.actividadActual = actividad;
        nombreActividadLabel.setText(actividad.getName());
        
        // Calculamos estadísticas 
        
        double km = actividad.getTotalDistance() / 1000.0;
        distancia.setText(String.format("%.2f km", km));

        long h = actividad.getDuration().toHours();
        long m = actividad.getDuration().toMinutesPart();
        long s = actividad.getDuration().toSecondsPart();
        duracion.setText(String.format("%02d:%02d:%02d", h, m, s));

        velocidad.setText(String.format("%.1f km/h", actividad.getAverageSpeed()));

        double ritmoMedio = actividad.getAveragePace();
        int ritmoMinutos = (int) ritmoMedio;
        int ritmoSegundos = (int) ((ritmoMedio - ritmoMinutos) * 60);
        ritmo.setText(String.format("%d:%02d /km", ritmoMinutos, ritmoSegundos));

        ascenso.setText(String.format("%.0f m", actividad.getElevationGain()));
        descenso.setText(String.format("%.0f m", actividad.getElevationLoss()));
        altitudMin.setText(String.format("%.0f m", actividad.getMinElevation()));
        altitudMax.setText(String.format("%.0f m", actividad.getMaxElevation()));

        // Mapa de actividad 
        if (actividad.getSuggestedMap() != null) {
            // ruta fichero
            File archivoMapa = new File(actividad.getSuggestedMap().getImagePath());
            buildMap(archivoMapa);

            this.proyeccionMapa = new MapProjection(
                actividad.getSuggestedMap(), 
                mapPane.getPrefWidth(), 
                mapPane.getPrefHeight()
            );

            // Recorrido
            pintarRuta();

            // Anotaciones previas
            cargarAnotacionesPrevias();
        }
    }
    // =========================================================
    //  PINTA EL CAMINO EN EL MAPA
    // =========================================================

    private void pintarRuta() {
        if (actividadActual == null || proyeccionMapa == null) return;

        List<Point2D> puntosPixeles = proyeccionMapa.projectActivity(actividadActual);

        if (puntosPixeles == null || puntosPixeles.isEmpty()) return;

        javafx.scene.shape.Polyline camino = new javafx.scene.shape.Polyline();

        camino.setStroke(Color.web("#3B82F6"));
        camino.setStrokeWidth(3.5);
        camino.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        camino.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        for (Point2D punto : puntosPixeles) {
            camino.getPoints().addAll(punto.getX(), punto.getY());
        }

        mapPane.getChildren().add(camino);
    }
    // =========================================================
    //  ANOTACIONES DE BASE DE DATOS - CARGAR 
    // =========================================================

    
    private void cargarAnotacionesPrevias() {
        if (actividadActual == null || actividadActual.getAnnotations() == null) return;

        map_listview.getItems().setAll(actividadActual.getAnnotations());

        for (Annotation anotacion : actividadActual.getAnnotations()) {
            if (anotacion.getGeoPoints() != null && !anotacion.getGeoPoints().isEmpty()) {

                Point2D p1 = proyeccionMapa.project(anotacion.getGeoPoints().get(0));

                Color colorMarca = Color.web(anotacion.getColor());

                switch (anotacion.getType()) {
                
                    case POINT -> {
                        addPoint(p1.getX(), p1.getY(), colorMarca);
                    }

                    case CIRCLE -> {
                        if (anotacion.getGeoPoints().size() >= 2) {
                            Point2D p2 = proyeccionMapa.project(anotacion.getGeoPoints().get(1));
                            addCircle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), colorMarca);
                        }
                    }

                    case TEXT -> {
                        addText(p1.getX(), p1.getY(), colorMarca, anotacion.getText());
                    }

                    case LINE -> {
                        if (anotacion.getGeoPoints().size() >= 2) {
                            Point2D p2 = proyeccionMapa.project(anotacion.getGeoPoints().get(1));
                            addLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), colorMarca);
                        }
                    }

                    default -> {
                    }
            
                }
            }
        }
    }

    // =========================================================
    //  VENTANA DE ANOTACIÓN
    // =========================================================

    private void abrirVentanaAnotacion(double x, double y, double x2, double y2, AnnotationType tipo) {
        if (actividadActual == null || proyeccionMapa == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/AnadirAnotacion.fxml"));
            javafx.scene.Parent root = loader.load();

            AnadirAnotacionController controladorSecundario = loader.getController();
            controladorSecundario.setTipoAnotacion(tipo);

            Stage stageModal = new Stage();
            stageModal.setTitle("Añadir anotación");
            stageModal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stageModal.setScene(new javafx.scene.Scene(root));
            stageModal.setResizable(false);
            stageModal.showAndWait();

            if (controladorSecundario.isOKPressed()) {
                Annotation anotacionTemporal = controladorSecundario.getNuevaAnotacion();

                if (anotacionTemporal != null) {
                    List<GeoPoint> listaPuntos = new ArrayList<>();

                    // Siempre hay primer punto
                    GeoPoint puntoGeografico = proyeccionMapa.unproject(x, y);
                    listaPuntos.add(puntoGeografico);

                    // Si hay segundo punto, se desproyecta y se añade
                    if (x2 != -1 && y2 != -1) {
                        GeoPoint puntoGeografico2 = proyeccionMapa.unproject(x2, y2);
                        listaPuntos.add(puntoGeografico2);
                    }

                    Annotation anotacionDefinitiva = new Annotation(
                        anotacionTemporal.getType(),
                        anotacionTemporal.getText(),
                        anotacionTemporal.getColor(),
                        anotacionTemporal.getStrokeWidth(),
                        listaPuntos
                    );

                    // Se guarda en la base de datos 
                    SportActivityApp.getInstance().addAnnotation(actividadActual, anotacionDefinitiva);

                    // Actualisza vista con base de datos 
                    map_listview.getItems().setAll(actividadActual.getAnnotations());

                    // Limpia map pane y regenera todo 
                    mapPane.getChildren().clear(); 
                    buildMap(new File(actividadActual.getSuggestedMap().getImagePath())); 
                    pintarRuta(); 
                    cargarAnotacionesPrevias(); 
                }
            }

        } catch (IOException ex) {
            System.err.println("Error abriendo el diálogo: " + ex.getMessage());
        }
    }
}
