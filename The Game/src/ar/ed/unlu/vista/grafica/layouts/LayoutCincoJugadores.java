package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public class LayoutCincoJugadores extends LayoutBase {
        // Columna para el jugador VERTICAL (pegado al borde izquierdo)
        private final int X_COL_VERTICAL = 20;

        // Columna para los jugadores HORIZONTALES de la IZQUIERDA (J2 y J4)
        private final int X_COL_IZQ = 140;

        // Columna para los jugadores HORIZONTALES de la DERECHA (J5 y Tú)
        private final int X_COL_DER = 530;

        // --- DEFINICIÓN DE FILAS ---
        private final int Y_ARR = 25;   // Fila Superior
        private final int Y_CEN = 225;  // Fila Central (Para el vertical)
        private final int Y_ABA = 520;  // Fila Inferior

        @Override
        public int[] getPosicionRival(int i) {
            // i = índice del rival (0 a 3)
            switch (i) {
                // J2: Abajo Izquierda (Horizontal)
                case 0: return new int[]{X_COL_IZQ, Y_ABA, 0};

                // J3: Centro Izquierda (Vertical) -> Usa la columna especial vertical
                case 1: return new int[]{X_COL_VERTICAL, Y_CEN, 1};

                // J4: Arriba Izquierda (Horizontal)
                case 2: return new int[]{X_COL_IZQ, Y_ARR, 0};

                // J5: Arriba Derecha (Horizontal)
                case 3: return new int[]{X_COL_DER, Y_ARR, 0};

                default: return new int[]{0,0,0};
            }
        }

        @Override
        public Point getPosicionMiZona() {
            // Tú: Abajo Derecha
            return new Point(X_COL_DER, Y_ABA);
        }
    }
