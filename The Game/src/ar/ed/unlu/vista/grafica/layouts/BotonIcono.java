package ar.ed.unlu.vista.grafica.layouts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class BotonIcono extends JButton {
    private BufferedImage imgOn, imgOff;
    private boolean estado; // true = ON, false = OFF

    public BotonIcono(String nombreOn, String nombreOff, boolean estadoInicial, ActionListener accion) {
        this.estado = estadoInicial;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        cargarImagenes(nombreOn, nombreOff);

        addActionListener(e -> {
            this.estado = !this.estado; // Cambiar estado interno
            repaint();
            accion.actionPerformed(e); // Ejecutar lógica
        });
    }

    public void setEstado(boolean nuevoEstado) {
        this.estado = nuevoEstado;
        this.repaint();
    }

    private void cargarImagenes(String on, String off) {
        try {
            imgOn = ImageIO.read(getClass().getResourceAsStream("/resources/tablero/" + on + ".png"));
            imgOff = ImageIO.read(getClass().getResourceAsStream("/resources/tablero/" + off + ".png"));
        } catch (Exception e) { System.err.println("Faltan iconos de audio"); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage img = estado ? imgOn : imgOff;
        if (img != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        }
    }
}