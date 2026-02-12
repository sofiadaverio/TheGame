package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.modelo.RegistroRanking;
import ar.ed.unlu.vista.grafica.ConfiguracionJuego;
import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public class PanelRanking extends JPanel {

    private BufferedImage fondo, marcoTabla, trofeo;
    private Runnable accionVolver;
    private List<RegistroRanking> listaRanking;
    private Font fuenteGotica, fuenteNormal;

    public PanelRanking(Runnable accionVolver) {
        this.accionVolver = accionVolver;
        setLayout(null); // Layout absoluto para control total
        cargarRecursos();

        // BOTÓN VOLVER
        try {
            JButton btnVolver = new BotonCristal("VOLVER", "BOTON_ROJO.png");
            btnVolver.setBounds(50, 650, 200, 50); // Abajo a la izquierda
            btnVolver.addActionListener(e -> {GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
                accionVolver.run();
            });
            add(btnVolver);
        } catch (Exception e) {}
    }

    public void setRanking(List<RegistroRanking> lista) {
        this.listaRanking = lista;
        repaint();
    }

    public void actualizarFondo() {
        try {
            String nombreFondo = ConfiguracionJuego.getPathFondoMenu();
            fondo = cargar("tablero/" + nombreFondo);
            repaint();
        } catch (Exception e) { }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. FONDO
        if (fondo != null) g2.drawImage(fondo, 0, 0, w, h, null);
        else { g2.setColor(Color.BLACK); g2.fillRect(0,0,w,h); }

        // 2. TÍTULO
        g2.setColor(Color.WHITE);
        g2.setFont(fuenteGotica.deriveFont(60f));
        drawCenteredString(g2, "HALL OF FAME", w/2, 80);

        // 3. TABLA (Fondo semi-transparente o marco)
        int tablaW = 700;
        int tablaH = 500;
        int tablaX = (w - tablaW) / 2;
        int tablaY = (h - tablaH) / 2 + 30;

        // Dibujamos un fondo oscuro translúcido para que se lea bien
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(tablaX, tablaY, tablaW, tablaH, 30, 30);
        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(tablaX, tablaY, tablaW, tablaH, 30, 30);

        // 4. DIBUJAR LISTA
        if (listaRanking == null || listaRanking.isEmpty()) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(fuenteNormal.deriveFont(30f));
            drawCenteredString(g2, "Aún no hay leyendas...", w/2, h/2);
        } else {
            int y = tablaY + 80;
            int puesto = 1;

            // Cabecera
            g2.setColor(Color.GRAY);
            g2.setFont(fuenteNormal.deriveFont(Font.BOLD, 20f));
            g2.drawString("PUESTO", tablaX + 50, tablaY + 40);
            g2.drawString("EQUIPO", tablaX + 200, tablaY + 40);
            g2.drawString("TIEMPO", tablaX + 550, tablaY + 40);
            g2.drawLine(tablaX+20, tablaY+50, tablaX+tablaW-20, tablaY+50);

            for (RegistroRanking r : listaRanking) {
                // Colores según puesto
                if (puesto == 1) g2.setColor(new Color(255, 215, 0)); // Dorado
                else if (puesto == 2) g2.setColor(new Color(192, 192, 192)); // Plata
                else if (puesto == 3) g2.setColor(new Color(205, 127, 50)); // Bronce
                else g2.setColor(Color.WHITE);

                g2.setFont(fuenteGotica.deriveFont(35f));

                // Puesto
                g2.drawString("#" + puesto, tablaX + 60, y);

                // Nombre (con truncado si es muy largo)
                g2.drawString(r.getNombreEquipo(), tablaX + 200, y);

                // Tiempo
                g2.setFont(fuenteNormal.deriveFont(Font.BOLD, 28f));
                g2.drawString(r.getTiempoFormateado(), tablaX + 550, y);

                y += 80; // Espacio entre filas
                puesto++;
            }
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int startX = x - (metrics.stringWidth(text) / 2);
        g.drawString(text, startX, y);
    }

    private void cargarRecursos() {
        try {
            String nombreFondo = ConfiguracionJuego.getPathFondoMenu();
            fondo = cargar("tablero/" + nombreFondo);

            // Fuentes
            InputStream isFont = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (isFont != null) fuenteGotica = Font.createFont(Font.TRUETYPE_FONT, isFont);
            else fuenteGotica = new Font("Serif", Font.BOLD, 40);

            fuenteNormal = new Font("Segoe UI", Font.PLAIN, 20);

        } catch (Exception e) { e.printStackTrace(); }
    }

    private BufferedImage cargar(String path) {
        try { return ImageIO.read(getClass().getResourceAsStream("/resources/" + path)); }
        catch (Exception e) { return null; }
    }
}