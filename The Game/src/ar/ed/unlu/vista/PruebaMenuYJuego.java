package ar.ed.unlu.vista;

import ar.ed.unlu.vista.grafica.VistaGrafica;
import javax.swing.*;

public class PruebaMenuYJuego {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Creamos la vista SIN controlador (null)
            // La vista está programada para detectar null y funcionar en "Modo Prueba"
            VistaGrafica vista = new VistaGrafica(null);

            // 2. Mostramos la ventana
            vista.iniciar();

            System.out.println("PRUEBA INICIADA: Deberías ver el Menú Violeta.");
            System.out.println("Escribe un nombre y toca ENTRAR para ver el tablero.");
        });
    }
}
