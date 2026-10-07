package upb.rutas.modelo;

import java.util.Objects;

public class Arista {
    private final String id;
    private final Nodo origen;
    private final Nodo destino;
    private final double peso;
    private final boolean conEscaleras;
    private boolean bloqueada;

    public Arista(String id, Nodo origen, Nodo destino, double peso, boolean conEscaleras) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.conEscaleras = conEscaleras;
        this.bloqueada = false;
    }

    public String getId() {
        return id;
    }

    public Nodo getOrigen() {
        return origen;
    }

    public Nodo getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public boolean isConEscaleras() {
        return conEscaleras;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
    }

    public Nodo getVecino(Nodo nodo) {
        if (nodo.equals(origen)) {
            return destino;
        }
        if (nodo.equals(destino)) {
            return origen;
        }
        throw new IllegalArgumentException("El nodo no pertenece a esta arista");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arista arista)) return false;
        return Objects.equals(id, arista.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
