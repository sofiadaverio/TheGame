package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutCincoJugadores extends LayoutBase {

    @Override
    public Point getPosicionMazos(int w, int h) {
        int cx = getCentroX(w);
        int cy = getCentroY(h);
        return new Point(cx + 100, cy - 60);
    }

    @Override
    public int[] getPosicionRival(int i, int w, int h) {
        int cy = getCentroY(h);

        int anchoUtil = w - ANCHO_CHAT;


        int xIzq = (int) (anchoUtil * 0.12);
        int xDer = (int) (anchoUtil * 0.88) - 320;

        xIzq = Math.max(xIzq, 240);

        xDer = Math.max(xDer, anchoUtil / 2 + 150);

        int yAbajo = h - 260; // Fila de abajo
        yAbajo = Math.max(yAbajo, cy + 140);

        int yArriba = 20; // Fila de arriba

        switch (i) {
            case 0: // J2: Abajo Izquierda
                return new int[]{xIzq, yAbajo, 0};

            case 1: // J3: Centro Izquierda (VERTICAL)
                // Fijo al borde izquierdo
                return new int[]{20, cy - 160, 1};

            case 2: // J4: Arriba Izquierda
                return new int[]{xIzq, yArriba, 0};

            case 3: // J5: Arriba Derecha
                return new int[]{xDer, yArriba, 0};

            default: return new int[]{0, 0, 0};
        }
    }

    @Override
    public Point getPosicionMiZona(int w, int h) {
        // Usamos la misma lógica de "xDer" para que tú (J1) estés alineado con J5
        int anchoUtil = w - ANCHO_CHAT;
        int xDer = (int) (anchoUtil * 0.88) - 320;

        // Tope
        xDer = Math.max(xDer, anchoUtil / 2 + 150);

        int y = h - 260;
        y = Math.max(y, (h / 2) + 140);

        return new Point(xDer, y);
    }
}