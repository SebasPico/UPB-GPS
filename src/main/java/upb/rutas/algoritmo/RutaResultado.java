package upb.rutas.algoritmo;

import upb.rutas.modelo.Nodo;

import java.util.List;

public class RutaResultado {
    private final boolean encontrada;
    private final List<Nodo> ruta;
    private final double distancia;

    public RutaResultado(boolean encontrada, List<Nodo> ruta, double distancia) {
        this.encontrada = encontrada;
        this.ruta = ruta;
        this.distancia = distancia;
    }

    public boolean isEncontrada() {
        return encontrada;
    }

    public List<Nodo> getRuta() {
        return ruta;
    }

    public double getDistancia() {
        return distancia;
    }
}
