package ar.ed.unlu.vista.grafica;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class GestorAudio {

    // CONSTANTES CON LOS NOMBRES EXACTOS DE TUS ARCHIVOS (Asumo extensión .wav)
    public static final String MUSICA_ESPERA  = "Sonido_Espera.wav";
    public static final String MUSICA_JUEGO   = "Sonido_Juego.wav";

    public static final String SFX_BOTON      = "Sonido_Boton.wav";
    public static final String SFX_CARTA      = "Sonido_Carta.wav";
    public static final String SFX_ERROR      = "Sonido_Error.wav";
    public static final String SFX_VICTORIA   = "Sonido_Victoria.wav";
    public static final String SFX_DERROTA    = "Sonido_Derrota.wav";

    private static Clip clipMusica;
    private static String musicaActual = "";

    /**
     * Reproduce música de fondo en bucle.
     * Si ya está sonando la misma, no hace nada.
     * Si es otra, detiene la anterior y arranca la nueva.
     */
    public static void reproducirMusica(String archivo) {
        if (!ConfiguracionJuego.musicaActivada) return;

        // Si ya está sonando esta canción, no la reiniciamos
        if (clipMusica != null && clipMusica.isRunning() && archivo.equals(musicaActual)) {
            return;
        }

        detenerMusica(); // Parar la anterior

        try {
            // CARGA DESDE /resources/sonidos/
            String path = "/resources/sonidos/" + archivo;
            InputStream is = GestorAudio.class.getResourceAsStream(path);

            if (is != null) {
                // BufferedInputStream ayuda a evitar problemas de marcado en algunos wavs
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

                clipMusica = AudioSystem.getClip();
                clipMusica.open(audioIn);

                // Bajar un poco el volumen de la música para que no tape los efectos
                FloatControl gainControl = (FloatControl) clipMusica.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f); // -10 decibeles

                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                clipMusica.start();
                musicaActual = archivo;
            } else {
                System.err.println("No se encontró el audio: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void detenerMusica() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
            musicaActual = "";
        }
    }

    public static void reproducirEfecto(String archivo) {
        // --- ESTA ES LA LÍNEA CLAVE ---
        if (!ConfiguracionJuego.efectosActivados) {
            return; // Si está desactivado, NO hace nada.
        }
        // ------------------------------

        new Thread(() -> {
            try {
                // NOTA: Asegurate que la ruta sea la correcta para tu proyecto
                // Si usas carpeta resources marcada como root: "/sonidos/" + archivo
                // Si no: "/resources/sonidos/" + archivo
                String path = "/resources/sonidos/" + archivo;

                InputStream is = GestorAudio.class.getResourceAsStream(path);
                if (is != null) {
                    InputStream bufferedIn = new java.io.BufferedInputStream(is);
                    javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(bufferedIn);
                    javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                    clip.open(audioIn);

                    // Control de volumen opcional (bajar un poquito los efectos)
                    // FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // gainControl.setValue(-5.0f);

                    clip.start();
                }
            } catch (Exception e) {
                // e.printStackTrace(); // Descomentar para ver errores si no suena
            }
        }).start();
    }

}