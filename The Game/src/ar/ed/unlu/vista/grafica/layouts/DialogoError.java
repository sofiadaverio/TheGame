package ar.ed.unlu.vista.grafica.layouts;


import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class DialogoError extends JDialog {

    private BufferedImage fondo;
    private Font fuenteTexto;

    public DialogoError(Component parent, String mensaje) {
        super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        cargarRecursos();

        setSize(420, 180);

        // Centrado inicial
        setLocationRelativeTo(parent);

        Point p = getLocation();
        setLocation(p.x - 140, p.y);

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        panelFondo.setLayout(null);
        panelFondo.setOpaque(false);

        JLabel lblTexto = new JLabel("<html><center>" + mensaje + "</center></html>", SwingConstants.CENTER);
        lblTexto.setForeground(Color.WHITE);


        if (fuenteTexto != null) {
            // Antes 18f -> AHORA 22f (Más grande y legible)
            lblTexto.setFont(fuenteTexto.deriveFont(22f));
        } else {
            lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }

        // Mantenemos la posición centrada
        lblTexto.setBounds(30, 65, 340, 80);
        panelFondo.add(lblTexto);

        try {
            BotonCristal btnCerrar = new BotonCristal("X", "BOTON_ROJO.png");
            btnCerrar.setBounds(370, 45, 35, 35);
            btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
            btnCerrar.setMargin(new Insets(0, 0, 0, 0));

            btnCerrar.addActionListener(e -> {
                GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
                dispose();
            });
            panelFondo.add(btnCerrar);
        } catch (Exception e) {
            JButton btn = new JButton("X");
            btn.setBounds(370, 45, 35, 35);
            btn.setMargin(new Insets(0,0,0,0));
            btn.addActionListener(ev -> dispose());
            panelFondo.add(btn);
        }

        add(panelFondo);
    }

    private void cargarRecursos() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/tablero/MENSAJE_OSCURO.png");
            if (is != null) fondo = ImageIO.read(is);

            InputStream isFont = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (isFont != null) {
                Font fontBase = Font.createFont(Font.TRUETYPE_FONT, isFont);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fontBase);
                this.fuenteTexto = fontBase;
            }
        } catch (Exception e) { }
    }
}