package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.controlador.Controlador;
import ar.ed.unlu.controlador.EstadoTurno;
import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.modelo.EstadoJuego;
import ar.ed.unlu.modelo.Mazo;
import ar.ed.unlu.modelo.RegistroRanking;
import ar.ed.unlu.vista.IVista;
import ar.ed.unlu.vista.consola.EstadoVistaConsola;
import ar.ed.unlu.vista.grafica.layouts.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VistaGrafica extends JFrame implements IVista {

    private Controlador controlador;
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private String nombreJugador;
    private String ultimoJugadorTurno = "";

    private PanelSalaEspera panelSalaEspera;
    private PanelReglas panelReglas;
    private PanelJuego panelJuego;
    private PanelConfiguracion panelConfig;
    private PanelRanking panelRanking;


    public VistaGrafica(Controlador ctrl, String nombreJugador) {
        this.controlador = ctrl;
        this.nombreJugador = nombreJugador;

        setTitle("The Game: Quick & Easy - Jugador: " + nombreJugador);
        setSize(1224, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);


        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarCierreApp(); // Llamamos a nuestro método personalizado
            }
        });


        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        panelReglas = new PanelReglas(() -> cardLayout.show(panelContenedor, "SALA_ESPERA"));

        panelConfig = new PanelConfiguracion(() -> {
            if (panelSalaEspera != null) panelSalaEspera.actualizarFondo();
            if (panelReglas != null) panelReglas.actualizarFondo();
            if (panelJuego != null) panelJuego.actualizarFondo();
            cardLayout.show(panelContenedor, "SALA_ESPERA");
            this.repaint();
        });

        panelRanking = new PanelRanking(() -> cardLayout.show(panelContenedor, "SALA_ESPERA"));

        panelJuego = new PanelJuego(controlador, nombreJugador);
        panelJuego.setAccionVolver(() -> {
            cardLayout.show(panelContenedor, "SALA_ESPERA");
            GestorAudio.reproducirMusica(GestorAudio.MUSICA_ESPERA);
        });

        panelSalaEspera = new PanelSalaEspera(
                (esModoPro) -> {
                    if (panelJuego != null) panelJuego.setModoJuego(esModoPro);
                    if (controlador != null) controlador.iniciarPartida(esModoPro);
                },
                (mensajeSala) -> { if (controlador != null) controlador.enviarMensajeChat(mensajeSala, nombreJugador); },
                () -> cardLayout.show(panelContenedor, "REGLAS"),
                () -> cardLayout.show(panelContenedor, "CONFIG"),
                () -> {
                    List<RegistroRanking> lista = controlador.getRanking();
                    panelRanking.setRanking(lista);
                    panelRanking.actualizarFondo(); // Por si cambió la config
                    cardLayout.show(panelContenedor, "RANKING");
                }
        );

        panelContenedor.add(panelSalaEspera, "SALA_ESPERA");
        panelContenedor.add(panelReglas, "REGLAS");
        panelContenedor.add(panelConfig, "CONFIG");
        panelContenedor.add(panelJuego, "JUEGO");
        panelContenedor.add(panelRanking, "RANKING");

        add(panelContenedor);
        cardLayout.show(panelContenedor, "SALA_ESPERA");
        GestorAudio.reproducirMusica(GestorAudio.MUSICA_ESPERA);
    }

    private void confirmarCierreApp() {
        GestorAudio.reproducirEfecto(GestorAudio.SFX_BOTON);
        if (panelJuego != null && panelJuego.isShowing()) {
            int opcion = DialogoSalida.mostrar(this);
            if (opcion == 0) {

                if (controlador != null) controlador.cerrarPartida(true, nombreJugador);
                else System.exit(0);
            } else if (opcion == 1) {
                if (controlador != null) controlador.cerrarPartida(false, nombreJugador);
                else System.exit(0);
            }


        } else {
            boolean seguro = DialogoConfirmacion.mostrar(this, "¿SEGURO QUE DESEAS SALIR?");

            if (seguro) {
                if (controlador != null) {
                    // Cerramos la conexión correctamente antes de matar el proceso
                    controlador.cerrarPartida(false, nombreJugador);
                } else {
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void iniciar() { this.setVisible(true); }

    @Override
    public void mostrarSalaEspera(List<String> jugadores) {
        cardLayout.show(panelContenedor, "SALA_ESPERA");
        if (panelSalaEspera != null) panelSalaEspera.actualizarListaJugadores(jugadores);
    }

    @Override
    public void mostrarJuego(List<Carta> mano, List<Mazo> mazos, String jugadorActual, EstadoTurno estado, String feedback) {
        if (!panelJuego.isShowing()) {
            GestorAudio.reproducirMusica(GestorAudio.MUSICA_JUEGO);
            cardLayout.show(panelContenedor, "JUEGO");
        }

        if (panelJuego != null) {
            if (!jugadorActual.equals(this.ultimoJugadorTurno)) {
                this.ultimoJugadorTurno = jugadorActual;
                panelJuego.agregarMensajeChat("\n════════ TURNO DE " + jugadorActual.toUpperCase() + " ════════");
            }
            try {
                // Chequear estado global para saber si pausar
                EstadoJuego estadoGlobal = controlador.getEstadoJuego(); // Necesitarás exponer esto en el controlador si no está

                if (estadoGlobal == EstadoJuego.ESPERANDO_RECONEXION) {
                    panelJuego.setJuegoPausado(true);
                    panelJuego.setMensajeEstado("ESPERANDO JUGADORES...");
                } else {
                    panelJuego.setJuegoPausado(false);
                }
            } catch (Exception e) {}
            boolean esPro = controlador.esModoProfesional();
            panelJuego.setModoJuego(esPro);
            panelJuego.resetearInterfaz();
            panelJuego.actualizarMano(mano);
            panelJuego.actualizarMesa(mazos);

            boolean esMiTurno = jugadorActual.equals(nombreJugador);
            panelJuego.setTurnoActivo(esMiTurno);

            boolean jugadaExitosa = (feedback != null && feedback.contains("xitos"));
            boolean consultarMovimiento = (estado == EstadoTurno.CONSULAR_MOVIMIENTO);

            if (esMiTurno) {
                if (jugadaExitosa || consultarMovimiento) {
                    panelJuego.setMensajeEstado("JUGÁ OTRA CARTA O TOCÁ EL MAZO PARA PASAR");
                } else {
                    panelJuego.setMensajeEstado("¡ES TU TURNO!");
                }
            } else {
                panelJuego.setMensajeEstado("Turno de: " + jugadorActual);
            }



            if (feedback != null && !feedback.isEmpty() && !jugadaExitosa && !feedback.startsWith("¡Jugada")) {
                panelJuego.mostrarError(feedback);
            }
        }

        if (controlador != null && panelJuego != null) {
            try { panelJuego.actualizarRivales(controlador.getNombresJugadores(), controlador.obtenerCantidadCartasRivales()); } catch (Exception e) {}
        }
    }

    @Override
    public void mostrarMensajeChat(String mensaje) {
        if (panelJuego != null && panelJuego.isVisible()) panelJuego.agregarMensajeChat(mensaje);
        else if (panelSalaEspera != null) panelSalaEspera.agregarMensajeChat(mensaje);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        GestorAudio.reproducirEfecto(GestorAudio.SFX_ERROR);
        // Usamos DialogoError como pediste
        DialogoError dialogo = new DialogoError(this, mensaje);
        dialogo.setVisible(true);
    }

    @Override
    public void mostrarPantallaFin(String resultado) {
        SwingUtilities.invokeLater(() -> {
            boolean gano = resultado.equalsIgnoreCase("VICTORIA");
            if (panelJuego != null) panelJuego.mostrarFinJuego(gano, "LA PARTIDA HA TERMINADO");
        });
    }

    @Override
    public void setModoJuego(boolean esPro) {
        if (panelJuego != null) {
            panelJuego.setModoJuego(esPro);
        }
    }

    @Override
    public void mostrarPantallaFin(boolean esVictoria, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            if (panelJuego != null) panelJuego.mostrarFinJuego(esVictoria, mensaje);
        });
    }

    @Override
    public void mostrarJuegoPausado(boolean pausado) {
        if (panelJuego != null) {
            panelJuego.setJuegoPausado(pausado);
        }
    }

    @Override public void limpiarPantalla() {}
    @Override public void setEstado(EstadoVistaConsola estado) {}
}