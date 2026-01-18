package ar.ed.unlu.vista;

import ar.ed.unlu.modelo.Carta;
import ar.ed.unlu.modelo.ColorCarta;
import ar.ed.unlu.vista.grafica.VistaPrincipal;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PruebaGraficaModular {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // "Sofia" es tu nombre
            VistaPrincipal vista = new VistaPrincipal(null, "Sofia");

            // Enviamos 4 jugadores en total (Sofia + 3 rivales)
            List<String> jugadores = Arrays.asList("Jaoquin", "Sofia","Lucia","Paula");

            vista.iniciarPartida(jugadores);

            List<Carta> mano = new ArrayList<>();
            mano.add(new Carta(10, ColorCarta.ROJA));
            mano.add(new Carta(1, ColorCarta.AZUL));
            vista.actualizarMano(mano);

            // 6. PRUEBA DE PLACA DE ERROR (A los 2 segundos aparece)
            new Timer(2000, e -> {
                vista.mostrarError("¡CUIDADO! No puedes jugar ahí.");
            }).start();

            vista.setVisible(true);
        });
    }
}