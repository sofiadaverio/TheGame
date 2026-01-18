package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.modelo.Mensajes; // Asegúrate de tener tu enum Mensajes

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class PanelSalaEspera extends JPanel {

    private BufferedImage fondo;
    private Font fuenteJuego;

    // Componentes Izquierda (Jugadores)
    private DefaultListModel<String> modeloListaJugadores;
    private JList<String> listaJugadores;

    // Componentes Derecha (Chat)
    private JTextArea areaChat;
    private JComboBox<Mensajes> comboMensajes;

    // Acciones
    private Consumer<Mensajes> accionEnviarMensaje;
    private Runnable accionIniciarPartida;

    public PanelSalaEspera(Runnable onIniciar, Consumer<Mensajes> onMensaje) {
        this.accionIniciarPartida = onIniciar;
        this.accionEnviarMensaje = onMensaje;

        setLayout(new GridLayout(1, 2, 20, 20)); // Dividir pantalla en 2 columnas
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margen externo

        cargarRecursos();
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        // --- COLUMNA IZQUIERDA: JUGADORES Y BOTONES ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setOpaque(false);

        // Título Sala
        JLabel lblTitulo = new JLabel("SALA DE ESPERA");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(deriveFont(30f));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);

        // Lista de Jugadores
        modeloListaJugadores = new DefaultListModel<>();
        modeloListaJugadores.addElement("Esperando jugadores..."); // Texto default

        listaJugadores = new JList<>(modeloListaJugadores);
        listaJugadores.setFont(deriveFont(18f));
        listaJugadores.setForeground(Color.GREEN); // Estilo "Matrix" o retro
        listaJugadores.setBackground(new Color(0, 0, 0, 180));
        listaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollJugadores = new JScrollPane(listaJugadores);
        scrollJugadores.setOpaque(false);
        scrollJugadores.getViewport().setOpaque(false);
        scrollJugadores.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Título de la lista
        scrollJugadores.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "JUGADORES CONECTADOS",
                TitledBorder.CENTER, TitledBorder.TOP, deriveFont(14f), Color.WHITE));

        panelIzquierdo.add(scrollJugadores, BorderLayout.CENTER);

        // Botones de Acción (Abajo a la izquierda)
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 10, 10));
        panelBotones.setOpaque(false);

        BotonCristal btnIniciar = new BotonCristal("INICIAR PARTIDA", "BOTON_VERDE.png");
        btnIniciar.setPreferredSize(new Dimension(0, 50));
        btnIniciar.addActionListener(e -> {
            if (accionIniciarPartida != null) accionIniciarPartida.run();
        });

        BotonCristal btnReglas = new BotonCristal("VER REGLAS", "BOTON_TURQUESA.png");
        btnReglas.setPreferredSize(new Dimension(0, 50));
        btnReglas.addActionListener(e -> mostrarReglas());

        panelBotones.add(btnIniciar);
        panelBotones.add(btnReglas);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        add(panelIzquierdo);

        // --- COLUMNA DERECHA: CHAT ---
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 10));
        panelDerecho.setOpaque(false);

        // Área de Chat (Historial)
        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setFont(new Font("Consolas", Font.BOLD, 14));
        areaChat.setForeground(Color.LIGHT_GRAY);
        areaChat.setBackground(new Color(20, 20, 20, 200));
        areaChat.setLineWrap(true);

        JScrollPane scrollChat = new JScrollPane(areaChat);
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false);
        scrollChat.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "CHAT DE SALA",
                TitledBorder.CENTER, TitledBorder.TOP, deriveFont(14f), Color.WHITE));

        panelDerecho.add(scrollChat, BorderLayout.CENTER);

        // Input Chat (Combo + Botón)
        JPanel panelInputChat = new JPanel(new BorderLayout(5, 0));
        panelInputChat.setOpaque(false);

        comboMensajes = new JComboBox<>(Mensajes.values());
        comboMensajes.setFont(new Font("Arial", Font.BOLD, 14));

        BotonCristal btnEnviar = new BotonCristal("ENVIAR", "BOTON_AZUL.png");
        btnEnviar.setPreferredSize(new Dimension(100, 40));
        btnEnviar.addActionListener(e -> enviarMensaje());

        panelInputChat.add(comboMensajes, BorderLayout.CENTER);
        panelInputChat.add(btnEnviar, BorderLayout.EAST);

        panelDerecho.add(panelInputChat, BorderLayout.SOUTH);

        add(panelDerecho);
    }

    // --- MÉTODOS PÚBLICOS PARA ACTUALIZAR LA VISTA ---

    public void actualizarListaJugadores(List<String> nombres) {
        modeloListaJugadores.clear();
        for (String nombre : nombres) {
            modeloListaJugadores.addElement(" > " + nombre);
        }
        // Actualizar título con contador
        // (Requeriría guardar referencia al border, pero visualmente así está bien por ahora)
    }

    public void agregarMensajeChat(String mensaje) {
        areaChat.append(mensaje + "\n");
        areaChat.setCaretPosition(areaChat.getDocument().getLength()); // Auto-scroll
    }

    private void enviarMensaje() {
        if (accionEnviarMensaje != null) {
            Mensajes m = (Mensajes) comboMensajes.getSelectedItem();
            accionEnviarMensaje.accept(m);
        }
    }

    private void mostrarReglas() {
        JOptionPane.showMessageDialog(this, "Reglas: Jueguen cartas ascendentes o descendentes.\nNo digan números exactos.");
    }

    // --- UTILIDADES GRÁFICAS ---

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        else { g.setColor(new Color(30, 0, 30)); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    private void cargarRecursos() {
        try {
            // Usamos el mismo fondo violeta
            InputStream is = getClass().getResourceAsStream("/resources/tablero/FONDO_VIOLETA.png");
            if (is == null) is = getClass().getResourceAsStream("/resources/tablero/FONDO_VIOLETA.PNG");
            if (is != null) fondo = ImageIO.read(is);

            is = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (is != null) {
                fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Font deriveFont(float size) {
        if (fuenteJuego != null) return fuenteJuego.deriveFont(Font.BOLD, size);
        return new Font("Arial", Font.BOLD, (int)size);
    }
}
