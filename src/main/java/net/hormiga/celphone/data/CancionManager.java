package net.hormiga.celphone.data;

import java.util.ArrayList;
import java.util.List;

public class CancionManager {

    private static final List<Cancion> canciones = new ArrayList<>();
    private static int indiceActual = -1;

    public static void setLista(List<Cancion> nuevaLista) {
        canciones.clear();
        canciones.addAll(nuevaLista);
        indiceActual = 0;
    }

    public static List<Cancion> getLista() {
        return canciones;
    }

    public static Cancion getActual() {
        if (indiceActual >= 0 && indiceActual < canciones.size()) {
            return canciones.get(indiceActual);
        }
        return null;
    }

    public static boolean siguiente() {
        if (indiceActual + 1 < canciones.size()) {
            indiceActual++;
            return true;
        }
        return false;
    }

    public static boolean anterior() {
        if (indiceActual - 1 >= 0) {
            indiceActual--;
            return true;
        }
        return false;
    }

    public static int getIndiceActual() {
        return indiceActual;
    }

    public static void setIndice(int nuevoIndice) {
        if (nuevoIndice >= 0 && nuevoIndice < canciones.size()) {
            indiceActual = nuevoIndice;
        }
    }
}
