package ar.ed.unlu.modelo;

import ar.ed.unlu.controlador.ControladorGrafico;
import ar.ed.unlu.vista.VistaConsolaGrafica;

public class Main {
    public static void main(String[] args) {
        // Crear el modelo y controlador del juego
        Juego juego = new Juego();
        ControladorGrafico controlador = new ControladorGrafico(juego);

        // Crear la vista inicial para el administrador del juego
        VistaConsolaGrafica vistaAdministrador = new VistaConsolaGrafica("Administrador", controlador);
        controlador.agregarVistaJugador("Administrador", vistaAdministrador);

        // Mostrar la vista inicial
        vistaAdministrador.iniciar();
    }
}