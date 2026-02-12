package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class DialogoSalida extends JDialog {

    private BufferedImage fondo;
    private Font fuenteTexto;
    private int opcionElegida = 2; // 0=Guardar, 1=Salir sin guardar, 2=Cancelar (Default)

    public DialogoSalida(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        cargarRecursos();
        setSize(500, 300);
        setLocationRelativeTo(parent);

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
                else { g.setColor(Color.BLACK); g.fillRect(0,0,getWidth(),getHeight()); g.setColor(Color.WHITE); g.drawRect(0,0,getWidth()-1,getHeight()-1); }
            }
        };
        panelFondo.setLayout(null);
        panelFondo.setOpaque(false);

        // TEXTO
        JLabel lblTitulo = new JLabel("<html><center>¿DESEAS GUARDAR ANTES DE SALIR?</center></html>", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(fuenteTexto != null ? fuenteTexto.deriveFont(22f) : new Font("Arial", Font.BOLD, 25));
        lblTitulo.setBounds(50, 100, 400, 60);
        panelFondo.add(lblTitulo);

        // BOTÓN 1: GUARDAR Y SALIR (Verde)
        BotonCristal btnGuardar = new BotonCristal("GUARDAR Y SALIR", "BOTON_VERDE.png");
        btnGuardar.setBounds(50, 170, 180, 45);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 11));
        btnGuardar.addActionListener(e -> {
            opcionElegida = 0;
            GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
            dispose();
        });
        panelFondo.add(btnGuardar);

        // BOTÓN 2: SALIR SIN GUARDAR (Rojo)
        BotonCristal btnSalir = new BotonCristal("SALIR S/ GUARDAR", "BOTON_ROJO.png");
        btnSalir.setBounds(270, 170, 180, 45);
        btnSalir.setFont(new Font("Arial", Font.BOLD, 11));
        btnSalir.addActionListener(e -> {
            opcionElegida = 1;
            GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
            dispose();
        });
        panelFondo.add(btnSalir);

        // BOTÓN 3: CANCELAR (Azul/Gris - Abajo)
        BotonCristal btnCancelar = new BotonCristal("CANCELAR", "BOTON_AZUL.png");
        btnCancelar.setBounds(175, 220, 150, 45);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 11));
        btnCancelar.addActionListener(e -> {
            opcionElegida = 2;
            GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
            dispose();
        });
        panelFondo.add(btnCancelar);

        add(panelFondo);
    }

    public static int mostrar(Window parent) {
        DialogoSalida dialogo = new DialogoSalida(parent);
        dialogo.setVisible(true);
        return dialogo.opcionElegida;
    }

    private void cargarRecursos() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/tablero/MENSAJE_OSCURO.png");
            if (is != null) fondo = ImageIO.read(is);
            InputStream isFont = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (isFont != null) fuenteTexto = Font.createFont(Font.TRUETYPE_FONT, isFont);
        } catch (Exception e) {}
    }
}