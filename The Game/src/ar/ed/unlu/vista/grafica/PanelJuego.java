package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.controlador.ControladorConsola;
import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.modelo.Mensajes;
import ar.ed.unlu.modelo.TipoMazo;
import ar.ed.unlu.vista.grafica.layouts.LayoutMesa;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PanelJuego extends JPanel {

    private ControladorConsola controlador;
    private LayoutMesa layoutActual;
    private List<String> todosLosJugadores = new ArrayList<>();
    private List<String> rivales = new ArrayList<>();
    private String miNombre;

    // Recursos
    private BufferedImage fondo, titulo, marcoH, marcoV, placaError;
    private Font fuenteJuego;

    // UI y Cartas
    private JPanel panelMano, panelChat;
    private JTextArea txtChat;
    private JComboBox<Mensajes> comboChat;
    private BotonCristal btnEnviar;
    private String mensajeError = null;
    private Timer timerError;
    private CartaGrafica cartaAsc, cartaDes, cartaRobar;

    // Dimensiones
    private final int W_FRAME_H = 320; private final int W_FRAME_V = 220;
    private final int H_FRAME_H = 220; private final int H_FRAME_V = 320;
    private final int W_CARTA = 100; private final int H_CARTA = 155;

    public PanelJuego(ControladorConsola ctrl, String nombre, LayoutMesa layoutInicial) {
        this.controlador = ctrl;
        this.miNombre = nombre;
        this.layoutActual = layoutInicial;

        setLayout(null);
        cargarFuente();
        cargarRecursos();

        iniciarComponentesUI();
        inicializarMazosCentrales();
        reacomodarComponentes();

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(mensajeError != null) { mensajeError = null; repaint(); }
            }
        });
    }

    private void inicializarMazosCentrales() {
        // Usamos el Enum directamente
        cartaAsc = new CartaGrafica(TipoMazo.ASCENDENTE, 1);
        cartaDes = new CartaGrafica(TipoMazo.DESCENDENTE, 10);
        cartaRobar = new CartaGrafica(false);

        add(cartaAsc); add(cartaDes); add(cartaRobar);
    }

    private void reacomodarComponentes() {
        if (layoutActual == null) return;
        Point pm = layoutActual.getPosicionMiZona();
        panelMano.setBounds(pm.x, pm.y, W_FRAME_H, H_FRAME_H);

        Point pMazos = layoutActual.getPosicionMazos();
        int gap = layoutActual.getGapMazo();
        if (cartaAsc != null) cartaAsc.setBounds(pMazos.x, pMazos.y, W_CARTA, H_CARTA);
        if (cartaDes != null) cartaDes.setBounds(pMazos.x + gap, pMazos.y, W_CARTA, H_CARTA);
        if (cartaRobar != null) cartaRobar.setBounds(pMazos.x + (gap * 2) + 20, pMazos.y, W_CARTA, H_CARTA);
    }

    public void setLayoutMesa(LayoutMesa nuevoLayout) {
        this.layoutActual = nuevoLayout;
        reacomodarComponentes();
        repaint();
    }

    // --- FILTRADO Y ORDENAMIENTO VISUAL ---
    public void setJugadores(List<String> j) {
        this.todosLosJugadores = j;
        this.rivales.clear();

        // 1. Encontrar mi posición en la lista
        int miIndice = -1;
        for (int i = 0; i < j.size(); i++) {
            if (j.get(i).equalsIgnoreCase(miNombre)) {
                miIndice = i;
                break;
            }
        }

        // Si no me encuentro (error raro), muestro todo normal filtrado
        if (miIndice == -1) {
            this.rivales = j.stream()
                    .filter(nombre -> !nombre.equals(miNombre))
                    .collect(Collectors.toList());
        } else {
            // 2. Sentido Horario

            int total = j.size();
            for (int k = 1; k < total; k++) {
                // Usamos módulo (%) para dar la vuelta a la lista si llegamos al final
                int indiceRival = (miIndice + k) % total;
                this.rivales.add(j.get(indiceRival));
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); int h = getHeight();
        if (fondo != null) g2.drawImage(fondo, 0, 0, w, h, null);
        else { g2.setColor(Color.DARK_GRAY); g2.fillRect(0,0,w,h); }

        Point pt = layoutActual.getPosicionTitulo();
        if (titulo != null) g2.drawImage(titulo, pt.x, pt.y, 205, 200, null);

        Point pe = layoutActual.getPosicionEtiquetas();
        int gap = layoutActual.getGapMazo();
        dibujarEtiqueta(g2, "ASCENDENTE", pe.x, pe.y);
        dibujarEtiqueta(g2, "DESCENDENTE", pe.x + gap, pe.y);
        dibujarEtiqueta(g2, "ROBAR", pe.x + (gap*2) + 20, pe.y);

        dibujarRivales(g2);
    }

    private void dibujarRivales(Graphics2D g2) {
        // MI MARCO
        Point pm = layoutActual.getPosicionMiZona();
        if (marcoH != null) g2.drawImage(marcoH, pm.x, pm.y, W_FRAME_H, H_FRAME_H, null);
        g2.setColor(Color.LIGHT_GRAY);
        configurarFuente(g2, 18f);
        drawCenteredString(g2, miNombre + " (Tú)", new Rectangle(pm.x, pm.y - 10, W_FRAME_H, 20));

        // RIVALES (Usamos la lista filtrada 'rivales')
        for (int i = 0; i < rivales.size(); i++) {
            int[] pos = layoutActual.getPosicionRival(i);
            int x = pos[0]; int y = pos[1];
            boolean vertical = pos[2] == 1; // 1=Vertical, 0=Horizontal

            if (vertical && marcoV != null) g2.drawImage(marcoV, x, y, W_FRAME_V, H_FRAME_V, null);
            else if (!vertical && marcoH != null) g2.drawImage(marcoH, x, y, W_FRAME_H, H_FRAME_H, null);

            g2.setColor(Color.LIGHT_GRAY);
            configurarFuente(g2, 16f);
            int ancho = vertical ? W_FRAME_V : W_FRAME_H;
            drawCenteredString(g2, rivales.get(i), new Rectangle(x, y - 10, ancho, 20));
        }
    }

    // ... (Resto de métodos de carga y utilidades se mantienen igual) ...
    public void actualizarMesa(int asc, int des) {
        if (cartaAsc != null) {
            remove(cartaAsc);
            // Si asc es 0 o 1, se verá la base. Si es 5, se verá gris con un 5.
            cartaAsc = new CartaGrafica(TipoMazo.ASCENDENTE, asc);
            add(cartaAsc);
        }
        if (cartaDes != null) {
            remove(cartaDes);
            // Si des es 0 o 10, se verá la base.
            cartaDes = new CartaGrafica(TipoMazo.DESCENDENTE, des);
            add(cartaDes);
        }
        reacomodarComponentes();
        revalidate(); repaint();
    }
    private void cargarFuente() {
        try {
            String path = "/resources/fuentes/dealerplate.otf";
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego);
            }
        } catch (Exception e) { }
    }

    private void cargarRecursos() {
        try {
            fondo = cargar("tablero/FONDO_VIOLETA.png");
            titulo = cargar("tablero/TITULO_THE_GAME.png");
            marcoH = cargar("tablero/ESPACIO_CARTA_HORIZONTAL.png");
            marcoV = cargar("tablero/ESPACIO_CARTA_VERTICAL.png");
            placaError = cargar("tablero/MENSAJE_OSCURO.png");
        } catch (Exception e) { e.printStackTrace(); }
    }
    private BufferedImage cargar(String path) {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/" + path);
            if (is == null) is = getClass().getResourceAsStream("/resources/" + path.replace(".png", ".PNG"));
            return ImageIO.read(is);
        } catch (Exception e) { return null; }
    }
    private void iniciarComponentesUI() {
        panelMano = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 30));
        panelMano.setOpaque(false);
        add(panelMano);
        panelChat = new JPanel(new BorderLayout());
        panelChat.setBounds(1000, 0, 280, 800);
        txtChat = new JTextArea(); txtChat.setEditable(false);
        txtChat.setBackground(new Color(30,30,30)); txtChat.setForeground(Color.LIGHT_GRAY);
        JScrollPane scroll = new JScrollPane(txtChat);
        scroll.setBorder(null);
        JPanel input = new JPanel(new BorderLayout());
        comboChat = new JComboBox<>(Mensajes.values());
        btnEnviar = new BotonCristal("ENVIAR", "BOTON_VERDE.png");
        btnEnviar.setPreferredSize(new Dimension(80, 30));
        btnEnviar.addActionListener(e -> {
            if(controlador!=null) controlador.enviarMensajeChat((Mensajes)comboChat.getSelectedItem(), miNombre);
        });
        input.add(comboChat, BorderLayout.CENTER); input.add(btnEnviar, BorderLayout.EAST);
        panelChat.add(scroll, BorderLayout.CENTER);
        panelChat.add(input, BorderLayout.SOUTH);
        add(panelChat);
    }
    public void actualizarMano(List<Carta> cartas) {
        panelMano.removeAll();
        for (Carta c : cartas) {
            CartaGrafica cg = new CartaGrafica(c.getNumero(), c.getColor());
            cg.setPreferredSize(new Dimension(W_CARTA, H_CARTA));
            cg.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    for(Component c : panelMano.getComponents()) ((JComponent)c).setBorder(null);
                    cg.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
                }
            });
            panelMano.add(cg);
        }
        panelMano.revalidate(); panelMano.repaint();
    }
    private void dibujarEtiqueta(Graphics2D g2, String txt, int x, int y) {
        if (placaError == null) return;

        // 1. ANCHO CORREGIDO: 100px (Igual que la carta) para que no se vea desfasado
        int ancho = 100;
        int alto = 40;

        g2.drawImage(placaError, x, y, ancho, alto, null);

        g2.setColor(Color.LIGHT_GRAY);
        configurarFuente(g2, 14f);

        drawCenteredString(g2, txt, new Rectangle(x, y + 10, ancho, alto));
    }

    private void configurarFuente(Graphics2D g2, float size) {
        if (fuenteJuego != null) {
            g2.setFont(fuenteJuego.deriveFont(Font.BOLD, size));
        } else {
            // Si entra acá, es porque no encontró el archivo .otf
            g2.setFont(new Font("Arial", Font.BOLD, (int)size));
        }
    }

    private void drawCenteredString(Graphics g, String text, Rectangle rect) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Fórmula estándar de centrado vertical
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, x, y);
    }


    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g); // Dibuja primero los componentes hijos (chat, botones)

        // Dibuja el mensaje de error por encima de todo si existe
        if (mensajeError != null && placaError != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pW = 380;
            int pH = 110;

            int pX = (getWidth() - pW) / 2;
            int pY = (getHeight() - pH) / 2;

            g2.drawImage(placaError, pX, pY, pW, pH, null);

            g2.setColor(Color.WHITE);
            configurarFuente(g2, 18f);

            drawCenteredString(g2, mensajeError, new Rectangle(pX, pY, pW, pH));
        }
    }

    public void mostrarError(String msg) {
        this.mensajeError = msg;
        if(timerError != null) timerError.stop();
        timerError = new Timer(3000, e -> { mensajeError = null; repaint(); });
        timerError.setRepeats(false);
        timerError.start();
        repaint();
    }


}