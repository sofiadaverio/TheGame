package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutCuatroJugadores extends LayoutBase {

    // --- GRILLA (RECTÁNGULO) ---
    private final int X_COL_IZQ = 120;  // Alineado con los mazos izquierdos
    private final int X_COL_DER = 580;  // Alineado con los mazos derechos

    private final int Y_ARR = 25;
    private final int Y_ABA = 520;

    @Override
    public int[] getPosicionRival(int i) {
        // Orden Reloj (Tú estás Abajo Der):
        // 0 -> J2 (Abajo Izq)
        // 1 -> J3 (Arriba Izq)
        // 2 -> J4 (Arriba Der)

        switch (i) {
            case 0: return new int[]{X_COL_IZQ, Y_ABA, 0}; // J2: Horizontal Abajo-Izq
            case 1: return new int[]{X_COL_IZQ, Y_ARR, 0}; // J3: Horizontal Arriba-Izq
            case 2: return new int[]{X_COL_DER, Y_ARR, 0}; // J4: Horizontal Arriba-Der
            default: return new int[]{0, 0, 0};
        }
    }


    @Override
    public Point getPosicionMiZona() { return new Point(X_COL_DER, Y_ABA); }

}