package ar.ed.unlu.vista.grafica.layouts;

import ar.ed.unlu.vista.grafica.ConfiguracionJuego;
import ar.ed.unlu.vista.grafica.GestorAudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;

public class PanelAudio extends JPanel {

    private BotonIcono btnMusica;
    private BotonIcono btnSonido;

    public PanelAudio() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        setOpaque(false);

        // Botón Música
        btnMusica = new BotonIcono(
                "MUSICA_ON", "MUSICA_OFF",
                ConfiguracionJuego.musicaActivada,
                e -> {
                    boolean nuevoEstado = !ConfiguracionJuego.musicaActivada;
                    ConfiguracionJuego.musicaActivada = nuevoEstado;
                    if (nuevoEstado) GestorAudio.reproducirMusica(GestorAudio.MUSICA_ESPERA);
                    else GestorAudio.detenerMusica();
                }
        );
        btnMusica.setPreferredSize(new Dimension(50, 50));

        // Botón Sonido
        btnSonido = new BotonIcono(
                "SONIDO_ON", "SONIDO_OFF",
                ConfiguracionJuego.efectosActivados,
                e -> ConfiguracionJuego.efectosActivados = !ConfiguracionJuego.efectosActivados
        );
        btnSonido.setPreferredSize(new Dimension(50, 50));

        add(btnMusica);
        add(btnSonido);

        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                actualizarIconos();
            }
        });
    }

    private void actualizarIconos() {
        btnMusica.setEstado(ConfiguracionJuego.musicaActivada);
        btnSonido.setEstado(ConfiguracionJuego.efectosActivados);
    }
}