package upb.rutas.algoritmo;

import upb.rutas.modelo.Arista;
import upb.rutas.modelo.Grafo;
import upb.rutas.modelo.Nodo;

import java.util.*;

public class Dijkstra {
    private final Grafo grafo;

    public Dijkstra(Grafo grafo) {
        this.grafo = grafo;
    }

    public RutaResultado calcularRuta(Nodo origen, Nodo destino, boolean accesible) {
        if (origen == null || destino == null) {
            return new RutaResultado(false, new ArrayList<>(), 0);
        }

        Map<Nodo, Double> distancias = new HashMap<>();
        Map<Nodo, Nodo> predecesores = new HashMap<>();
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distancia));

        for (Nodo nodo : grafo.getNodos()) {
            distancias.put(nodo, Double.POSITIVE_INFINITY);
        }

        distancias.put(origen, 0.0);
        cola.add(new NodoDistancia(origen, 0.0));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            Nodo nodoActual = actual.nodo;

            if (actual.distancia > distancias.getOrDefault(nodoActual, Double.POSITIVE_INFINITY)) {
                continue;
            }

            if (nodoActual.equals(destino)) {
                break;
            }

            for (Arista arista : grafo.getAdyacencia(nodoActual.getId())) {
                if (arista.isBloqueada()) {
                    continue;
                }

                if (accesible && arista.isConEscaleras()) {
                    continue;
                }

                Nodo vecino = arista.getVecino(nodoActual);
                double nuevaDistancia = distancias.getOrDefault(nodoActual, Double.POSITIVE_INFINITY) + arista.getPeso();

                if (nuevaDistancia < distancias.getOrDefault(vecino, Double.POSITIVE_INFINITY)) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, nodoActual);
                    cola.add(new NodoDistancia(vecino, nuevaDistancia));
                }
            }
        }

        if (distancias.getOrDefault(destino, Double.POSITIVE_INFINITY) == Double.POSITIVE_INFINITY) {
            return new RutaResultado(false, new ArrayList<>(), 0);
        }

        List<Nodo> ruta = reconstruirRuta(origen, destino, predecesores);
        return new RutaResultado(true, ruta, distancias.get(destino));
    }

    private List<Nodo> reconstruirRuta(Nodo origen, Nodo destino, Map<Nodo, Nodo> predecesores) {
        LinkedList<Nodo> ruta = new LinkedList<>();
        Nodo actual = destino;

        while (actual != null) {
            ruta.addFirst(actual);
            if (actual.equals(origen)) {
                break;
            }
            actual = predecesores.get(actual);
        }

        return ruta;
    }

    private static class NodoDistancia {
        private final Nodo nodo;
        private final double distancia;

        public NodoDistancia(Nodo nodo, double distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }
    }
}
