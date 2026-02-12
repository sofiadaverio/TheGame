package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class DialogoConfirmacion extends JDialog {

    private BufferedImage fondo;
    private Font fuenteTexto;
    private boolean respuesta = false;


    public DialogoConfirmacion(Window parent, String mensaje) {
        super(parent, ModalityType.APPLICATION_MODAL); // Bloquea la ventana de atrás
        setUndecorated(true); // Sin bordes de Windows
        setBackground(new Color(0, 0, 0, 0)); // Transparente

        cargarRecursos();
        setSize(450, 250);
        setLocationRelativeTo(parent);


        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(Color.BLACK); g.fillRect(0,0,getWidth(),getHeight());
                    g.setColor(Color.WHITE); g.drawRect(0,0,getWidth()-1,getHeight()-1);
                }
            }
        };
        panelFondo.setLayout(null);
        panelFondo.setOpaque(false);

        // --- MENSAJE ---
        JLabel lblMensaje = new JLabel("<html><center>" + mensaje + "</center></html>", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.WHITE);
        lblMensaje.setFont(fuenteTexto != null ? fuenteTexto.deriveFont(Font.BOLD, 22f) : new Font("Arial", Font.BOLD, 18));
        lblMensaje.setBounds(30, 40, 390, 80);
        panelFondo.add(lblMensaje);

        // --- BOTÓN SÍ (Verde) ---
        try {
            // Usamos BotonCristal o un JButton con ícono
            JButton btnSi = new BotonCristal("SÍ", "BOTON_VERDE.png");
            btnSi.setBounds(60, 150, 120, 50);
            btnSi.addActionListener(e -> {
                respuesta = true;
                GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
                dispose(); // Cerrar ventana
            });
            panelFondo.add(btnSi);
        } catch (Exception e) {}

        // --- BOTÓN NO (Rojo) ---
        try {
            JButton btnNo = new BotonCristal("NO", "BOTON_ROJO.png");
            btnNo.setBounds(270, 150, 120, 50);
            btnNo.addActionListener(e -> {
                respuesta = false;
                GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
                dispose(); // Cerrar ventana
            });
            panelFondo.add(btnNo);
        } catch (Exception e) {}

        add(panelFondo);
    }


    public static boolean mostrar(Window parent, String mensaje) {
        DialogoConfirmacion dialogo = new DialogoConfirmacion(parent, mensaje);
        dialogo.setVisible(true); // Se detiene aquí hasta que el usuario responda
        return dialogo.respuesta;
    }

    private void cargarRecursos() {
        try {
            // Usamos el fondo de mensaje oscuro
            InputStream is = getClass().getResourceAsStream("/resources/tablero/MENSAJE_OSCURO.png");
            if (is != null) fondo = ImageIO.read(is);

            InputStream isFont = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (isFont != null) {
                fuenteTexto = Font.createFont(Font.TRUETYPE_FONT, isFont);
            }
        } catch (Exception e) {}
    }
}