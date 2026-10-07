package upb.rutas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import upb.rutas.modelo.Grafo;
import upb.rutas.modelo.Lugar;
import upb.rutas.persistencia.GrafoPersistencia;
import upb.rutas.servicio.LugarServicio;
import upb.rutas.ui.RutaView;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        GrafoPersistencia persistencia = new GrafoPersistencia();
        Path archivoDatos = resolverArchivoDatos();

        Grafo grafo;
        List<Lugar> lugares;
        try {
            grafo = persistencia.cargarGrafo(archivoDatos);
            lugares = persistencia.cargarLugares(archivoDatos);
        } catch (IOException | RuntimeException ex) {
            // RNF-06: no debe fallar silenciosamente ni con excepción no controlada.
            Alert alerta = new Alert(Alert.AlertType.ERROR,
                    "No se pudo cargar el archivo de datos del campus (" + ex.getMessage() + ").");
            alerta.showAndWait();
            stage.close();
            return;
        }

        LugarServicio lugarServicio = new LugarServicio(grafo);
        lugarServicio.cargarLugares(lugares);

        RutaView rutaView = new RutaView(grafo, lugarServicio, persistencia, archivoDatos);
        TabPane root = rutaView.construirVista();
        String hojaEstilos = getClass().getResource("/styles/app.css").toExternalForm();
        root.getStylesheets().add(hojaEstilos);

        stage.setTitle("Sistema de Rutas UPB");
        stage.setScene(new Scene(root, 980, 700));
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.show();
    }

    private Path resolverArchivoDatos() {
        try {
            return Path.of(getClass().getResource("/data/grafo_upb.json").toURI());
        } catch (URISyntaxException | NullPointerException ex) {
            throw new UncheckedIOException(new IOException("Recurso data/grafo_upb.json no encontrado", ex));
        }
    }
}
