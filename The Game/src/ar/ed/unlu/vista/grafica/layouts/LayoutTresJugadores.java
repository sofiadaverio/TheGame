package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutTresJugadores extends LayoutBase {

    @Override
    public int[] getPosicionRival(int i, int w, int h) {
        int cx = getCentroX(w);

        int separacion = Math.max((w - ANCHO_CHAT) / 4, 200);

        int yArriba = 20; // Margen superior

        switch (i) {
            case 0:
                return new int[]{cx - separacion - 160, yArriba, 0};

            case 1:
                return new int[]{cx + separacion - 160, yArriba, 0};

            default: return new int[]{0, 0, 0};
        }
    }

    @Override
    public Point getPosicionMiZona(int w, int h) {
        int cx = getCentroX(w);

        // Tú vas ABAJO y CENTRADO
        int x = cx - 160;

        int y = h - 260;
        y = Math.max(y, (h / 2) + 140); // Tope mínimo

        return new Point(x, y);
    }

}