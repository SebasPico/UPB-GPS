package upb.rutas.modelo;

import java.util.Objects;

/** Lugar específico dentro de un edificio (RF-03/RF-04), resuelto a su nodo contenedor. */
public class Lugar {
    private final String nombre;
    private final String nodoId;

    public Lugar(String nombre, String nodoId) {
        this.nombre = nombre;
        this.nodoId = nodoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNodoId() {
        return nodoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lugar lugar)) return false;
        return Objects.equals(nombre.toLowerCase(), lugar.nombre.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }

    @Override
    public String toString() {
        return nombre;
    }
}
