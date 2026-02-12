package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.modelo.ColorCarta;
import ar.ed.unlu.modelo.TipoMazo;
import ar.ed.unlu.vista.grafica.CartaGrafica;
import ar.ed.unlu.vista.grafica.ConfiguracionJuego;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class PanelReglas extends JPanel {

    private BufferedImage fondo;
    private Runnable accionVolver;
    private Font fuenteTitulo, fuenteSubtitulo, fuenteTexto;

    public PanelReglas(Runnable accionVolver) {
        this.accionVolver = accionVolver;
        setLayout(new BorderLayout());
        cargarRecursos();

        // --- CONTENIDO SCROLLABLE ---
        Box boxContenido = Box.createVerticalBox();
        boxContenido.setOpaque(false);
        boxContenido.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));

        // 1. TÍTULO
        JLabel lblTitulo = new JLabel("REGLAMENTO");
        lblTitulo.setFont(fuenteTitulo.deriveFont(55f));
        lblTitulo.setForeground(new Color(220, 220, 255));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(lblTitulo);

        boxContenido.add(Box.createVerticalStrut(20));

        // 2. OBJETIVO
        boxContenido.add(crearSubtitulo("OBJETIVO"));
        boxContenido.add(crearBloqueTexto(
                "Juegan como un equipo. Deben colocar las 50 cartas en los dos mazos.<br>" +
                        "Hay cartas del 1 al 10 en <b>5 colores diferentes</b>:"
        ));

        JPanel panelColores = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelColores.setOpaque(false);
        panelColores.add(crearCartaChica(new CartaGrafica(2, ColorCarta.AZUL), "Azul"));
        panelColores.add(crearCartaChica(new CartaGrafica(4, ColorCarta.ROJA), "Roja"));
        panelColores.add(crearCartaChica(new CartaGrafica(6, ColorCarta.VERDE), "Verde"));
        panelColores.add(crearCartaChica(new CartaGrafica(8, ColorCarta.AMARILLO), "Amarillo"));
        panelColores.add(crearCartaChica(new CartaGrafica(10, ColorCarta.GRIS), "Gris"));
        panelColores.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(panelColores);

        boxContenido.add(crearBloqueTexto(
                "Se deben colocar en los mazos centrales:<br>" +
                        "• <b>Mazo 1: ASCENDENTE (1 al 10).</b><br>" +
                        "• <b>Mazo 2: DESCENDENTE (10 al 1).</b>"
        ));

        JPanel panelMazos = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        panelMazos.setOpaque(false);
        panelMazos.add(crearCartaChica(new CartaGrafica(TipoMazo.ASCENDENTE, 1), "Ascendente"));
        panelMazos.add(crearCartaChica(new CartaGrafica(TipoMazo.DESCENDENTE, 10), "Descendente"));
        panelMazos.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(panelMazos);

        boxContenido.add(Box.createVerticalStrut(30));

        // 3. TURNO
        boxContenido.add(crearSubtitulo("TURNO DEL JUGADOR"));
        boxContenido.add(crearBloqueTexto(
                "1. Debes jugar <b>1 o 2 cartas</b> de tu mano.<br>" +
                        "2. Repones tu mano al final del turno (vuelves a tener 2 cartas)."
        ));
        boxContenido.add(Box.createVerticalStrut(30));

        // 4. TRUCO
        boxContenido.add(crearSubtitulo("EL TRUCO DE LA MARCHA ATRÁS"));
        boxContenido.add(crearBloqueTexto(
                "Si juegas una carta del <b>MISMO COLOR</b> exacto que la que está en la mesa, " +
                        "puedes ignorar el orden y 'retroceder'.<br>" +
                        "<i>(Ej: En el mazo Ascendente hay un 7 Rojo, puedes jugar un 2 Rojo encima).</i>"
        ));

        JPanel panelTruco = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelTruco.setOpaque(false);
        panelTruco.add(crearCartaChica(new CartaGrafica(7, ColorCarta.ROJA), "Mesa (Roja)"));
        panelTruco.add(crearFlechaDibujada());
        panelTruco.add(crearCartaChica(new CartaGrafica(2, ColorCarta.ROJA), "¡Juegas esta!"));
        panelTruco.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxContenido.add(panelTruco);

        boxContenido.add(Box.createVerticalStrut(30));

        // 5. COMUNICACIÓN
        boxContenido.add(crearSubtitulo("COMUNICACIÓN"));
        boxContenido.add(crearBloqueTexto(
                "❌ <b>PROHIBIDO</b> decir números exactos ('Tengo el 9 rojo').<br>" +
                        "✅ <b>PERMITIDO</b> dar pistas vagas ('Tengo una roja alta')."
        ));
        boxContenido.add(Box.createVerticalStrut(20));

        // 6. PRO
        boxContenido.add(crearSubtitulo("MODO PROFESIONAL"));
        boxContenido.add(crearBloqueTexto(
                "• Solo se juega <b>EXACTAMENTE 1 carta</b> por turno.<br>" +
                        "• Prohibido dar pistas sobre valores. <b>Solo colores.</b>"
        ));
        boxContenido.add(Box.createVerticalStrut(40));

        // SCROLL
        JPanel panelWrapper = new JPanel(new BorderLayout());
        panelWrapper.setOpaque(false);
        panelWrapper.add(boxContenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        add(scroll, BorderLayout.CENTER);

        // --- ZONA INFERIOR (BOTÓN CENTRADO + AUDIO) ---
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // 1. DERECHA: Audio
        PanelAudio pAudio = new PanelAudio();
        panelSur.add(pAudio, BorderLayout.EAST);

        // 2. IZQUIERDA: Panel Fantasma (Del mismo tamaño que el audio para equilibrar)
        JPanel pDummy = new JPanel();
        pDummy.setOpaque(false);
        pDummy.setPreferredSize(pAudio.getPreferredSize());
        panelSur.add(pDummy, BorderLayout.WEST);

        // 3. CENTRO: Botón Volver (Ahora sí queda en el medio absoluto)
        JPanel pCentro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pCentro.setOpaque(false);
        try {
            BotonCristal btnVolver = new BotonCristal("VOLVER", "BOTON_TURQUESA.png");
            btnVolver.setPreferredSize(new Dimension(250, 60));
            btnVolver.setFont(fuenteTitulo.deriveFont(24f));
            btnVolver.addActionListener(e -> accionVolver.run());
            pCentro.add(btnVolver);
        } catch (Exception ex) {
            JButton btn = new JButton("VOLVER");
            btn.addActionListener(e -> accionVolver.run());
            pCentro.add(btn);
        }
        panelSur.add(pCentro, BorderLayout.CENTER);

        add(panelSur, BorderLayout.SOUTH);
    }

    // --- MÉTODOS DE AYUDA (Igual que antes) ---
    private JPanel crearCartaChica(CartaGrafica cartaReal, String etiqueta) {
        cartaReal.setPreferredSize(new Dimension(70, 108));
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(cartaReal, BorderLayout.CENTER);
        JLabel lblEtiqueta = new JLabel(etiqueta, SwingConstants.CENTER);
        lblEtiqueta.setForeground(Color.LIGHT_GRAY);
        lblEtiqueta.setFont(new Font("Arial", Font.PLAIN, 12));
        contenedor.add(lblEtiqueta, BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearFlechaDibujada() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(); int h = getHeight();
                Path2D path = new Path2D.Float();
                path.moveTo(10, h/2); path.lineTo(w-10, h/2);
                path.moveTo(w-25, h/2-10); path.lineTo(w-10, h/2); path.lineTo(w-25, h/2+10);
                g2.draw(path);
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(60, 108));
        return p;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuenteSubtitulo);
        lbl.setForeground(new Color(100, 255, 218));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel crearBloqueTexto(String htmlBody) {
        String html = "<html><div style='width: 600px; text-align: left; font-family: sans-serif; font-size: 15px; color: white;'>" + htmlBody + "</div></html>";
        JLabel lbl = new JLabel(html);
        lbl.setFont(fuenteTexto);
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private void cargarRecursos() {
        try {
            String nombreFondo = ConfiguracionJuego.getPathFondoMenu();
            InputStream is = getClass().getResourceAsStream("/resources/tablero/" + nombreFondo);
            if (is != null) fondo = ImageIO.read(is);

            InputStream isFont = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (isFont != null) {
                Font fontBase = Font.createFont(Font.TRUETYPE_FONT, isFont);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fontBase);
                fuenteTitulo = fontBase;
                fuenteSubtitulo = fontBase.deriveFont(28f);
            } else {
                fuenteTitulo = new Font("Arial", Font.BOLD, 30);
                fuenteSubtitulo = new Font("Arial", Font.BOLD, 22);
            }
        } catch (Exception ex) {
            fuenteTitulo = new Font("Arial", Font.BOLD, 30);
            fuenteSubtitulo = new Font("Arial", Font.BOLD, 22);
        }
        fuenteTexto = new Font("Segoe UI", Font.PLAIN, 16);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        else { g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    public void actualizarFondo() {
        cargarRecursos();
        repaint();
    }
}