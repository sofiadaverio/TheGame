package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class BotonCristal extends JButton {

    private BufferedImage imagenFondo;

    public BotonCristal(String texto, String nombreArchivoImagen) {
        super(texto);


        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setOpaque(false);

        this.setForeground(Color.WHITE);
        this.setFont(new Font("Arial", Font.BOLD, 14));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.addActionListener(e -> {
            GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
        });

        try {

            String ruta = "/resources/tablero/" + nombreArchivoImagen;

            if (!ruta.endsWith(".png")) {
                ruta += ".png";
            }

            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null) {
                this.imagenFondo = ImageIO.read(is);
            } else {
                System.err.println("ERROR BOTON: No encuentro la imagen: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("EXCEPCION BOTON: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }
}