package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.modelo.ColorCarta;
import ar.ed.unlu.modelo.TipoMazo; // <--- IMPORTANTE: Usamos tu Enum

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

    // CAMBIO: Ahora usamos el Enum, no un String
    private TipoMazo tipoMazoEnum;

    private boolean esMazo;
    private boolean tapada = false;
    private boolean rotada = false;

    // --- CONSTRUCTORES ---

    // Constructor para Cartas de Jugador (Sigue igual)
    public CartaGrafica(int numero, ColorCarta color) {
        this.numero = numero;
        this.colorLogico = color;
        this.esMazo = false;
        this.rotada = false;
        configurar();
    }

    // CAMBIO: Constructor para Mazos Centrales usando Enum
    public CartaGrafica(TipoMazo tipo, int numero) {
        this.tipoMazoEnum = tipo;
        this.numero = numero;
        this.esMazo = true;
        this.rotada = false;
        configurar();
    }

    // Constructor para Carta Tapada (Robar)
    public CartaGrafica(boolean estaRotada) {
        this.tapada = true;
        this.rotada = estaRotada;
        configurar();
    }

    public CartaGrafica() { this(false); }

    private void configurar() {
        if (rotada) {
            this.setPreferredSize(new Dimension(155, 100));
        } else {
            this.setPreferredSize(new Dimension(100, 155));
        }
        this.setOpaque(false);
        cargarFuente();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        RoundRectangle2D forma = new RoundRectangle2D.Float(0, 0, w, h, 25, 25);
        g2.setClip(forma);

        if (tapada) {
            if (rotada) dibujarImagen(g2, "REVERSO_90_GRADOS.png", w, h);
            else dibujarImagen(g2, "REVERSO.png", w, h);
        } else {
            // Muestra el número real, sin parches
            int numVisual = numero;

            if (esMazo) {
                // Si es carta base, dibuja la flecha de fondo
                if (esCartaBase()) {
                    // Usamos el Enum para decidir la imagen
                    String nombre = (tipoMazoEnum == TipoMazo.ASCENDENTE) ? "ASCENDENTE.png" : "DESCENDENTE.png";
                    dibujarImagen(g2, nombre, w, h);
                    dibujarNumerosBaseMazo(g2, w, h);
                } else {
                    // Si ya jugaron una carta encima
                    dibujarImagen(g2, "GRIS.png", w, h);
                    dibujarNumeroGrande(g2, w, h, numVisual);
                }
            } else {
                // Carta de jugador normal
                dibujarImagen(g2, colorLogico.toString() + ".png", w, h);
                dibujarNumeroGrande(g2, w, h, numVisual);
                dibujarNumerosEsquinas(g2, w, h, numVisual);
            }
        }

        g2.setClip(null);
        g2.setColor(new Color(20, 20, 20));
        g2.setStroke(new BasicStroke(3));
        g2.draw(forma);
    }

    // --- NUEVA LÓGICA CON ENUM ---
    private boolean esCartaBase() {
        if (!esMazo) return false;

        // Si el controlador manda 0 indicando "vacío", es base.
        if (numero == 0) return true;

        // Si es Ascendente y el número es 1, es la base visual.
        if (tipoMazoEnum == TipoMazo.ASCENDENTE && numero == 1) return true;

        // Si es Descendente y el número es 10 (Quick & Easy) o 100 (Original), es base.
        if (tipoMazoEnum == TipoMazo.DESCENDENTE) {
            return numero == 10 || numero == 100;
        }

        return false;
    }

    private void dibujarNumerosBaseMazo(Graphics2D g2, int w, int h) {
        configurarFuente(g2, 20f);
        g2.setColor(Color.WHITE);

        float tamanoChico = 20f;
        float tamanoGrande = 50f;

        // Lógica limpia con Enum
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

    // ... (El resto de métodos dibujarNumeroGrande, dibujarImagen, etc. siguen igual)

    private void dibujarNumeroGrande(Graphics2D g2, int w, int h, int numVisual) {
        String texto = String.valueOf(numVisual);
        float tamano = 55f;
        configurarFuente(g2, tamano);
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(texto)) / 2;
        int y = (int) (h * 0.80);
        if (esMazo && !esCartaBase()) {
            y = (h - fm.getHeight()) / 2 + fm.getAscent();
        }
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
        g2.setColor(new Color(0,0,0, 200));
        g2.drawString(txt, x+2, yPos+2);
        g2.setColor(Color.WHITE);
        g2.drawString(txt, x, yPos);
    }

    private void configurarFuente(Graphics2D g2, float size) {
        if (fuenteJuego != null) {
            g2.setFont(fuenteJuego.deriveFont(Font.BOLD, size));
        } else {
            g2.setFont(new Font("Arial", Font.BOLD, (int)size));
        }
    }

    private void dibujarImagen(Graphics2D g2, String nombre, int w, int h) {
        BufferedImage img = obtenerImagen(nombre);
        if (img != null) g2.drawImage(img, 0, 0, w, h, null);
        else { g2.setColor(Color.DARK_GRAY); g2.fillRect(0, 0, w, h); }
    }

    private BufferedImage obtenerImagen(String nombreArchivo) {
        if (!cacheImagenes.containsKey(nombreArchivo)) {
            try {
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
                String path = "/resources/fuentes/dealerplate.otf";
                InputStream is = getClass().getResourceAsStream(path);
                if (is != null) {
                    fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, is);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego);
                }
            } catch (Exception e) { }
        }
    }
}