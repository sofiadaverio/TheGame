package ar.ed.unlu.vista.grafica;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class BotonCristal extends JButton {

    private BufferedImage imagenFondo;

    public BotonCristal(String texto, String nombreArchivoImagen) {
        super(texto);

        // 1. Configuración visual básica para quitar el estilo "Windows"
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setOpaque(false);

        // 2. Configuración de fuente y color texto
        this.setForeground(Color.WHITE);
        this.setFont(new Font("Arial", Font.BOLD, 14));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 3. Carga de Imagen Segura
        try {
            // Ajustamos la ruta a tu carpeta "tablero"
            String ruta = "/resources/tablero/" + nombreArchivoImagen;

            // Truco: Si el nombre no termina en .png, se lo agregamos
            if (!ruta.endsWith(".png")) {
                ruta += ".png";
            }

            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null) {
                this.imagenFondo = ImageIO.read(is);
            } else {
                System.err.println("❌ ERROR BOTON: No encuentro la imagen: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("❌ EXCEPCION BOTON: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Si tenemos imagen, la dibujamos estirada al tamaño del botón
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Si falló la imagen, pintamos un fondo semitransparente para que se vea el botón al menos
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Llamamos al padre para que dibuje el TEXTO encima de la imagen
        super.paintComponent(g);
    }
}