package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.controlador.Controlador;
import ar.ed.unlu.modelo.*;
import ar.ed.unlu.vista.grafica.CartaGrafica;
import ar.ed.unlu.vista.grafica.ConfiguracionJuego;
import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelJuego extends JPanel {

    private Controlador controlador;
    private LayoutMesa layoutActual;
    private List<String> rivales = new ArrayList<>();
    private List<Integer> cantidadCartasRivales = new ArrayList<>();

    private String miNombre;
    private boolean esMiTurno = false;
    private boolean modoProfesional = false;
    private String mensajeEstado = ""; // Texto de estado (ej: "Tu Turno")

    // Lógica
    private Carta cartaSeleccionada = null;
    private CartaGrafica graficaSeleccionada = null;
    private Runnable accionVolver;
    private boolean juegoPausado = false;

    private String mensajeFinJuego = null;
    private boolean victoria = false;

    // Recursos
    private BufferedImage fondo, titulo, imgProfesional, marcoH, marcoV, placaError, btnSiImg, imgReverso180, imgReverso90;
    private Font fuenteJuego;

    // UI
    private JPanel panelMano;
    private PanelChat panelChat;
    private BotonCristal btnSalir;
    private CartaGrafica cartaAsc, cartaDes, cartaRobar;

    // Dimensiones
    private final int FIN_W = 550; private final int FIN_H = 300;
    private final int W_FRAME_H = 320; private final int W_FRAME_V = 220;
    private final int H_FRAME_H = 220; private final int H_FRAME_V = 320;
    private final int W_CARTA_STD = 100; private final int H_CARTA_STD = 155;
    private final int W_CARTA_ROT = 155; private final int H_CARTA_ROT = 100;
    private final int ANCHO_CHAT = 280;

    public PanelJuego(Controlador ctrl, String nombre) {
        this.controlador = ctrl;
        this.miNombre = nombre;
        this.layoutActual = new LayoutCuatroJugadores();

        setLayout(null);
        cargarFuente();
        cargarRecursos();

        iniciarComponentesUI();
        iniciarAudio();
        inicializarMazosCentrales();

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) { reacomodarComponentes(); repaint(); }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (mensajeFinJuego != null) { chequearClickFinJuego(e.getX(), e.getY()); return; }
                if (cartaSeleccionada != null) { deseleccionarCarta(); }
            }
        });
    }

    private void intentarPasarTurno() {
        if (mensajeFinJuego != null) return;

        if (esMiTurno) {
            try { GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON); } catch(Exception e){}

            if (controlador != null) {
                controlador.solicitarPasarTurno(miNombre);
            }
        } else {
            mostrarError("¡NO ES TU TURNO!");
        }
    }

    private void tryingToPlay(TipoMazo tipo) {
        if (mensajeFinJuego != null) return;
        if (!esMiTurno) { mostrarError("¡NO ES TU TURNO!"); return; }
        if (cartaSeleccionada == null) { mostrarError("¡PRIMERO SELECCIONÁ UNA CARTA!"); return; }

        if (controlador != null) {
            try { GestorAudio.reproducirEfecto(GestorAudio.SFX_CARTA); } catch(Exception e){}
            controlador.realizarJugada(cartaSeleccionada, new Mazo(tipo), miNombre);
        }
        deseleccionarCarta();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); int h = getHeight();
        int anchoJuego = w - ANCHO_CHAT;

        if (fondo != null) g2.drawImage(fondo, 0, 0, anchoJuego, h, null);
        else { g2.setColor(new Color(30, 30, 30)); g2.fillRect(0,0,anchoJuego,h); }

        g2.setColor(Color.BLACK); g2.fillRect(anchoJuego, 0, ANCHO_CHAT, h);
        g2.setColor(Color.WHITE); g2.drawLine(anchoJuego, 0, anchoJuego, h);

        if (layoutActual != null) {
            Point pt = layoutActual.getPosicionTitulo(w, h);
            if (titulo != null) g2.drawImage(titulo, pt.x, pt.y, 205, 200, null);
            if (modoProfesional && imgProfesional != null) g2.drawImage(imgProfesional, pt.x+20, pt.y + 180, 205, 50, null);

            Point pe = layoutActual.getPosicionEtiquetas(w, h);
            int gap = layoutActual.getGapMazo();
            dibujarEtiqueta(g2, "ASCENDENTE", pe.x, pe.y);
            dibujarEtiqueta(g2, "DESCENDENTE", pe.x + gap, pe.y);
            dibujarEtiqueta(g2, "ROBAR/PASAR", pe.x + (gap*2) + 20, pe.y);
        }
        dibujarRivalesManualmente(g2);
    }

    private void dibujarRivalesManualmente(Graphics2D g2) {
        if (layoutActual == null) return;
        int w = getWidth(); int h = getHeight();
        Point pm = layoutActual.getPosicionMiZona(w, h);
        if (marcoH != null) g2.drawImage(marcoH, pm.x, pm.y, W_FRAME_H, H_FRAME_H, null);

        if (esMiTurno) {
            g2.setColor(modoProfesional ? new Color(50, 255, 50) : new Color(160, 80, 255));
            configurarFuente(g2, 22f);

            // --- DIBUJAR MENSAJE DE ESTADO (MANUAL) ---
            String msg = (mensajeEstado != null && !mensajeEstado.isEmpty()) ? mensajeEstado : "¡TU TURNO!";
            drawCenteredString(g2, msg, new Rectangle(pm.x - 50, pm.y - 40, W_FRAME_H + 100, 30));

            g2.setStroke(new BasicStroke(3));
            g2.drawRect(pm.x, pm.y, W_FRAME_H, H_FRAME_H);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            configurarFuente(g2, 18f);
            drawCenteredString(g2, (miNombre + " (Tú)"), new Rectangle(pm.x, pm.y - 10, W_FRAME_H, 20));
        }

        for (int i = 0; i < rivales.size(); i++) {
            int[] pos = layoutActual.getPosicionRival(i, w, h);
            int x = pos[0]; int y = pos[1];
            if (x == 0 && y == 0) { x = 20; y = 20 + (i * 200); }
            boolean vertical = pos[2] == 1;
            int cantidad = (i < cantidadCartasRivales.size()) ? cantidadCartasRivales.get(i) : 0;
            if (vertical) {
                if (marcoV != null) g2.drawImage(marcoV, x, y, W_FRAME_V, H_FRAME_V, null);
                g2.setColor(Color.WHITE); configurarFuente(g2, 16f);
                drawCenteredString(g2, rivales.get(i), new Rectangle(x, y - 10, W_FRAME_V, 20));
            } else {
                if (marcoH != null) g2.drawImage(marcoH, x, y, W_FRAME_H, H_FRAME_H, null);
                g2.setColor(Color.WHITE); configurarFuente(g2, 16f);
                drawCenteredString(g2, rivales.get(i), new Rectangle(x, y - 10, W_FRAME_H, 20));
            }
            int overlap = 120;
            for (int c = 0; c < cantidad; c++) {
                if (vertical) { if (imgReverso90 != null) { int cardX = x + (W_FRAME_V - W_CARTA_ROT) / 2; int totalH = (cantidad - 1) * overlap + H_CARTA_ROT; int startY = y + (H_FRAME_V - totalH) / 2; int cardY = startY + (c * overlap); drawRoundedImage(g2, imgReverso90, cardX, cardY, W_CARTA_ROT, H_CARTA_ROT); } }
                else { if (imgReverso180 != null) { int totalW = (cantidad - 1) * overlap + W_CARTA_STD; int startX = x + (W_FRAME_H - totalW) / 2; int cardX = startX + (c * overlap); int cardY = y + (H_FRAME_H - H_CARTA_STD) / 2; drawRoundedImage(g2, imgReverso180, cardX, cardY, W_CARTA_STD, H_CARTA_STD); } }
            }
        }
    }

    public void setJuegoPausado(boolean pausado) {
        this.juegoPausado = pausado;
        repaint();
    }

    public void setMensajeEstado(String msg) { this.mensajeEstado = msg; SwingUtilities.invokeLater(() -> repaint()); }

    private void drawRoundedImage(Graphics2D g2, BufferedImage img, int x, int y, int w, int h) { RoundRectangle2D rounded = new RoundRectangle2D.Float(x, y, w, h, 25, 25); Shape oldClip = g2.getClip(); g2.setClip(rounded); g2.drawImage(img, x, y, w, h, null); g2.setClip(oldClip); g2.setColor(new Color(20, 20, 20)); g2.setStroke(new BasicStroke(3)); g2.draw(rounded); }
    public void mostrarError(String msg) { try { GestorAudio.reproducirEfecto(GestorAudio.SFX_ERROR); } catch(Exception e){} SwingUtilities.invokeLater(() -> { DialogoError dialogo = new DialogoError(this, msg); dialogo.setVisible(true); }); }
    public void mostrarFinJuego(boolean victoria, String mensaje) { try { if (victoria) GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON); else GestorAudio.reproducirEfecto(GestorAudio.SFX_ERROR); } catch(Exception e){} SwingUtilities.invokeLater(() -> { this.mensajeFinJuego = mensaje; this.victoria = victoria; repaint(); }); }
    @Override protected void paintChildren(Graphics g) {
        super.paintChildren(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cx = (getWidth() - ANCHO_CHAT) / 2; int cy = getHeight() / 2;
        if (mensajeFinJuego != null && placaError != null) {
            int pX = cx - FIN_W/2; int pY = cy - FIN_H/2;
            g2.setColor(new Color(0,0,0,150)); g2.fillRect(0,0,getWidth(), getHeight());
            g2.drawImage(placaError, pX, pY, FIN_W, FIN_H, null);
            g2.setColor(victoria ? Color.GREEN : Color.RED); configurarFuente(g2, 35f); drawCenteredString(g2, victoria ? "¡VICTORIA!" : "DERROTA", new Rectangle(pX, pY + 125, FIN_W, 40));
            g2.setColor(Color.WHITE); configurarFuente(g2, 22f); drawCenteredString(g2, mensajeFinJuego, new Rectangle(pX + 30, pY + 175, FIN_W - 60, 40));
            if (btnSiImg != null) { int btnW = 120; int btnH = 50; int btnX = pX + (FIN_W - btnW)/2; int btnY = pY + 230; g2.drawImage(btnSiImg, btnX, btnY, btnW, btnH, null); configurarFuente(g2, 18f); drawCenteredString(g2, "SALIR", new Rectangle(btnX, btnY, btnW, btnH)); }
        }
        if (juegoPausado && mensajeFinJuego == null) { // Solo si no terminó el juego
            int w = getWidth();
            int h = getHeight();

            // Fondo oscuro transparente
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, w, h);

            // Texto parpadeante o fijo
            g2.setColor(Color.ORANGE);
            configurarFuente(g2, 40f);
            String msg = "JUEGO PAUSADO";
            String subMsg = "Esperando que se reconecten todos los jugadores...";

            drawCenteredString(g2, msg, new Rectangle(0, h/2 - 50, w, 50));

            g2.setColor(Color.WHITE);
            configurarFuente(g2, 20f);
            drawCenteredString(g2, subMsg, new Rectangle(0, h/2 + 20, w, 30));
        }
    }
    private void chequearClickFinJuego(int x, int y) { int cx = (getWidth() - ANCHO_CHAT) / 2; int cy = getHeight() / 2; int pX = cx - FIN_W/2; int pY = cy - FIN_H/2; int btnW = 120; int btnH = 50; int btnX = pX + (FIN_W - btnW)/2; int btnY = pY + 230; if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) { if (accionVolver != null) accionVolver.run(); } }
    public void resetearInterfaz() { this.mensajeFinJuego = null; repaint(); }
    public void setModoJuego(boolean esPro) { this.modoProfesional = esPro; cargarRecursos(); cargarChat(esPro); this.repaint(); }
    public void actualizarFondo() { cargarRecursos(); repaint(); }
    public void actualizarMano(List<Carta> cartas) { SwingUtilities.invokeLater(() -> { resetearInterfaz(); panelMano.removeAll(); boolean seleccionSigueValida = false; if (cartas != null) { for (Carta c : cartas) { CartaGrafica cg = new CartaGrafica(c.getNumero(), c.getColor()); cg.setPreferredSize(new Dimension(W_CARTA_STD, H_CARTA_STD)); cg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); if (cartaSeleccionada != null && c.getNumero() == cartaSeleccionada.getNumero() && c.getColor() == cartaSeleccionada.getColor()) { seleccionSigueValida = true; cg.setBorder(BorderFactory.createLineBorder(Color.CYAN, 4)); this.graficaSeleccionada = cg; this.cartaSeleccionada = c; } cg.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { seleccionarCarta(c, cg); } }); panelMano.add(cg); } } if (!seleccionSigueValida) deseleccionarCarta(); panelMano.revalidate(); panelMano.repaint(); }); }
    public void actualizarRivales(List<String> todosLosJugadores, Map<String, Integer> cantidades) { SwingUtilities.invokeLater(() -> { if (todosLosJugadores == null) return; this.rivales.clear(); this.cantidadCartasRivales.clear(); int miIndice = -1; for (int i = 0; i < todosLosJugadores.size(); i++) { if (todosLosJugadores.get(i).trim().equalsIgnoreCase(miNombre.trim())) { miIndice = i; break; } } if (miIndice != -1) { int total = todosLosJugadores.size(); for (int k = 1; k < total; k++) { String nombreRival = todosLosJugadores.get((miIndice + k) % total); this.rivales.add(nombreRival); this.cantidadCartasRivales.add(cantidades.getOrDefault(nombreRival, 0)); } } int cant = rivales.size(); if (cant == 1) layoutActual = new LayoutDosJugadores(); else if (cant == 2) layoutActual = new LayoutTresJugadores(); else if (cant == 3) layoutActual = new LayoutCuatroJugadores(); else layoutActual = new LayoutCincoJugadores(); reacomodarComponentes(); repaint(); }); }
    public void actualizarMesa(List<Mazo> mazos) { SwingUtilities.invokeLater(() -> { if (mazos == null) return; if (cartaAsc != null) remove(cartaAsc); if (cartaDes != null) remove(cartaDes); for (Mazo m : mazos) { TipoMazo tipo = m.getTipoMazo(); Carta ultima = m.obtenerUltimaCarta(); if (tipo == TipoMazo.ASCENDENTE) { if (ultima == null) cartaAsc = new CartaGrafica(TipoMazo.ASCENDENTE, 1); else cartaAsc = new CartaGrafica(ultima.getNumero(), ultima.getColor()); for(MouseListener ml : cartaAsc.getMouseListeners()) cartaAsc.removeMouseListener(ml); cartaAsc.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { tryingToPlay(TipoMazo.ASCENDENTE); } }); add(cartaAsc); } else if (tipo == TipoMazo.DESCENDENTE) { if (ultima == null) cartaDes = new CartaGrafica(TipoMazo.DESCENDENTE, 10); else cartaDes = new CartaGrafica(ultima.getNumero(), ultima.getColor()); for(MouseListener ml : cartaDes.getMouseListeners()) cartaDes.removeMouseListener(ml); cartaDes.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { tryingToPlay(TipoMazo.DESCENDENTE); } }); add(cartaDes); } } revalidate(); reacomodarComponentes(); repaint(); }); }
    public void setTurnoActivo(boolean activo) { this.esMiTurno = activo; this.repaint(); }
    private void seleccionarCarta(Carta c, CartaGrafica cg) { if (mensajeFinJuego != null) return; if (graficaSeleccionada != null) graficaSeleccionada.setBorder(null); this.cartaSeleccionada = c; this.graficaSeleccionada = cg; cg.setBorder(BorderFactory.createLineBorder(Color.CYAN, 4)); try { GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON); } catch(Exception e){} }
    private void deseleccionarCarta() { if (graficaSeleccionada != null) graficaSeleccionada.setBorder(null); cartaSeleccionada = null; graficaSeleccionada = null; }
    public void reacomodarComponentes() {
        if (layoutActual == null) return; int w = getWidth(); int h = getHeight(); if (w == 0 || h == 0) return;
        Point pm = layoutActual.getPosicionMiZona(w, h); if(panelMano != null) panelMano.setBounds(pm.x, pm.y, W_FRAME_H, H_FRAME_H);
        Point pMazos = layoutActual.getPosicionMazos(w, h); int gap = layoutActual.getGapMazo();
        if (cartaAsc != null) cartaAsc.setBounds(pMazos.x, pMazos.y, W_CARTA_STD, H_CARTA_STD);
        if (cartaDes != null) cartaDes.setBounds(pMazos.x + gap, pMazos.y, W_CARTA_STD, H_CARTA_STD);
        if (cartaRobar != null) cartaRobar.setBounds(pMazos.x + (gap * 2) + 20, pMazos.y, W_CARTA_STD, H_CARTA_STD);
        repaint();
    }
    public void setAccionVolver(Runnable accion) {
        this.accionVolver = accion;
        for(ActionListener al : btnSalir.getActionListeners()) btnSalir.removeActionListener(al);
        btnSalir.addActionListener(e -> accionBotonSalir());
    }
    public void agregarMensajeChat(String msg) { if (panelChat != null) panelChat.agregarMensaje(msg); }
    private void cargarChat(boolean esPro) { if (panelChat != null) remove(panelChat); Object[] opciones = esPro ? MensajesPro.values() : MensajesNormal.values(); panelChat = new PanelChat(opciones, e -> { if (controlador != null) { Object sel = panelChat.getMensajeSeleccionado(); if (sel instanceof MensajesNormal) controlador.enviarMensajeChat((MensajesNormal) sel, miNombre); else if (sel instanceof MensajesPro) controlador.enviarMensajeChat((MensajesPro) sel, miNombre); } }); int xChat = getWidth() - ANCHO_CHAT; panelChat.setBounds((xChat > 0 ? xChat : 1000), 0, ANCHO_CHAT, getHeight()); this.addComponentListener(new ComponentAdapter() { public void componentResized(ComponentEvent e) { panelChat.setBounds(getWidth() - ANCHO_CHAT, 0, ANCHO_CHAT, getHeight()); reacomodarComponentes(); } }); add(panelChat); }
    private void iniciarComponentesUI() { panelMano = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 30)); panelMano.setOpaque(false); add(panelMano); btnSalir = new BotonCristal("SALIR", "BOTON_ROJO.png"); btnSalir.setBounds(20, 20, 100, 40); btnSalir.setFont(new Font("Arial", Font.BOLD, 12)); add(btnSalir); cargarChat(false); }
    private void iniciarAudio() { JPanel pAudio = new PanelAudio(); pAudio.setSize(120, 60); pAudio.setLocation(800, 600); this.addComponentListener(new ComponentAdapter() { public void componentResized(ComponentEvent e) { pAudio.setLocation(getWidth() - ANCHO_CHAT - 130, getHeight() - 80); } }); add(pAudio); }
    private void inicializarMazosCentrales() { cartaRobar = new CartaGrafica(false); cartaRobar.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { intentarPasarTurno(); } }); add(cartaRobar); cartaAsc = null; cartaDes = null; }
    private void cargarRecursos() { try { String nombreFondo = ConfiguracionJuego.getPathFondoJuego(modoProfesional); fondo = cargar("tablero/" + nombreFondo); titulo = cargar("tablero/TITULO_THE_GAME.png"); imgProfesional = cargar("tablero/TEXTO_PROFESIONAL.png"); marcoH = cargar("tablero/ESPACIO_CARTA_HORIZONTAL.png"); marcoV = cargar("tablero/ESPACIO_CARTA_VERTICAL.png"); placaError = cargar("tablero/MENSAJE_OSCURO.png"); btnSiImg = cargar("tablero/BOTON_VERDE.png"); imgReverso180 = cargar("cartas/REVERSO_180_GRADOS.png"); imgReverso90 = cargar("cartas/REVERSO_90_GRADOS.png"); } catch (Exception e) { e.printStackTrace(); } }
    private BufferedImage cargar(String path) { try { InputStream is = getClass().getResourceAsStream("/resources/" + path); return ImageIO.read(is); } catch (Exception e) { return null; } }
    private void cargarFuente() { try { InputStream is = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf"); if (is != null) { fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, is); GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego); } } catch (Exception e) { } }
    private void configurarFuente(Graphics2D g2, float size) { if (fuenteJuego != null) g2.setFont(fuenteJuego.deriveFont(Font.BOLD, size)); else g2.setFont(new Font("Arial", Font.BOLD, (int)size)); }
    private void drawCenteredString(Graphics g, String text, Rectangle rect) { FontMetrics metrics = g.getFontMetrics(g.getFont()); int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2; int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent(); g.drawString(text, x, y); }
    private void dibujarEtiqueta(Graphics2D g2, String txt, int x, int y) { if (placaError == null) return; g2.drawImage(placaError, x, y, 100, 40, null); g2.setColor(Color.LIGHT_GRAY); configurarFuente(g2, 14f); drawCenteredString(g2, txt, new Rectangle(x, y + 10, 100, 40)); }
    private void accionBotonSalir() {
        try { GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON); } catch(Exception e){}

        // Usamos nuestro diálogo personalizado en lugar de JOptionPane
        int n = DialogoSalida.mostrar(SwingUtilities.getWindowAncestor(this));

        // 0 = Guardar, 1 = Salir s/ Guardar, 2 = Cancelar
        if (n == 0) {
            // GUARDAR Y SALIR
            if (controlador != null) controlador.cerrarPartida(true, miNombre);
        } else if (n == 1) {
            // SALIR SIN GUARDAR
            if (controlador != null) controlador.cerrarPartida(false, miNombre);
        }
        // Si es 2, no hacemos nada (Cancela)
    }
}