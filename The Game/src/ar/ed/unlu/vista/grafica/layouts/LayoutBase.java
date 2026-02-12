package ar.ed.unlu.vista.grafica.layouts;

import java.awt.Point;

public abstract class LayoutBase implements LayoutMesa {

    protected final int ANCHO_CHAT = 280;

    // --- CÁLCULO DEL CENTRO ---
    protected int getCentroX(int w) {
        return (w - ANCHO_CHAT) / 2;
    }

    protected int getCentroY(int h) {
        return h / 2;
    }

    @Override
    public Point getPosicionMazos(int w, int h) {
        int cx = getCentroX(w);
        int cy = getCentroY(h);
        int x = cx - 20;
        int y = cy - 60;

        return new Point(x, y);
    }

    @Override
    public Point getPosicionTitulo(int w, int h) {
        Point pMazos = getPosicionMazos(w, h);

        int x = pMazos.x - 280;

        int y = pMazos.y - 40;

        return new Point(x, y);
    }

    @Override
    public Point getPosicionEtiquetas(int w, int h) {
        Point p = getPosicionMazos(w, h);
        return new Point(p.x, p.y - 55);
    }

    @Override
    public int getGapMazo() {
        return 120;
    }
}