package upb.rutas.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import upb.rutas.algoritmo.Dijkstra;
import upb.rutas.algoritmo.RutaResultado;
import upb.rutas.modelo.Arista;
import upb.rutas.modelo.Grafo;
import upb.rutas.modelo.Nodo;
import upb.rutas.persistencia.GrafoPersistencia;
import upb.rutas.servicio.LugarServicio;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/** Vista principal JavaFX: cálculo de rutas, búsqueda por lugar y gestión de bloqueos (RF-01..RF-09). */
public class RutaView {
    private final Grafo grafo; 
    private final LugarServicio lugarServicio;
    private final GrafoPersistencia persistencia;
    private final Path archivoDatos;

    private final ComboBox<Nodo> origenCombo = new ComboBox<>();
    private final ComboBox<Nodo> destinoCombo = new ComboBox<>();
    private final CheckBox accesibleCheck = new CheckBox("Ruta accesible (evitar escaleras)");
    private final Label resultadoLabel = new Label("Seleccione origen y destino");

    private final TextField lugarField = new TextField();
    private final Label lugarResultadoLabel = new Label();

    private final ComboBox<Arista> arcoCombo = new ComboBox<>();
    private final Label bloqueoEstadoLabel = new Label();
    private final MapaGrafoView mapaGrafo;

    public RutaView(Grafo grafo, LugarServicio lugarServicio, GrafoPersistencia persistencia, Path archivoDatos) {
        this.grafo = grafo;
        this.lugarServicio = lugarServicio;
        this.persistencia = persistencia;
        this.archivoDatos = archivoDatos;
        this.mapaGrafo = new MapaGrafoView(grafo);
    }

    public TabPane construirVista() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("app-tabs");
        tabPane.getTabs().add(new Tab("Calcular ruta", construirTabRuta()));
        tabPane.getTabs().add(new Tab("Lugares", construirTabLugares()));
        tabPane.getTabs().add(new Tab("Gestionar bloqueos", construirTabBloqueos()));
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));
        return tabPane;
    }

    private VBox construirTabRuta() {
        origenCombo.getItems().setAll(grafo.getNodos());
        destinoCombo.getItems().setAll(grafo.getNodos());
        if (!origenCombo.getItems().isEmpty()) {
            origenCombo.setValue(origenCombo.getItems().get(0));
        }
        if (destinoCombo.getItems().size() > 1) {
            destinoCombo.setValue(destinoCombo.getItems().get(destinoCombo.getItems().size() - 1));
        }

        Button calcularButton = new Button("Calcular ruta");
        calcularButton.getStyleClass().add("primary-button");
        calcularButton.setOnAction(e -> calcularRuta());

        Label titulo = new Label("Encuentra tu ruta");
        titulo.getStyleClass().add("page-title");
        Label subtitulo = new Label("Selecciona dos puntos del campus y calcula el recorrido más corto.");
        subtitulo.getStyleClass().add("page-subtitle");
        Label origenLabel = new Label("Punto de origen");
        origenLabel.getStyleClass().add("field-label");
        Label destinoLabel = new Label("Punto de destino");
        destinoLabel.getStyleClass().add("field-label");
        resultadoLabel.getStyleClass().add("result-card");
        origenCombo.setMaxWidth(Double.MAX_VALUE);
        destinoCombo.setMaxWidth(Double.MAX_VALUE);

        VBox formulario = new VBox(12,
            titulo,
            subtitulo,
            origenLabel, origenCombo,
            destinoLabel, destinoCombo,
            accesibleCheck,
            calcularButton,
            resultadoLabel
        );
        formulario.getStyleClass().add("form-card");
        formulario.setPrefWidth(350);

        VBox mapaPanel = mapaGrafo.construirVista();
        HBox contenido = new HBox(22, formulario, mapaPanel);
        HBox.setHgrow(mapaPanel, Priority.ALWAYS);
        mapaPanel.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(12, contenido);
        root.getStyleClass().add("content-shell");
        return root;
    }

    private void calcularRuta() {
        Nodo origen = origenCombo.getValue();
        Nodo destino = destinoCombo.getValue();

        if (origen == null || destino == null) {
            resultadoLabel.setText("Debe seleccionar un origen y un destino.");
            return;
        }

        Dijkstra dijkstra = new Dijkstra(grafo);
        RutaResultado ruta = dijkstra.calcularRuta(origen, destino, accesibleCheck.isSelected());

        if (!ruta.isEncontrada()) {
            resultadoLabel.setText("No existe una ruta disponible para ese recorrido.");
            mapaGrafo.limpiarRuta();
            return;
        }

        StringBuilder texto = new StringBuilder("Ruta: ");
        for (Nodo nodo : ruta.getRuta()) {
            texto.append(nodo.getNombre()).append(" -> ");
        }
        texto.setLength(texto.length() - 4);
        texto.append("  |  total: ").append(String.format("%.2f", ruta.getDistancia())).append(" pasos");
        resultadoLabel.setText(texto.toString());
        mapaGrafo.resaltarRuta(ruta.getRuta());
    }

    private VBox construirTabLugares() {
        lugarField.setPromptText("Ej: Biblioteca");
        Button buscarButton = new Button("Buscar lugar");
        buscarButton.getStyleClass().add("primary-button");
        buscarButton.setOnAction(e -> {
            Optional<Nodo> nodo = lugarServicio.buscarNodoPorLugar(lugarField.getText());
            if (nodo.isPresent()) {
                lugarResultadoLabel.setText("Lugar ubicado en: " + nodo.get().getNombre());
                destinoCombo.setValue(nodo.get());
            } else {
                lugarResultadoLabel.setText("No se encontró ese lugar. Puede registrarlo abajo.");
            }
        });

        ComboBox<Nodo> nodoParaLugarCombo = new ComboBox<>();
        nodoParaLugarCombo.getItems().setAll(grafo.getNodos());
        nodoParaLugarCombo.setMaxWidth(Double.MAX_VALUE);
        TextField nombreLugarField = new TextField();
        nombreLugarField.setPromptText("Nombre del lugar a registrar");
        Button registrarButton = new Button("Registrar lugar en edificio");
        registrarButton.getStyleClass().add("secondary-button");
        registrarButton.setOnAction(e -> {
            Nodo nodo = nodoParaLugarCombo.getValue();
            String nombre = nombreLugarField.getText();
            if (nodo == null || nombre == null || nombre.isBlank()) {
                lugarResultadoLabel.setText("Debe indicar un nombre y un edificio/nodo.");
                return;
            }
            lugarServicio.registrarLugar(nombre.trim(), nodo.getId());
            lugarResultadoLabel.setText("Lugar '" + nombre.trim() + "' registrado en " + nodo.getNombre());
            nombreLugarField.clear();
            guardarSilencioso();
        });

        Label titulo = new Label("Busca por lugar");
        titulo.getStyleClass().add("page-title");
        Label subtitulo = new Label("Encuentra un destino sin tener que conocer el edificio.");
        subtitulo.getStyleClass().add("page-subtitle");
        Label registrarTitulo = new Label("Registrar un lugar nuevo");
        registrarTitulo.getStyleClass().add("section-title");
        lugarResultadoLabel.getStyleClass().add("result-card");

        Label nombreLugarLabel = new Label("Nombre del lugar");
        nombreLugarLabel.getStyleClass().add("field-label");
        VBox busqueda = new VBox(12,
            nombreLugarLabel,
                lugarField, buscarButton, lugarResultadoLabel,
            new Label("Consulta también disponible desde la pestaña Calcular ruta.")
        );
        busqueda.getStyleClass().add("form-card");
        VBox registro = new VBox(12,
            registrarTitulo,
            new Label("Nombre del lugar"), nombreLugarField,
            new Label("Edificio o nodo donde se encuentra"), nodoParaLugarCombo,
            registrarButton
        );
        registro.getStyleClass().add("form-card");
        VBox root = new VBox(12, titulo, subtitulo, busqueda, registro);
        root.getStyleClass().add("content-shell");
        return root;
    }

    private VBox construirTabBloqueos() {
        arcoCombo.getItems().setAll(grafo.getAristasUnicas());
        arcoCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Arista a) {
                return a == null ? "" : a.getOrigen().getNombre() + " <-> " + a.getDestino().getNombre()
                        + (a.isBloqueada() ? " [BLOQUEADO]" : "");
            }

            @Override
            public Arista fromString(String string) {
                return null;
            }
        });

        Button bloquearButton = new Button("Bloquear tramo");
        Button desbloquearButton = new Button("Desbloquear tramo");
        bloquearButton.getStyleClass().add("primary-button");
        desbloquearButton.getStyleClass().add("secondary-button");

        bloquearButton.setOnAction(e -> cambiarBloqueo(true));
        desbloquearButton.setOnAction(e -> cambiarBloqueo(false));

        Label titulo = new Label("Gestiona los caminos");
        titulo.getStyleClass().add("page-title");
        Label subtitulo = new Label("Bloquea temporalmente un tramo para que no sea usado por Dijkstra.");
        subtitulo.getStyleClass().add("page-subtitle");
        Label tramoLabel = new Label("Selecciona un tramo");
        tramoLabel.getStyleClass().add("field-label");
        bloqueoEstadoLabel.getStyleClass().add("status-text");
        arcoCombo.setMaxWidth(Double.MAX_VALUE);
        HBox panel = new HBox(10, bloquearButton, desbloquearButton);

        VBox formulario = new VBox(12,
            tramoLabel,
                arcoCombo,
            panel,
                bloqueoEstadoLabel
        );
        formulario.getStyleClass().add("form-card");
        VBox root = new VBox(12, titulo, subtitulo, formulario);
        root.getStyleClass().add("content-shell");
        return root;
    }

    private void cambiarBloqueo(boolean bloqueada) {
        Arista arista = arcoCombo.getValue();
        if (arista == null) {
            bloqueoEstadoLabel.setText("Seleccione un tramo primero.");
            return;
        }
        grafo.setArcoBloqueado(arista.getOrigen(), arista.getDestino(), bloqueada);
        bloqueoEstadoLabel.setText("Tramo " + arista.getOrigen().getNombre() + " <-> "
                + arista.getDestino().getNombre() + (bloqueada ? " bloqueado." : " desbloqueado."));
        arcoCombo.getItems().setAll(grafo.getAristasUnicas());
        guardarSilencioso();
    }

    private void guardarSilencioso() {
        try {
            persistencia.guardarGrafo(grafo, lugarServicio.getLugares(), archivoDatos);
        } catch (IOException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo guardar el archivo de datos: " + ex.getMessage());
            alerta.showAndWait();
        }
    }
}
