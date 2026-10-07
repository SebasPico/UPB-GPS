package upb.rutas.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Grafo {
    private final Map<String, Nodo> nodos;
    private final Map<String, List<Arista>> adyacencia;

    public Grafo() {
        this.nodos = new HashMap<>();
        this.adyacencia = new HashMap<>();
    }

    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
        adyacencia.putIfAbsent(nodo.getId(), new ArrayList<>());
    }

    public Nodo getNodo(String id) {
        return nodos.get(id);
    }

    public List<Nodo> getNodos() {
        return new ArrayList<>(nodos.values());
    }

    public void agregarArista(Nodo origen, Nodo destino, double peso, boolean conEscaleras) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Origen o destino no pueden ser nulos");
        }

        String idArista1 = origen.getId() + "-" + destino.getId();
        String idArista2 = destino.getId() + "-" + origen.getId();

        adyacencia.computeIfAbsent(origen.getId(), key -> new ArrayList<>())
                .add(new Arista(idArista1, origen, destino, peso, conEscaleras));

        adyacencia.computeIfAbsent(destino.getId(), key -> new ArrayList<>())
                .add(new Arista(idArista2, destino, origen, peso, conEscaleras));
    }

    public List<Arista> getAdyacencia(String idNodo) {
        return adyacencia.getOrDefault(idNodo, new ArrayList<>());
    }

    public Map<String, List<Arista>> getAdyacencia() {
        return adyacencia;
    }

    public void eliminarArista(Nodo origen, Nodo destino) {
        if (origen == null || destino == null) {
            return;
        }

        adyacencia.getOrDefault(origen.getId(), new ArrayList<>())
                .removeIf(arista -> arista.getDestino().equals(destino));

        adyacencia.getOrDefault(destino.getId(), new ArrayList<>())
                .removeIf(arista -> arista.getDestino().equals(origen));
    }

    public boolean contieneNodo(String id) {
        return nodos.containsKey(id);
    }

    /** Devuelve cada arista una sola vez (colapsando los dos sentidos del grafo no dirigido). */
    public List<Arista> getAristasUnicas() {
        Map<String, Arista> unicas = new LinkedHashMap<>();
        for (List<Arista> lista : adyacencia.values()) {
            for (Arista arista : lista) {
                String clave = ordenarClave(arista.getOrigen().getId(), arista.getDestino().getId());
                unicas.putIfAbsent(clave, arista);
            }
        }
        return new ArrayList<>(unicas.values());
    }

    private String ordenarClave(String id1, String id2) {
        return id1.compareTo(id2) <= 0 ? id1 + "|" + id2 : id2 + "|" + id1;
    }

    public void setArcoBloqueado(Nodo origen, Nodo destino, boolean bloqueada) {
        for (Arista arista : adyacencia.getOrDefault(origen.getId(), new ArrayList<>())) {
            if (arista.getDestino().equals(destino)) {
                arista.setBloqueada(bloqueada);
            }
        }
        for (Arista arista : adyacencia.getOrDefault(destino.getId(), new ArrayList<>())) {
            if (arista.getDestino().equals(origen)) {
                arista.setBloqueada(bloqueada);
            }
        }
    }
}
