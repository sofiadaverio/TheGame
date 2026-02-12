package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.modelo.ColorCarta;
import ar.ed.unlu.modelo.TipoMazo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CartaGrafica extends JPanel {

    private static Map<String, BufferedImage> cacheImagenes = new HashMap<>();
    private static Font fuenteJuego = null;

    private int numero;
    private ColorCarta colorLogico;
    private TipoMazo tipoMazoEnum;

    private boolean esMazo;
    private boolean tapada = false;

    // 0 = Normal, 1 = 90 grados, 2 = 180 grados
    private int tipoRotacion = 0;

    // --- CONSTRUCTORES ---

    public CartaGrafica(int numero, ColorCarta color) {
        this.numero = numero;
        this.colorLogico = color;
        this.esMazo = false;
        configurar();
    }

    public CartaGrafica(TipoMazo tipo, int numero) {
        this.tipoMazoEnum = tipo;
        this.numero = numero;
        this.esMazo = true;
        configurar();
    }

    // Constructor para Cartas Tapadas (Robar o Rivales)
    // rotacion: 0=Normal, 1=90°, 2=180°
    public CartaGrafica(int rotacion) {
        this.tapada = true;
        this.tipoRotacion = rotacion;
        configurar();
    }

    // Constructor legacy (para compatibilidad)
    public CartaGrafica(boolean rotada90) {
        this(rotada90 ? 1 : 0);
    }

    private void configurar() {
        // Si es rotación 90° (Lateral), invertimos dimensiones
        if (tipoRotacion == 1) {
            this.setPreferredSize(new Dimension(155, 100));
            this.setSize(155, 100);
        } else {
            // Normal o 180° (Vertical)
            this.setPreferredSize(new Dimension(100, 155));
            this.setSize(100, 155);
        }
        this.setOpaque(false);
        cargarFuente();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        // Recorte redondeado prolijo
        RoundRectangle2D forma = new RoundRectangle2D.Float(0, 0, w, h, 25, 25);
        g2.setClip(forma);

        if (tapada) {
            if (tipoRotacion == 1) dibujarImagen(g2, "REVERSO_90_GRADOS.png", w, h);
            else if (tipoRotacion == 2) dibujarImagen(g2, "REVERSO_180_GRADOS.png", w, h);
            else dibujarImagen(g2, "REVERSO.png", w, h);
        } else {
            // Lógica de cartas destapadas (Tu código original)
            int numVisual = numero;
            if (esMazo) {
                if (esCartaBase()) {
                    String nombre = (tipoMazoEnum == TipoMazo.ASCENDENTE) ? "ASCENDENTE.png" : "DESCENDENTE.png";
                    dibujarImagen(g2, nombre, w, h);
                    dibujarNumerosBaseMazo(g2, w, h);
                } else {
                    dibujarImagen(g2, "GRIS.png", w, h);
                    dibujarNumeroGrande(g2, w, h, numVisual);
                }
            } else {
                dibujarImagen(g2, colorLogico.toString() + ".png", w, h);
                dibujarNumeroGrande(g2, w, h, numVisual);
                dibujarNumerosEsquinas(g2, w, h, numVisual);
            }
        }

        // Borde negro fino
        g2.setClip(null);
        g2.setColor(new Color(20, 20, 20));
        g2.setStroke(new BasicStroke(3));
        g2.draw(forma);
    }

    private boolean esCartaBase() {
        if (!esMazo) return false;
        if (numero == 0) return true;
        if (tipoMazoEnum == TipoMazo.ASCENDENTE && numero == 1) return true;
        if (tipoMazoEnum == TipoMazo.DESCENDENTE && numero == 10) return true;
        return false;
    }

    private void dibujarNumerosBaseMazo(Graphics2D g2, int w, int h) {
        g2.setColor(Color.WHITE);
        float tamanoChico = 20f;
        float tamanoGrande = 50f;
        if (tipoMazoEnum == TipoMazo.ASCENDENTE) {
            configurarFuente(g2, tamanoChico);
            dibujarTextoCentrado(g2, "10", w, 30);
            configurarFuente(g2, tamanoGrande);
            dibujarTextoCentrado(g2, "1", w, h - 20);
        } else {
            configurarFuente(g2, tamanoGrande);
            dibujarTextoCentrado(g2, "10", w, 50);
            configurarFuente(g2, tamanoChico);
            dibujarTextoCentrado(g2, "1", w, h - 15);
        }
    }

    private void dibujarNumeroGrande(Graphics2D g2, int w, int h, int numVisual) {
        String texto = String.valueOf(numVisual);
        configurarFuente(g2, 55f);
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(texto)) / 2;
        int y = (int) (h * 0.80);
        if (esMazo && !esCartaBase()) y = (h - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(new Color(0, 0, 0, 220));
        g2.drawString(texto, x + 3, y + 3);
        g2.setColor(Color.WHITE);
        g2.drawString(texto, x, y);
    }

    private void dibujarNumerosEsquinas(Graphics2D g2, int w, int h, int numVisual) {
        configurarFuente(g2, 18f);
        String texto = String.valueOf(numVisual);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.drawString(texto, 12, 28);
        g2.drawString(texto, w - fm.stringWidth(texto) - 12, h - 12);
    }

    private void dibujarTextoCentrado(Graphics2D g2, String txt, int w, int yPos) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(txt)) / 2;
        g2.drawString(txt, x, yPos);
    }

    private void configurarFuente(Graphics2D g2, float size) {
        if (fuenteJuego != null) g2.setFont(fuenteJuego.deriveFont(Font.BOLD, size));
        else g2.setFont(new Font("Arial", Font.BOLD, (int)size));
    }

    private void dibujarImagen(Graphics2D g2, String nombre, int w, int h) {
        BufferedImage img = obtenerImagen(nombre);
        if (img != null) g2.drawImage(img, 0, 0, w, h, null);
        else { g2.setColor(Color.DARK_GRAY); g2.fillRect(0, 0, w, h); }
    }

    private BufferedImage obtenerImagen(String nombreArchivo) {
        if (!cacheImagenes.containsKey(nombreArchivo)) {
            try {
                // Buscamos en ambas carpetas por si acaso
                String path = "/resources/cartas/" + nombreArchivo;
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    path = "/resources/tablero/" + nombreArchivo;
                    is = getClass().getResourceAsStream(path);
                }
                if (is != null) cacheImagenes.put(nombreArchivo, ImageIO.read(is));
            } catch (Exception e) { return null; }
        }
        return cacheImagenes.get(nombreArchivo);
    }

    private void cargarFuente() {
        if (fuenteJuego == null) {
            try {
                InputStream is = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
                if (is != null) {
                    fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, is);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego);
                }
            } catch (Exception e) { }
        }
    }

    // Método auxiliar para el panel
    public boolean isRotada() { return tipoRotacion == 1; }
}