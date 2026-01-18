package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public abstract class LayoutBase implements LayoutMesa {


    @Override
    public Point getPosicionTitulo() {
        return new Point(260, 270);
    }

    @Override
    public Point getPosicionMazos() {
        // Centrados verticalmente en la columna derecha (X=540 según tu código viejo)
        return new Point(540, 310);
    }

    @Override
    public Point getPosicionEtiquetas() {
        // 45px arriba de los mazos
        return new Point(540, 255);
    }


    @Override
    public int getGapMazo() {
        return 120;
    }

}