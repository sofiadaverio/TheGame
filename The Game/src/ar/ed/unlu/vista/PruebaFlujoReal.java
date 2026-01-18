package ar.ed.unlu.vista;

import javax.swing.*;
import ar.ed.unlu.vista.grafica.*;

public class PruebaFlujoReal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Crear la vista (Si hiciste el Rebuild, esto ya no dará error rojo)
                VistaGrafica vista = new VistaGrafica(null);

                // 2. Mostrar la ventana
                vista.iniciar();

                System.out.println("--- PRUEBA ---");
                System.out.println("Deberías ver el Menú Violeta.");
                System.out.println("Escribe un nombre y dale a ENTRAR.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}