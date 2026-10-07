package upb.rutas.persistencia;

import upb.rutas.modelo.Arista;
import upb.rutas.modelo.Grafo;
import upb.rutas.modelo.Lugar;
import upb.rutas.modelo.Nodo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Carga y guarda el grafo (nodos, aristas, lugares) en un archivo JSON (RF-08). */
public class GrafoPersistencia {

    public Grafo cargarGrafo(Path archivo) throws IOException {
        String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
        Map<String, Object> raiz = (Map<String, Object>) JsonLite.parse(contenido);

        Grafo grafo = new Grafo();

        List<Object> nodosJson = (List<Object>) raiz.getOrDefault("nodos", List.of());
        for (Object o : nodosJson) {
            Map<String, Object> n = (Map<String, Object>) o;
            grafo.agregarNodo(new Nodo((String) n.get("id"), (String) n.get("nombre"), (String) n.get("tipo")));
        }

        List<Object> aristasJson = (List<Object>) raiz.getOrDefault("aristas", List.of());
        for (Object o : aristasJson) {
            Map<String, Object> a = (Map<String, Object>) o;
            Nodo origen = grafo.getNodo((String) a.get("origen"));
            Nodo destino = grafo.getNodo((String) a.get("destino"));
            double peso = ((Number) a.get("peso")).doubleValue();
            boolean escaleras = Boolean.TRUE.equals(a.get("escaleras"));
            boolean bloqueada = Boolean.TRUE.equals(a.get("bloqueada"));
            grafo.agregarArista(origen, destino, peso, escaleras);
            if (bloqueada) {
                grafo.setArcoBloqueado(origen, destino, true);
            }
        }

        return grafo;
    }

    public List<Lugar> cargarLugares(Path archivo) throws IOException {
        String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
        Map<String, Object> raiz = (Map<String, Object>) JsonLite.parse(contenido);

        List<Lugar> lugares = new ArrayList<>();
        List<Object> lugaresJson = (List<Object>) raiz.getOrDefault("lugares", List.of());
        for (Object o : lugaresJson) {
            Map<String, Object> l = (Map<String, Object>) o;
            lugares.add(new Lugar((String) l.get("nombre"), (String) l.get("nodoId")));
        }
        return lugares;
    }

    public void guardarGrafo(Grafo grafo, List<Lugar> lugares, Path archivo) throws IOException {
        Map<String, Object> raiz = new LinkedHashMap<>();

        List<Object> nodosJson = new ArrayList<>();
        for (Nodo nodo : grafo.getNodos()) {
            Map<String, Object> n = new LinkedHashMap<>();
            n.put("id", nodo.getId());
            n.put("nombre", nodo.getNombre());
            n.put("tipo", nodo.getTipo());
            nodosJson.add(n);
        }
        raiz.put("nodos", nodosJson);

        List<Object> aristasJson = new ArrayList<>();
        for (Arista arista : grafo.getAristasUnicas()) {
            Map<String, Object> a = new LinkedHashMap<>();
            a.put("origen", arista.getOrigen().getId());
            a.put("destino", arista.getDestino().getId());
            a.put("peso", arista.getPeso());
            a.put("escaleras", arista.isConEscaleras());
            a.put("bloqueada", arista.isBloqueada());
            aristasJson.add(a);
        }
        raiz.put("aristas", aristasJson);

        List<Object> lugaresJson = new ArrayList<>();
        for (Lugar lugar : lugares) {
            Map<String, Object> l = new LinkedHashMap<>();
            l.put("nombre", lugar.getNombre());
            l.put("nodoId", lugar.getNodoId());
            lugaresJson.add(l);
        }
        raiz.put("lugares", lugaresJson);

        Files.writeString(archivo, JsonLite.write(raiz), StandardCharsets.UTF_8);
    }
}
