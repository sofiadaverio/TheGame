package ar.ed.unlu.vista.grafica;

import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.modelo.ColorCarta;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PruebaGrafica {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {

                // 1. CORRECCIÓN: El constructor nuevo NO recibe nombre (solo el controlador)
                // Pasamos 'null' porque estamos probando sin lógica real.
                VistaGrafica vista = new VistaGrafica(null);

                // Mostramos la ventana (aparecerá el Menú Violeta)
                vista.iniciar();

                // 2. SIMULAR INICIO DE PARTIDA (Saltando el menú manualmente para la prueba)
                // Esto forzará el cambio de pantalla al Tablero de Juego
                List<String> jugadores = Arrays.asList("Sofia", "Rival 1");
                vista.iniciarPartida(jugadores);

                // 3. PONER CARTAS EN LA MESA
                vista.actualizarMesa(1, 100);

                // 4. PONER CARTAS EN TU MANO
                List<Carta> mano = new ArrayList<>();
                mano.add(new Carta(10, ColorCarta.ROJA));
                mano.add(new Carta(99, ColorCarta.AZUL));
                vista.actualizarMano(mano);

                // 5. PRUEBA DE ERROR (A los 2 segundos)
                new Timer(2000, e -> {
                    vista.mostrarError("¡CUIDADO! No puedes jugar ahí.");
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}