package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutDosJugadores extends LayoutBase {

    private final int X = 340;

    private final int Y_ARR = 25;
    private final int Y_ABA = 520;

    @Override
    public int[] getPosicionRival(int i) {
        // Solo hay 1 Rival
        if (i == 0) {
            // Lo ponemos Arriba a la Izquierda para equilibrar la pantalla
            return new int[]{X, Y_ARR, 0};
        }
        return new int[]{0, 0, 0};
    }

    @Override
    public Point getPosicionMiZona() {
        return new Point(X, Y_ABA); // Tú: Abajo Derecha
    }
}
