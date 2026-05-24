package Controladores;

import App.MainApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import upv.ipc.sportlib.SportActivityApp;

public class PrincipalController implements Initializable {

    @FXML
    private StackPane contentArea;
    @FXML
    private HBox btnGpx;
    @FXML
    private HBox btnAct;
    @FXML
    private HBox btnMapas;
    @FXML
    private HBox btnHist;
    @FXML
    private HBox btnAcum;
    @FXML
    private Button cerrarSesionButton;

    private SportActivityApp app = SportActivityApp.getInstance();
    private HBox botónActivo = null;
    private ImageView logoImagen_profile;
    @FXML
    private HBox btnAcum1;
    @FXML
    private Text AvatarText;
    @FXML
    private Circle Avatar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        var usuarioActual = app.getCurrentUser();

        if (usuarioActual != null) {
            // Mostrar nombre del usuario
            AvatarText.setText(usuarioActual.getNickName());

            // Cargar avatar si existe
            Image avatarActual = usuarioActual.getAvatar();
            if (avatarActual != null) {
                Avatar.setFill(new javafx.scene.paint.ImagePattern(avatarActual));
            } else {
                // Avatar por defecto
                Image defaultAvatar = new Image(getClass().getResourceAsStream("/logos/logo_usuario.png"));
                Avatar.setFill(new javafx.scene.paint.ImagePattern(defaultAvatar));
            }
        }
    }

    public void cargarVista(String fxml) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxml)
            );

            Node vista = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirMapas() {
        cargarVista("/Vistas/GestionDeMapasVista.fxml");
        cambiarBotónActivo(btnMapas);
    }

    @FXML
    private void abrirGPX() {
        cargarVista("/Vistas/ImportarActividadVista.fxml");
        cambiarBotónActivo(btnGpx);
    }

    @FXML
    private void abrirActividades() {
        cargarVista("/Vistas/ListaDeActividadesVista.fxml");
        cambiarBotónActivo(btnAct);
    }

    @FXML
    private void abrirHistorial() {
        cargarVista("/Vistas/HistorialSesionesVista.fxml");
        cambiarBotónActivo(btnHist);
    }

    @FXML
    private void abrirAcumulados() {
        cargarVista("/Vistas/AcumuladosVista.fxml");
        cambiarBotónActivo(btnAcum);
    }

    @FXML
    private void IrPerfil() throws Exception {
        MainApp.cargarPerfil();
    }

    private void cambiarBotónActivo(HBox nuevoBoton) {
        // Remover estilo del botón anterior
        if (botónActivo != null) {
            botónActivo.getStyleClass().remove("side-item-selected");
        }

        // Añadir estilo al nuevo botón
        nuevoBoton.getStyleClass().add("side-item-selected");
        botónActivo = nuevoBoton;
    }

    @FXML
    private void cerrarSesion(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("¿Cerrar sesión?");

        URL cssUrl = getClass().getResource("/EstilosVariados/estilos.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }

        ButtonType btnCerrar = new ButtonType("Cerrar sesión", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnCerrar, btnCancelar);

        Button cerrarButton = (Button) alert.getDialogPane().lookupButton(btnCerrar);

        cerrarButton.getStyleClass().add("danger-button");

        alert.showAndWait().ifPresent(response -> {
            if (response == btnCerrar) {
                try {
                    app.logout();
                    MainApp.cargarLogin();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
