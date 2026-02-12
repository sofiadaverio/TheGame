package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutCuatroJugadores extends LayoutBase {

    // Ancho aproximado del marco del jugador (para calcular los huecos)
    private final int ANCHO_FRAME = 320;

    @Override
    public int[] getPosicionRival(int i, int w, int h) {
        int cy = getCentroY(h);
        int anchoUtil = w - ANCHO_CHAT;


        int espacioVacioTotal = anchoUtil - (ANCHO_FRAME * 2);


        int gap = Math.max(espacioVacioTotal / 3, 20);

        int xIzq = gap;

        int xDer = gap + ANCHO_FRAME + gap;

        int yAbajo = h - 260;
        yAbajo = Math.max(yAbajo, cy + 140);

        int yArriba = 20;

        switch (i) {
            case 0: // J2: Abajo Izquierda
                return new int[]{xIzq, yAbajo, 0};

            case 1: // J3: Arriba Izquierda
                return new int[]{xIzq, yArriba, 0};

            case 2: // J4: Arriba Derecha
                return new int[]{xDer, yArriba, 0};

            default: return new int[]{0, 0, 0};
        }
    }

    @Override
    public Point getPosicionMiZona(int w, int h) {
        int cy = getCentroY(h);
        int anchoUtil = w - ANCHO_CHAT;

        int espacioVacioTotal = anchoUtil - (ANCHO_FRAME * 2);
        int gap = Math.max(espacioVacioTotal / 3, 20);

        int xDer = gap + ANCHO_FRAME + gap;

        int y = h - 260;
        y = Math.max(y, cy + 140);

        return new Point(xDer, y);
    }
}