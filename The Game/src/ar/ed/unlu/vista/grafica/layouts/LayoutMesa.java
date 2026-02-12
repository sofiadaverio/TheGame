package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public interface LayoutMesa {
    // Ahora todos reciben ancho (w) y alto (h) actuales de la ventana
    int[] getPosicionRival(int indiceRival, int w, int h);

    Point getPosicionTitulo(int w, int h);
    Point getPosicionMazos(int w, int h);
    Point getPosicionEtiquetas(int w, int h);
    Point getPosicionMiZona(int w, int h);
    int getGapMazo();
}