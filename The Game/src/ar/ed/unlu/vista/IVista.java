package ar.ed.unlu.vista;

import ar.ed.unlu.controlador.EstadoTurno; // <--- Importa tu Enum
import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.modelo.Mazo;
import java.util.List;

public interface IVista {
    void iniciar();

    void mostrarMensaje(String mensaje);

    void mostrarPantallaFin(String resultado);

    void mostrarMensajeChat(String mensaje);
    void limpiarPantalla();
    // (Ojo: Este setEstado es el de la vista consola, no el del juego. Lo dejamos por compatibilidad)
    void setEstado(ar.ed.unlu.vista.consola.EstadoVistaConsola estado);

    void mostrarSalaEspera(List<String> jugadores);

    void mostrarPantallaFin(boolean esVictoria, String mensaje);

    void setModoJuego(boolean esPro);

    void mostrarJuegoPausado(boolean pausado);

    void mostrarJuego(List<Carta> cartasJugador, List<Mazo> mazos, String nombre, EstadoTurno estadoParaEnviar, String fb);
}