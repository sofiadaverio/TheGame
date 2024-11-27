package ar.ed.unlu.vista;

import ar.ed.unlu.controlador.ControladorGrafico;
import ar.ed.unlu.modelo.Mensajes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class VistaConsolaGrafica extends JFrame {
    private JTextField txtEntrada;
    private JButton btnEnter;
    private JScrollPane scrol;
    private JTextArea txtSalida;
    private JPanel panelPrincipal;
    private ControladorGrafico controlador;
    private EstadoVistaConsola estado;
    private String nombreJugador;

    private void createUIComponents() {
        txtEntrada = new JTextField();
        txtSalida = new JTextArea();
        txtSalida.setEditable(false);
        scrol = new JScrollPane(txtSalida);
    }

    public VistaConsolaGrafica(String nombreJugador, ControladorGrafico controlador) {
        this.nombreJugador = nombreJugador;
        this.controlador = controlador;

        // Personalizar título para el "Administrador"
        if (nombreJugador.equals("Administrador")) {
            setTitle("Administrador");
        } else {
            setTitle("Jugador: " + nombreJugador);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setContentPane(panelPrincipal);
        this.controlador = controlador;
        btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarEntrada(txtEntrada.getText());
                txtEntrada.setText("");
            }
        });
        if (nombreJugador.equals("Administrador")) {
            mostrarMenuPrincipal(); // Solo el Administrador verá el menú principal
        }
    }

    private void procesarEntrada(String entrada) {
        entrada = entrada.trim();
        mostrarMensaje(entrada);

        switch (estado) {
            case MENU_PRINCIPAL:
                controlador.procesarMenuPrincipal(entrada, nombreJugador);
                break;

            case AGREGAR_JUGADOR:
                controlador.procesarAgregarJugador(entrada);
                break;

            case TURNO_JUGADOR:
                controlador.procesarTurnoJugador(entrada, nombreJugador); // Solo llamamos a este método
                break;

            case SEGUNDA_CARTA:
                controlador.procesarConfirmacionSegundaJugada(entrada, nombreJugador); // Aquí se maneja solo la confirmación para jugar otra carta
                break;
        }
        txtEntrada.setText("");
    }

    public void mostrarMenuPrincipal() {
        estado = EstadoVistaConsola.MENU_PRINCIPAL;
        mostrarMensaje("Menú Principal:");
        mostrarMensaje("1. Agregar jugador");
        mostrarMensaje("2. Comenzar juego");
        mostrarMensaje("Selecciona una opción:");
    }


    public void mostrarMensajes(ArrayList<Mensajes> mensajes) {
        estado = EstadoVistaConsola.COMUNICACION;
        limpiarPantalla();
        mostrarMensaje("Mensajes: ");
        for (Mensajes mensaje : mensajes) {
            mostrarMensaje(mensaje + "\n");
        }
        mostrarMensaje("Escribe tu mensaje: ");
    }

    public void mostrarMensaje(String mensaje) {
        txtSalida.append(mensaje + "\n");
    }

    public void mostrarMensajeln(String mensaje) {
        txtSalida.append(mensaje);
    }

    public void setEstado(EstadoVistaConsola estado) {
        this.estado = estado;
    }

    public void iniciar() {
        setVisible(true);
    }

    public void mostrarMensajes(List<String> mensajes) {
        estado = EstadoVistaConsola.COMUNICACION;
        limpiarPantalla(); // Limpia la pantalla
        mostrarMensaje("Mensajes del equipo: ");
        for (String mensaje : mensajes) {
            mostrarMensaje(mensaje);
        }
        mostrarMensaje("Selecciona un mensaje para enviar:");
        for (Mensajes mensaje : Mensajes.values()) {
            mostrarMensaje("- " + mensaje.name() + " (" + mensaje.getMensaje() + ")");
        }
        mostrarMensaje("Escribe el nombre del mensaje: ");
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void limpiarPantalla(){
        txtSalida.setText("");
    }

}





