package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.controlador.ControladorConsola;
import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.modelo.Mensajes; // Importante
import ar.ed.unlu.vista.grafica.layouts.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VistaGrafica extends JFrame {

    private ControladorConsola controlador;
    private CardLayout cardLayout;
    private JPanel panelContenedor;

    // --- PANTALLAS ---
    // private PanelMenu panelMenu; // YA NO LO USAMOS
    private PanelSalaEspera panelSalaEspera; // NUEVA PANTALLA
    private PanelJuego panelJuego;

    public VistaGrafica(ControladorConsola ctrl) {
        this.controlador = ctrl;

        setTitle("The Game - Quick & Easy");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // 1. INICIALIZAR SALA DE ESPERA (En lugar de Menú)
        // Runnable onIniciar -> Llama a iniciar partida real
        // Consumer<Mensajes> onMensaje -> Manda mensaje al chat
        panelSalaEspera = new PanelSalaEspera(
                () -> solicitarInicioPartida(),
                (mensaje) -> enviarMensajeAlChat(mensaje)
        );

        // 2. INICIALIZAR JUEGO
        panelJuego = new PanelJuego(ctrl, "Jugador", new LayoutCincoJugadores());

        // 3. AGREGAR PANTALLAS
        panelContenedor.add(panelSalaEspera, "SALA_ESPERA");
        panelContenedor.add(panelJuego, "JUEGO");

        add(panelContenedor);

        // Empezamos mostrando la SALA DE ESPERA
        cardLayout.show(panelContenedor, "SALA_ESPERA");

        // Simulamos que agregamos un jugador local a la lista visualmente
        // En el futuro, esto lo hará el setJugadores cuando el servidor avise
        panelSalaEspera.actualizarListaJugadores(java.util.Arrays.asList("Yo (Conectado)"));
    }

    // --- ACCIONES ---

    private void solicitarInicioPartida() {
        // Aquí le dirías al controlador que empiece
        System.out.println("Solicitando inicio de partida...");
        // TEMPORAL: Para probar, forzamos el cambio
        iniciarPartida(java.util.Arrays.asList("Jugador 1", "Jugador 2", "Jugador 3"));
    }

    private void enviarMensajeAlChat(Mensajes m) {
        System.out.println("Enviando mensaje: " + m);
        // controlador.enviarMensaje(...)
        // TEMPORAL: Lo mostramos localmente
        panelSalaEspera.agregarMensajeChat("Yo: " + m.toString());
    }

    // --- MÉTODOS PÚBLICOS ---

    public void iniciarPartida(List<String> jugadores) {
        LayoutMesa layoutSeleccionado;
        int n = jugadores.size();

        if (n == 5) layoutSeleccionado = new LayoutCincoJugadores();
        else if (n == 4) layoutSeleccionado = new LayoutCuatroJugadores();
        else if (n == 3) layoutSeleccionado = new LayoutTresJugadores();
        else if (n == 2) layoutSeleccionado = new LayoutDosJugadores();
        else layoutSeleccionado = new LayoutCuatroJugadores();

        if (panelJuego != null) {
            panelJuego.setJugadores(jugadores);
            panelJuego.setLayoutMesa(layoutSeleccionado);
        }
        cardLayout.show(panelContenedor, "JUEGO");
    }

    public void setJugadores(List<String> jugadores) {
        // Actualizamos la lista visual de la sala de espera
        if (panelSalaEspera != null) {
            panelSalaEspera.actualizarListaJugadores(jugadores);
        }
        // Y también preparamos el panel de juego
        if (panelJuego != null) {
            panelJuego.setJugadores(jugadores);
        }
    }

    public void actualizarMano(List<Carta> cartas) {
        if (panelJuego != null) panelJuego.actualizarMano(cartas);
    }

    public void actualizarMesa(int asc, int des) {
        if (panelJuego != null) panelJuego.actualizarMesa(asc, des);
    }

    public void mostrarError(String msg) {
        if (panelJuego != null && panelJuego.isVisible()) panelJuego.mostrarError(msg);
        else JOptionPane.showMessageDialog(this, msg);
    }

    public void iniciar() {
        this.setVisible(true);
    }
}