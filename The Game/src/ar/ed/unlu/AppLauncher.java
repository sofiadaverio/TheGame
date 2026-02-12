package ar.ed.unlu;

import ar.ed.unlu.controlador.Controlador;
import ar.ed.unlu.modelo.GestorPersistencia;
import ar.ed.unlu.modelo.Juego;
import ar.ed.unlu.vista.IVista;
import ar.ed.unlu.vista.consola.VistaConsola;
import ar.ed.unlu.vista.grafica.ConfiguracionJuego;
import ar.ed.unlu.vista.grafica.VistaGrafica;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import ar.edu.unlu.rmimvc.servidor.Servidor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class AppLauncher {

    private static Servidor servidor;
    private static Juego modelo;

    public static void main(String[] args) {
        ArrayList<String> ipsLocales = Util.getIpDisponibles();
        Object[] ipsArray = ipsLocales.toArray();

        String[] roles = {"Crear Partida (Host)", "Unirme a Partida (Cliente)"};
        int seleccionRol = JOptionPane.showOptionDialog(null,
                "¿Qué deseas hacer?", "The Game - Launcher",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);

        if (seleccionRol == -1) return;

        String ipServidor = "127.0.0.1";
        int puertoServidor = 8888;
        String ipCliente = "127.0.0.1";
        int puertoCliente = new Random().nextInt(1000) + 9000;

        try {
            if (seleccionRol == 0) {
                String ipElegida = (String) JOptionPane.showInputDialog(null,
                        "Selecciona TU IP para el Servidor:", "Configurar Host",
                        JOptionPane.QUESTION_MESSAGE, null, ipsArray, ipsArray[0]);

                if (ipElegida == null) return;

                ipServidor = ipElegida;
                ipCliente = ipElegida;

                String[] opcionesJuego = {"Nueva Partida", "Cargar Partida"};
                int eleccionJuego = JOptionPane.showOptionDialog(null,
                        "¿Cómo quieres iniciar?", "Gestión de Partida",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opcionesJuego, opcionesJuego[0]);

                if (eleccionJuego == -1) return;

                Juego juegoAIniciar = null;

                if (eleccionJuego == 1) {
                    // --- CARGAR PARTIDA ---
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Seleccionar archivo de partida (.save)");
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos guardados (*.save)", "save"));
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        try {
                            juegoAIniciar = (Juego) GestorPersistencia.cargar(selectedFile.getAbsolutePath());
                            juegoAIniciar.restaurarEstadoDespuesDeCargar();
                            JOptionPane.showMessageDialog(null, "¡Partida cargada correctamente!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error cargando archivo: " + ex.getMessage() + "\nSe iniciará una nueva partida.");
                            juegoAIniciar = null;
                        }
                    } else {
                        return;
                    }
                }

                if (juegoAIniciar == null) {
                    String nombreEquipo = JOptionPane.showInputDialog(null,
                            "Nombre del Equipo (Para el Ranking):", "Nueva Partida",
                            JOptionPane.QUESTION_MESSAGE);

                    if (nombreEquipo == null) return; // Si apretó Cancelar, sale.

                    if (nombreEquipo.trim().isEmpty()) {
                        nombreEquipo = "Equipo-" + new Random().nextInt(1000);
                    }


                    juegoAIniciar = new Juego();
                    juegoAIniciar.setNombreEquipo(nombreEquipo);
                }

                iniciarServidor(ipElegida, puertoServidor, juegoAIniciar);

            } else {

                String ipElegidaCliente = (String) JOptionPane.showInputDialog(null,
                        "Selecciona TU IP LOCAL (Cliente):", "Configurar Cliente",
                        JOptionPane.QUESTION_MESSAGE, null, ipsArray, ipsArray[0]);
                if (ipElegidaCliente == null) return;
                ipCliente = ipElegidaCliente;

                String ipGuardada = ConfiguracionJuego.ultimaIP;
                String inputIp = (String) JOptionPane.showInputDialog(null,
                        "Ingresa la IP del HOST (Servidor):", "Conectar",
                        JOptionPane.QUESTION_MESSAGE, null, null, ipGuardada);

                if (inputIp == null) return; // Cancelar
                if (inputIp.trim().isEmpty()) return; // Aceptar vacío
                ipServidor = inputIp;
            }


            String nombreGuardado = ConfiguracionJuego.ultimoNombre;
            String nombreJugador = (String) JOptionPane.showInputDialog(null,
                    "Elige tu Nombre de Jugador:", "Login",
                    JOptionPane.QUESTION_MESSAGE, null, null, nombreGuardado);

            if (nombreJugador == null) return; // Si apretó Cancelar, sale.

            if (nombreJugador.trim().isEmpty()) {
                nombreJugador = "Jugador-" + new Random().nextInt(100);
            }

            ConfiguracionJuego.guardarDatosConexion(nombreJugador, ipServidor);

            iniciarCliente(ipCliente, puertoCliente, ipServidor, puertoServidor, nombreJugador);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error grave al iniciar: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void iniciarServidor(String ip, int puerto, Juego juegoInstancia) throws RemoteException, RMIMVCException {
        System.setProperty("java.rmi.server.hostname", ip);
        modelo = juegoInstancia;
        servidor = new Servidor(ip, puerto);
        servidor.iniciar(modelo);
        System.out.println(">>> SERVIDOR INICIADO EN: " + ip + ":" + puerto);
        System.out.println(">>> EQUIPO: " + modelo.getNombreEquipo());
    }

    private static void iniciarCliente(String ipCliente, int puertoCliente, String ipServidor, int puertoServidor, String nombre) {
        try {
            String[] opcionesVista = {"Gráfica (Ventana)", "Consola (Texto)"};
            int tipoVista = JOptionPane.showOptionDialog(null,
                    "¿Cómo quieres jugar?", "Seleccionar Vista",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opcionesVista, opcionesVista[0]);

            if (tipoVista == -1) return;

            Controlador controlador = new Controlador();
            IVista vista;

            if (tipoVista == 0) {
                vista = new VistaGrafica(controlador, nombre);
            } else {
                VistaConsola consola = new VistaConsola(controlador);
                consola.setNombre(nombre);
                vista = consola;
            }

            Cliente cliente = new Cliente(ipCliente, puertoCliente, ipServidor, puertoServidor);
            System.out.println("Conectando Cliente " + ipCliente + " -> Servidor " + ipServidor);

            cliente.iniciar(controlador);

            vista.iniciar();
            controlador.agregarVista(nombre, vista);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error conectando al servidor: " + e.getMessage() +
                    "\nVerifica que el Servidor esté prendido y la IP sea correcta.");
            e.printStackTrace();
            System.exit(0);
        }
    }
}