package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutDosJugadores extends LayoutBase {

    @Override
    public int[] getPosicionRival(int i, int w, int h) {
        int cx = getCentroX(w);

        int x = cx - 160;

        int y = 20;

        if (i == 0) {
            return new int[]{x, y, 0};
        }
        return new int[]{0, 0, 0};
    }

    @Override
    public Point getPosicionMiZona(int w, int h) {
        int cx = getCentroX(w);
        int x = cx - 160;

        int y = h - 260;

        y = Math.max(y, (h / 2) + 140);

        return new Point(x, y);
    }
}