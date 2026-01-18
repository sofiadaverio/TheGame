package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutTresJugadores extends LayoutBase {

    // Usamos las mismas columnas extremas que en 4 jugadores
    private final int X_COL_IZQ = 120;
    private final int X_COL_DER = 580;
    private final int X_COL_CEN = 340;

    private final int Y_ARR = 25;
    private final int Y_ABA = 520;

    @Override
    public int[] getPosicionRival(int i) {
        // 0 -> Rival 1 (Arriba Izquierda)
        // 1 -> Rival 2 (Arriba Derecha)
        switch (i) {
            case 0: return new int[]{X_COL_IZQ, Y_ARR, 0};
            case 1: return new int[]{X_COL_DER, Y_ARR, 0};
            default: return new int[]{0, 0, 0};
        }
    }

    @Override
    public Point getPosicionMiZona() {
        return new Point(X_COL_CEN , Y_ABA); // Tú: Abajo Derecha
    }
}
