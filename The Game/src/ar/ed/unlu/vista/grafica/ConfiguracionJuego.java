package ar.ed.unlu.vista.grafica;

import java.io.*;
import java.util.Properties;

public class ConfiguracionJuego {

    private static final String ARCHIVO_CONFIG = "config_thegame.properties";

    private static int temaActual = 1;

    public static boolean musicaActivada = true;
    public static boolean efectosActivados = true;

    public static String ultimoNombre = "";
    public static String ultimaIP = "127.0.0.1";

    static {
        cargarConfiguracion();
    }

    public static void guardarDatosConexion(String nombre, String ip) {
        ultimoNombre = nombre;
        ultimaIP = ip;
        guardarConfiguracion();
    }


    public static void setTema(int numeroTema) {
        if (numeroTema >= 1 && numeroTema <= 5) {
            temaActual = numeroTema;
            guardarConfiguracion();
        }
    }
    public static void setMusica(boolean activa) {
        musicaActivada = activa;
        guardarConfiguracion();
    }

    public static void setEfectos(boolean activos) {
        efectosActivados = activos;
        guardarConfiguracion();
    }

    // --- PERSISTENCIA ---
    private static void guardarConfiguracion() {
        Properties prop = new Properties();
        prop.setProperty("musica", String.valueOf(musicaActivada));
        prop.setProperty("efectos", String.valueOf(efectosActivados));
        prop.setProperty("tema", String.valueOf(temaActual));
        // Guardamos también nombre e IP
        prop.setProperty("nombre", ultimoNombre);
        prop.setProperty("ip", ultimaIP);

        try (OutputStream output = new FileOutputStream(ARCHIVO_CONFIG)) {
            prop.store(output, "Configuracion The Game");
        } catch (IOException io) { }
    }

    private static void cargarConfiguracion() {
        File f = new File(ARCHIVO_CONFIG);
        if (!f.exists()) return;

        try (InputStream input = new FileInputStream(ARCHIVO_CONFIG)) {
            Properties prop = new Properties();
            prop.load(input);

            musicaActivada = Boolean.parseBoolean(prop.getProperty("musica", "true"));
            efectosActivados = Boolean.parseBoolean(prop.getProperty("efectos", "true"));
            temaActual = Integer.parseInt(prop.getProperty("tema", "1"));
            ultimoNombre = prop.getProperty("nombre", "");
            ultimaIP = prop.getProperty("ip", "127.0.0.1");

        } catch (IOException ex) { }
    }


    public static String getPathFondoMenu() {
        return "FONDO_GRIS_TEMA_" + temaActual + ".png";
    }

    public static String getPathFondoJuego(boolean esModoPro) {
        if (esModoPro) {
            return "FONDO_VIOLETA_TEMA_" + temaActual + ".png";
        } else {
            return "FONDO_VERDE_TEMA_" + temaActual + ".png";
        }
    }
}
