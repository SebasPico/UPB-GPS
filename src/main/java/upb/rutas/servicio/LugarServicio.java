package upb.rutas.servicio;

import upb.rutas.modelo.Grafo;
import upb.rutas.modelo.Lugar;
import upb.rutas.modelo.Nodo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Resuelve lugares específicos (ej. "Biblioteca") al nodo/edificio que los contiene (RF-03/RF-04). */
public class LugarServicio {
    private final Grafo grafo;
    private final Map<String, Lugar> lugaresPorNombre;

    public LugarServicio(Grafo grafo) {
        this.grafo = grafo;
        this.lugaresPorNombre = new LinkedHashMap<>();
    }

    public void cargarLugares(List<Lugar> lugares) {
        for (Lugar lugar : lugares) {
            lugaresPorNombre.put(lugar.getNombre().toLowerCase(), lugar);
        }
    }

    public void registrarLugar(String nombre, String nodoId) {
        if (!grafo.contieneNodo(nodoId)) {
            throw new IllegalArgumentException("El nodo '" + nodoId + "' no existe en el grafo");
        }
        lugaresPorNombre.put(nombre.toLowerCase(), new Lugar(nombre, nodoId));
    }

    public Optional<Nodo> buscarNodoPorLugar(String nombreLugar) {
        if (nombreLugar == null || nombreLugar.isBlank()) {
            return Optional.empty();
        }
        Lugar lugar = lugaresPorNombre.get(nombreLugar.trim().toLowerCase());
        if (lugar == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(grafo.getNodo(lugar.getNodoId()));
    }

    public List<Lugar> getLugares() {
        return new ArrayList<>(lugaresPorNombre.values());
    }
}
