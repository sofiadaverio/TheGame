package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public interface LayoutMesa {
    // [X, Y, Orientación (0=Horiz, 1=Vert)]
    int[] getPosicionRival(int indiceRival);

    Point getPosicionTitulo();
    Point getPosicionMazos();
    Point getPosicionEtiquetas();
    Point getPosicionMiZona();
    int getGapMazo();
}
