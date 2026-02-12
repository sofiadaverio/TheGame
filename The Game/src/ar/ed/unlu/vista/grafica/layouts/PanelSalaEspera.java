package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.modelo.MensajesSala;
import ar.ed.unlu.vista.grafica.ConfiguracionJuego;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class PanelSalaEspera extends JPanel {

    private BufferedImage fondo, tituloLogo;
    private PanelChat panelChat;
    private DefaultListModel<String> modeloLista;
    private Runnable accionConfig;
    private Runnable accionRanking;
    private JPanel panelCentralConFondo;

    public PanelSalaEspera(Consumer<Boolean> onIniciar,
                           Consumer<MensajesSala> onChat,
                           Runnable onReglas,
                           Runnable onConfig,
                           Runnable accionRanking) {
        this.accionConfig = onConfig;
        this.accionRanking = accionRanking;
        setLayout(new BorderLayout());
        cargarRecursos();
        iniciarComponentes(onIniciar, onChat, onReglas);
    }

    private void iniciarComponentes(Consumer<Boolean> onIniciar, Consumer<MensajesSala> onChat, Runnable onReglas) {

        panelCentralConFondo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
            }
        };
        panelCentralConFondo.setOpaque(false);
        panelCentralConFondo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // A. LOGO
        JPanel pNorte = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pNorte.setOpaque(false);
        if (tituloLogo != null) {
            JLabel lblLogo = new JLabel(new ImageIcon(tituloLogo.getScaledInstance(350, 150, Image.SCALE_SMOOTH)));
            pNorte.add(lblLogo);
        }
        panelCentralConFondo.add(pNorte, BorderLayout.NORTH);

        // B. LISTA JUGADORES (CORREGIDO: Panel contenedor para el borde)
        JPanel pIzq = new JPanel(new BorderLayout());
        pIzq.setOpaque(false);
        pIzq.setPreferredSize(new Dimension(280, 0));

        // Creamos un borde blanco bonito
        pIzq.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "JUGADORES EN SALA",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE));

        modeloLista = new DefaultListModel<>();
        JList<String> listaJugadores = new JList<>(modeloLista);
        listaJugadores.setFont(new Font("Arial", Font.BOLD, 14));
        listaJugadores.setBackground(new Color(0, 0, 0, 100)); // Semi-transparente negro
        listaJugadores.setForeground(Color.WHITE);

        JScrollPane scrollLista = new JScrollPane(listaJugadores);
        scrollLista.setOpaque(false);
        scrollLista.getViewport().setOpaque(false);
        scrollLista.setBorder(null); // Quitamos borde al scroll interno

        pIzq.add(scrollLista, BorderLayout.CENTER);
        panelCentralConFondo.add(pIzq, BorderLayout.WEST);

        // C. BOTONES
        JPanel pDer = new JPanel(new GridBagLayout());
        pDer.setOpaque(false);
        JPanel gridBotones = new JPanel(new GridLayout(6, 1, 0, 15));
        gridBotones.setOpaque(false);
        gridBotones.add(crearBoton("PARTIDA NORMAL", "BOTON_VERDE.png", e -> onIniciar.accept(false)));
        gridBotones.add(crearBoton("MODO PROFESIONAL", "BOTON_VIOLETA.png", e -> onIniciar.accept(true)));
        gridBotones.add(crearBoton("REGLAS", "BOTON_AZUL.png", e -> onReglas.run()));
        gridBotones.add(crearBoton("CONFIGURACION", "BOTON_AMARILLO.png", e -> accionConfig.run()));
        gridBotones.add(crearBoton("RANKING", "BOTON_GRIS.png", e -> {
            accionRanking.run();
        }));
        gridBotones.add(crearBoton("SALIR", "BOTON_ROJO.png", e -> System.exit(0)));
        pDer.add(gridBotones);
        panelCentralConFondo.add(pDer, BorderLayout.CENTER);

        // D. AUDIO
        JPanel pSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pSur.setOpaque(false);
        pSur.add(new PanelAudio());
        panelCentralConFondo.add(pSur, BorderLayout.SOUTH);

        add(panelCentralConFondo, BorderLayout.CENTER);

        // PANEL CHAT
        panelChat = new PanelChat(MensajesSala.values(), e -> {
            Object seleccionado = panelChat.getMensajeSeleccionado();
            if (seleccionado instanceof MensajesSala) onChat.accept((MensajesSala) seleccionado);
        });
        panelChat.setPreferredSize(new Dimension(300, 0));
        panelChat.setBackground(Color.BLACK);
        add(panelChat, BorderLayout.EAST);
    }

    private JButton crearBoton(String txt, String img, java.awt.event.ActionListener al) {
        JButton btn;
        try { btn = new BotonCristal(txt, img); } catch (Exception e) { btn = new JButton(txt); }
        Dimension dim = new Dimension(220, 45);
        btn.setPreferredSize(dim); btn.setMinimumSize(dim); btn.setMaximumSize(dim);
        btn.addActionListener(al);
        return btn;
    }

    public void actualizarFondo() {
        try {
            String nombreFondo = ConfiguracionJuego.getPathFondoMenu();
            fondo = cargar("tablero/" + nombreFondo);
            this.repaint();
        } catch (Exception e) { }
    }
    public void actualizarListaJugadores(List<String> jugadores) { modeloLista.clear(); for(String j : jugadores) modeloLista.addElement(j); }
    public void agregarMensajeChat(String msg) { panelChat.agregarMensaje(msg); }
    private void cargarRecursos() { try { String nombreFondo = ConfiguracionJuego.getPathFondoMenu(); fondo = cargar("tablero/" + nombreFondo); tituloLogo = cargar("tablero/TITULO_SALA_ESPERA.png"); } catch (Exception e) { e.printStackTrace(); } }
    private BufferedImage cargar(String path) { try { InputStream is = getClass().getResourceAsStream("/resources/" + path); return ImageIO.read(is); } catch (Exception e) { return null; } }
}