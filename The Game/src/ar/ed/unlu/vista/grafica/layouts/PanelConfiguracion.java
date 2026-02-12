package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.vista.grafica.ConfiguracionJuego;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class PanelConfiguracion extends JPanel {

    private BufferedImage fondo;
    private Runnable accionVolver;
    private Font fuenteTitulo, fuenteBotones;

    public PanelConfiguracion(Runnable accionVolver) {
        this.accionVolver = accionVolver;
        setLayout(new BorderLayout());
        cargarRecursos();

        // 1. PANEL CENTRAL (Flotante)
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);

        Box boxContenido = Box.createVerticalBox();
        boxContenido.setOpaque(false);

        // --- A. TÍTULO ---
        JLabel lblTitulo = new JLabel("CONFIGURACIÓN", SwingConstants.CENTER);
        lblTitulo.setFont(fuenteTitulo.deriveFont(50f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(lblTitulo);

        boxContenido.add(Box.createVerticalStrut(40)); // Más aire

        // --- B. SUBTÍTULO ---
        boxContenido.add(crearSubtitulo("ELEGIR TEMA VISUAL"));
        boxContenido.add(Box.createVerticalStrut(20));

        // --- C. BOTONES DE TEMAS (UNA SOLA LÍNEA) ---
        // Volvemos al FlowLayout para que queden uno al lado del otro
        JPanel panelTemas = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelTemas.setOpaque(false);
        // AUMENTAMOS EL ANCHO MÁXIMO: Antes 500, Ahora 900 (para que entren los 5)
        panelTemas.setMaximumSize(new Dimension(900, 60));

        panelTemas.add(crearBotonTema("HUMO", 1, "BOTON_GRIS.png"));
        panelTemas.add(crearBotonTema("VIDRIO", 2, "BOTON_AZUL.png"));
        panelTemas.add(crearBotonTema("ARBOLES", 3, "BOTON_VERDE.png"));
        panelTemas.add(crearBotonTema("ARAÑA", 4, "BOTON_VIOLETA.png"));
        panelTemas.add(crearBotonTema("RAYOS", 5, "BOTON_AMARILLO.png"));

        panelTemas.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(panelTemas);

        boxContenido.add(Box.createVerticalStrut(60)); // Espacio grande antes de volver

        // --- D. BOTÓN VOLVER ---
        JPanel pVolver = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pVolver.setOpaque(false);
        try {
            BotonCristal btnVolver = new BotonCristal("VOLVER", "BOTON_TURQUESA.png");
            btnVolver.setPreferredSize(new Dimension(220, 55));
            btnVolver.setFont(fuenteTitulo.deriveFont(24f));
            btnVolver.addActionListener(e -> accionVolver.run());
            pVolver.add(btnVolver);
        } catch (Exception ex) { }

        pVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(pVolver);

        panelCentral.add(boxContenido);
        add(panelCentral, BorderLayout.CENTER);

        // 2. PANEL SUR (Audio)
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);
        panelSur.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20));
        panelSur.add(new PanelAudio(), BorderLayout.EAST);

        add(panelSur, BorderLayout.SOUTH);
    }

    private JButton crearBotonTema(String nombre, int idTema, String imagenBoton) {
        JButton btn;
        try {
            btn = new BotonCristal(nombre, imagenBoton);
        } catch (Exception e) {
            btn = new JButton(nombre);
        }
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setFont(fuenteBotones);
        btn.addActionListener(e -> {
            ConfiguracionJuego.setTema(idTema);
            cargarRecursos();
            this.repaint();
            SwingUtilities.getWindowAncestor(this).repaint();
        });
        return btn;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuenteTitulo.deriveFont(28f));
        lbl.setForeground(new Color(200, 200, 255));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
    }

    private void cargarRecursos() {
        try {
            String pathFondo = "/resources/tablero/" + ConfiguracionJuego.getPathFondoMenu();
            InputStream is = getClass().getResourceAsStream(pathFondo);
            if (is != null) fondo = ImageIO.read(is);

            InputStream isFont = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (isFont != null) {
                Font fontBase = Font.createFont(Font.TRUETYPE_FONT, isFont);
                fuenteTitulo = fontBase;
                fuenteBotones = fontBase.deriveFont(14f);
            } else {
                fuenteTitulo = new Font("Arial", Font.BOLD, 30);
                fuenteBotones = new Font("Arial", Font.BOLD, 12);
            }
        } catch (Exception ex) {}
    }
}