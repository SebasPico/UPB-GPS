package upb.rutas.ui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import upb.rutas.modelo.Arista;
import upb.rutas.modelo.Grafo;
import upb.rutas.modelo.Nodo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Mapa interactivo del grafo: los nodos pueden arrastrarse con el mouse. */
public class MapaGrafoView {
    private static final double RADIO_NODO = 22;
    private static final double ANCHO_MAPA = 980;
    private static final double ALTO_MAPA = 610;

    private final Grafo grafo;  //Guardar posicion en un archivo
    private final Pane lienzo = new Pane();
    private final Map<String, Circle> circulos = new HashMap<>();
    private final Map<String, Label> etiquetas = new HashMap<>();
    private final Map<String, Line> lineas = new HashMap<>();
    private final Map<String, double[]> posiciones = new HashMap<>();
    private final Group capaAristas = new Group();
    private final Group capaNodos = new Group();

    public MapaGrafoView(Grafo grafo) {
        this.grafo = grafo;
        configurarPosiciones();
        construirMapa();
    }

    public VBox construirVista() {
        ScrollPane scrollPane = new ScrollPane(lienzo);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.setPrefViewportWidth(980);
        scrollPane.setPrefViewportHeight(540);

        Label titulo = new Label("Mapa de conexiones");
        titulo.getStyleClass().add("page-title");
        Label instruccion = new Label("Arrastra los nodos para organizar el mapa. Las líneas se actualizan automáticamente.");
        instruccion.getStyleClass().add("page-subtitle");
        Label leyenda = new Label("Azul: nodo   |   Rojo: escaleras   |   Gris: tramo bloqueado   |   Verde: ruta calculada");
        leyenda.getStyleClass().add("legend-text");
        HBox toolbar = new HBox(14, leyenda);
        toolbar.getStyleClass().add("map-toolbar");

        VBox root = new VBox(titulo, instruccion, toolbar, scrollPane);
        root.getStyleClass().add("map-shell");
        return root;
    }

    public void resaltarRuta(List<Nodo> ruta) {
        Set<String> nodosRuta = new HashSet<>();
        Set<String> aristasRuta = new HashSet<>();
        for (Nodo nodo : ruta) {
            nodosRuta.add(nodo.getId());
        }
        for (int i = 0; i + 1 < ruta.size(); i++) {
            aristasRuta.add(claveArista(ruta.get(i).getId(), ruta.get(i + 1).getId()));
        }

        for (Map.Entry<String, Circle> entry : circulos.entrySet()) {
            entry.getValue().setFill(nodosRuta.contains(entry.getKey()) ? Color.LIMEGREEN : Color.LIGHTBLUE);
        }
        for (Map.Entry<String, Line> entry : lineas.entrySet()) {
            entry.getValue().setStroke(aristasRuta.contains(entry.getKey()) ? Color.LIMEGREEN : colorArista(entry.getKey()));
            entry.getValue().setStrokeWidth(aristasRuta.contains(entry.getKey()) ? 5 : 2);
        }
    }

    public void limpiarRuta() {
        resaltarRuta(List.of());
    }

    private void construirMapa() {
        lienzo.setPrefSize(ANCHO_MAPA, ALTO_MAPA);
        lienzo.getStyleClass().add("grafo-canvas");
        lienzo.getChildren().addAll(capaAristas, capaNodos);

        for (Arista arista : grafo.getAristasUnicas()) {
            String clave = claveArista(arista.getOrigen().getId(), arista.getDestino().getId());
            Line linea = new Line();
            linea.setStroke(colorArista(arista));
            linea.setStrokeWidth(2);
            linea.setUserData(arista);
            lineas.put(clave, linea);
            capaAristas.getChildren().add(linea);
        }

        for (Nodo nodo : grafo.getNodos()) {
            double[] posicion = posiciones.getOrDefault(nodo.getId(), new double[]{100, 100});
            Circle circulo = new Circle(RADIO_NODO, Color.LIGHTBLUE);
            circulo.setStroke(Color.DARKSLATEBLUE);
            circulo.setStrokeWidth(2);
            circulo.setCenterX(posicion[0]);
            circulo.setCenterY(posicion[1]);
            circulo.setUserData(nodo);
            circulos.put(nodo.getId(), circulo);

            Label etiqueta = new Label(nodo.getNombre());
            etiqueta.setFont(Font.font("System", 13));
            etiqueta.getStyleClass().add("node-label");
            etiqueta.setMouseTransparent(true);
            etiquetas.put(nodo.getId(), etiqueta);
            actualizarEtiqueta(etiqueta, posicion[0], posicion[1]);

            configurarArrastre(nodo, circulo, etiqueta);
            capaNodos.getChildren().addAll(circulo, etiqueta);
        }

        actualizarAristas();
    }

    private void configurarArrastre(Nodo nodo, Circle circulo, Label etiqueta) {
        final double[] desplazamiento = new double[2];
        circulo.setOnMousePressed(event -> {
            desplazamiento[0] = circulo.getCenterX() - event.getX();
            desplazamiento[1] = circulo.getCenterY() - event.getY();
            event.consume();
        });
        circulo.setOnMouseDragged(event -> {
            double x = Math.max(RADIO_NODO, Math.min(ANCHO_MAPA - RADIO_NODO, event.getX() + desplazamiento[0]));
            double y = Math.max(RADIO_NODO, Math.min(ALTO_MAPA - RADIO_NODO, event.getY() + desplazamiento[1]));
            circulo.setCenterX(x);
            circulo.setCenterY(y);
            posiciones.put(nodo.getId(), new double[]{x, y});
            actualizarEtiqueta(etiqueta, x, y);
            actualizarAristas();
            event.consume();
        });
    }

    private void actualizarEtiqueta(Label etiqueta, double x, double y) {
        etiqueta.setLayoutX(x - etiqueta.prefWidth(-1) / 2);
        etiqueta.setLayoutY(y + RADIO_NODO + 3);
    }

    private void actualizarAristas() {
        for (Map.Entry<String, Line> entry : lineas.entrySet()) {
            Arista arista = (Arista) entry.getValue().getUserData();
            Circle origen = circulos.get(arista.getOrigen().getId());
            Circle destino = circulos.get(arista.getDestino().getId());
            entry.getValue().setStartX(origen.getCenterX());
            entry.getValue().setStartY(origen.getCenterY());
            entry.getValue().setEndX(destino.getCenterX());
            entry.getValue().setEndY(destino.getCenterY());
        }
    }

    private Color colorArista(String clave) {
        Line linea = lineas.get(clave);
        if (linea != null && linea.getUserData() instanceof Arista arista) {
            return colorArista(arista);
        }
        return Color.DARKGRAY;
    }

    private Color colorArista(Arista arista) {
        if (arista.isBloqueada()) {
            return Color.GRAY;
        }
        return arista.isConEscaleras() ? Color.INDIANRED : Color.DARKGRAY;
    }

    private String claveArista(String origen, String destino) {
        return origen.compareTo(destino) <= 0 ? origen + "|" + destino : destino + "|" + origen;
    }

    private void configurarPosiciones() {
        posiciones.put("Porteria", new double[]{110, 300});
        posiciones.put("Pp", new double[]{110, 500});
        posiciones.put("A", new double[]{240, 110});
        posiciones.put("B", new double[]{390, 110});
        posiciones.put("C", new double[]{530, 110});
        posiciones.put("D", new double[]{390, 260});
        posiciones.put("E", new double[]{560, 360});
        posiciones.put("F", new double[]{730, 300});
        posiciones.put("G", new double[]{760, 150});
        posiciones.put("H", new double[]{850, 250});
        posiciones.put("I", new double[]{850, 430});
        posiciones.put("J", new double[]{560, 190});
        posiciones.put("K", new double[]{700, 470});
        posiciones.put("L", new double[]{430, 500});
    }
}
