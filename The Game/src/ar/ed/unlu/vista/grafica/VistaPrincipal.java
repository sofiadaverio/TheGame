package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.controlador.ControladorConsola;
import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.vista.grafica.layouts.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VistaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private PanelJuego panelJuego;

    public VistaPrincipal(ControladorConsola ctrl, String nombreJugador) {
        setTitle("The Game - Quick & Easy");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // Inicializar PanelJuego
        panelJuego = new PanelJuego(ctrl, nombreJugador, new LayoutCincoJugadores());

        JPanel menu = new JPanel(); menu.setBackground(Color.BLACK);
        menu.add(new JLabel("MENU"));

        panelContenedor.add(menu, "MENU");
        panelContenedor.add(panelJuego, "JUEGO");

        add(panelContenedor);
        cardLayout.show(panelContenedor, "MENU");
    }

    public void iniciarPartida(List<String> jugadores) {
        LayoutMesa layoutSeleccionado;
        int n = jugadores.size();

        // LÓGICA DE SELECCIÓN DE LAYOUT
        if (n == 5) {
            System.out.println("Cargando Layout de 5 Jugadores");
            layoutSeleccionado = new LayoutCincoJugadores();
        } else if (n == 4) {
            System.out.println("Cargando Layout de 4 Jugadores (Rectángulo)");
            layoutSeleccionado = new LayoutCuatroJugadores();
        } else if (n == 3) {
            layoutSeleccionado = new LayoutTresJugadores();
        } else if (n == 2) {
            layoutSeleccionado = new LayoutDosJugadores();
        } else {
            layoutSeleccionado = new LayoutCuatroJugadores();
        }

        panelJuego.setJugadores(jugadores);
        panelJuego.setLayoutMesa(layoutSeleccionado);
        cardLayout.show(panelContenedor, "JUEGO");

    }

    public void actualizarMano(List<Carta> cartas) { panelJuego.actualizarMano(cartas); }
    public void actualizarMesa(int asc, int des) { panelJuego.actualizarMesa(asc, des); }
    public void mostrarError(String msg) { panelJuego.mostrarError(msg); }
}