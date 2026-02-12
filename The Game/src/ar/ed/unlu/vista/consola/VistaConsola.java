package ar.ed.unlu.vista.consola;

import ar.ed.unlu.controlador.Controlador;
import ar.ed.unlu.controlador.EstadoTurno;
import ar.ed.unlu.modelo.*;
import ar.ed.unlu.vista.IVista;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class VistaConsola extends JFrame implements IVista {
    private JPanel panelPrincipal;
    private JTextArea txtSalida;
    private JTextField txtEntrada;
    private JTextArea txtChat;
    private JComboBox<Object> comboBox;
    private JButton enviarButton;
    private JScrollPane scrol, scroll2;

    private Controlador controlador;
    private String nombreJugador;

    private boolean esperandoConfirmacion = false;
    private boolean esperandoConfirmacionSalida = false;
    private boolean viendoRanking = false;

    private boolean modoJuegoActivo = false;
    private EstadoVistaConsola estado;
    private boolean esModoProfesional;
    private String ultimoJugadorTurno = "";




    private void createUIComponents() {
        txtEntrada = new JTextField();
        txtSalida = new JTextArea(); txtSalida.setEditable(false);
        scrol = new JScrollPane(txtSalida);
        txtChat = new JTextArea(); txtChat.setEditable(false);
        scroll2 = new JScrollPane(txtChat);
        comboBox = new JComboBox<Object>();
        enviarButton = new JButton("Enviar");

    }

    public VistaConsola(Controlador controlador) {
        this.controlador = controlador;
        this.nombreJugador = "Jugador";

        setTitle("The Game - CONSOLA");
        setSize(900, 600);
        setLocationRelativeTo(null);
        if (panelPrincipal != null) setContentPane(panelPrincipal);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                iniciarProcesoSalida();
            }
        });

        txtEntrada.addActionListener(e -> {
            String texto = txtEntrada.getText().trim();
            if (!texto.isEmpty() || viendoRanking) {
                procesarEntrada(texto);
                txtEntrada.setText("");
            }
        });

        for(java.awt.event.ActionListener al : enviarButton.getActionListeners()) {
            enviarButton.removeActionListener(al);
        }

        enviarButton.addActionListener(e -> enviarChat());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                iniciarProcesoSalida();
            }
        });
    }

    private void procesarEntrada(String entrada) {


        if (viendoRanking) {
            viendoRanking = false;
            if (!modoJuegoActivo) {
                mostrarSalaEspera(controlador.getNombresJugadores());
            } else {
                limpiarPantalla();
                mostrarMensaje(">>> Volviendo... (Escribe algo para actualizar)");
            }
            return;
        }

        // 2. LÓGICA DE SALIDA
        if (esperandoConfirmacionSalida) {
            procesarRespuestaSalida(entrada);
            return;
        }

        // 3. COMANDOS GLOBALES
        if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
            iniciarProcesoSalida();
            return;
        }

        // 4. MENÚ PRINCIPAL (Solo si NO estamos jugando)
        if (!modoJuegoActivo) {
            if (entrada.equals("1")) {
                controlador.iniciarPartida(false);
                this.esModoProfesional=false;
                return;
            }
            if (entrada.equals("2")) {
                controlador.iniciarPartida(true);
                this.esModoProfesional=true;
                return;
            }
            if (entrada.equals("3")) {
                mostrarReglas();
                return;
            }
            if (entrada.equals("4")) {
                mostrarRanking();
                return;
            }
            return;
        }

        if (esperandoConfirmacion) {
            if (entrada.equalsIgnoreCase("si")) {
                controlador.confirmarSegundaCarta(true, nombreJugador);
                return;
            } else if (entrada.equalsIgnoreCase("no")) {
                controlador.confirmarSegundaCarta(false, nombreJugador);
                return;
            } else {
                mostrarMensaje("⚠️ Por favor, responde 'si' o 'no'.");
                return;
            }
        }

        if (entrada.contains(" ")) {
            intentarJugarCarta(entrada);
            return;
        }

        mostrarMensaje("Comando no reconocido. Usa el formato: COLOR NUMERO MAZO");
    }


    private void iniciarProcesoSalida() {
        this.esperandoConfirmacionSalida = true;
        limpiarPantalla();
        if (modoJuegoActivo) {
            mostrarMensaje("\n⚠️ ¿DESEAS GUARDAR LA PARTIDA ANTES DE SALIR?");
            mostrarMensaje("   [S] Guardar y Salir");
            mostrarMensaje("   [N] Salir SIN Guardar");
            mostrarMensaje("   [C] Cancelar (Seguir jugando)");
        } else {
            mostrarMensaje("\n⚠️ ¿SEGURO QUE DESEAS SALIR DEL JUEGO?");
            mostrarMensaje("   [S] SÍ, SALIR");
            mostrarMensaje("   [N] NO, VOLVER");
        }
        txtEntrada.requestFocus();
    }

    private void procesarRespuestaSalida(String entrada) {
        entrada = entrada.toUpperCase().trim();
        if (modoJuegoActivo) {
            if (entrada.equals("S")) {
                controlador.cerrarPartida(true, nombreJugador);
            } else if (entrada.equals("N")) {
                controlador.cerrarPartida(false, nombreJugador);
            } else if (entrada.equals("C")) {
                esperandoConfirmacionSalida = false;
                mostrarMensaje(">>> Volviendo al juego...");
            }
        } else {
            if (entrada.equals("S")) {
                System.exit(0);
            } else {
                esperandoConfirmacionSalida = false;
                mostrarSalaEspera(controlador.getNombresJugadores());
            }
        }
    }


    private void enviarChat() {
        Object seleccionado = comboBox.getSelectedItem();
        if (seleccionado == null) return;

        controlador.enviarMensajeChat(seleccionado, nombreJugador);
        txtEntrada.requestFocus();
    }


    @Override
    public void mostrarSalaEspera(List<String> jugadores) {
        this.viendoRanking = false;
        actualizarComboChat(false);
        limpiarPantalla();
        mostrarMensaje("=== SALA DE ESPERA (" + jugadores.size() + "/5) ===");
        for(String j : jugadores) mostrarMensaje(" > " + j);
        mostrarMensaje("1. Iniciar Partida (NORMAL)");
        mostrarMensaje("2. Iniciar Partida (PROFESIONAL)");
        mostrarMensaje("3. Ver Reglas");
        mostrarMensaje("4. Ver Ranking");
        mostrarMensaje("--------------------------");
        txtEntrada.requestFocus();
    }

    @Override
    public void mostrarJuego(List<Carta> mano, List<Mazo> mazos, String jugadorActual, EstadoTurno estado, String feedback) {
        if (esperandoConfirmacionSalida) return;
        this.viendoRanking = false;

        actualizarComboChat(true);
        limpiarPantalla();

        if (!jugadorActual.equals(this.ultimoJugadorTurno)) {
            this.ultimoJugadorTurno = jugadorActual;
            txtChat.append("\n════════ TURNO DE " + jugadorActual.toUpperCase() + " ════════\n");
            txtChat.setCaretPosition(txtChat.getDocument().getLength());
        }

        boolean esMiTurno = nombreJugador.equals(jugadorActual);
        this.esperandoConfirmacion = (esMiTurno && estado == EstadoTurno.CONSULAR_MOVIMIENTO);

        if (esMiTurno) mostrarMensaje(">>> ES TU TURNO <<<");
        else mostrarMensaje("Turno de: " + jugadorActual);

        mostrarMensaje("\n--- TUS CARTAS ---");
        for (Carta c : mano) mostrarMensaje(c.getColor() + " " + c.getNumero());

        mostrarMensaje("\n--- MAZOS ---");
        for (Mazo m : mazos) {
            Carta t = m.obtenerUltimaCarta();
            mostrarMensaje(m.getTipoMazo() + ": " + ((t==null)?"Vacío":t.getColor()+" "+t.getNumero()));
        }

        if (feedback != null && !feedback.isEmpty()) mostrarMensaje("\n>> " + feedback);

        mostrarMensaje("\n-------------------");
        if (esMiTurno) {
            if (esperandoConfirmacion) {
                mostrarMensaje("¿Quieres jugar otra carta? (si/no)");
            } else {
                mostrarMensaje("Jugada: Color Numero Mazo");
            }
            txtEntrada.requestFocus();
        }
    }

    private void intentarJugarCarta(String entrada) {
        try {
            String[] partes = entrada.split("\\s+");
            if (partes.length < 3) return;
            Carta c = parserCarta(partes[0], partes[1]);
            Mazo m = parserMazo(partes[2]);
            controlador.realizarJugada(c, m, nombreJugador);
        } catch (Exception e) {
            mostrarMensaje("Error: " + e.getMessage());
        }
    }

    private void mostrarRanking() {
        limpiarPantalla();
        this.viendoRanking = true;

        mostrarMensaje("=================================");
        mostrarMensaje("       HALL OF FAME (TOP 5)      ");
        mostrarMensaje("=================================");

        List<RegistroRanking> ranking = controlador.getRanking();

        if (ranking.isEmpty()) {
            mostrarMensaje("   Aún no hay registros.");
        } else {
            int puesto = 1;
            mostrarMensaje(String.format("%-5s %-20s %-10s", "POS", "EQUIPO", "TIEMPO"));
            mostrarMensaje("---------------------------------");
            for (RegistroRanking r : ranking) {
                mostrarMensaje(String.format("#%-4d %-20s %-10s", puesto++, r.getNombreEquipo(), r.getTiempoFormateado()));
            }
        }
        mostrarMensaje("=================================");
        mostrarMensaje("\nPresiona ENTER para volver...");
        txtEntrada.requestFocus();
    }

    public void mostrarReglas() {
        // TEXTO ORIGINAL MANTENIDO
        mostrarMensaje("\n=================================================================");
        mostrarMensaje("           THE GAME: QUICK & EASY - REGLAMENTO");
        mostrarMensaje("=================================================================");
        mostrarMensaje("OBJETIVO:");
        mostrarMensaje("  - Juegan como un equipo. Deben colocar las 50 cartas en los dos mazos.");
        mostrarMensaje("  - Mazo 1: ASCENDENTE (1 al 10).");
        mostrarMensaje("  - Mazo 2: DESCENDENTE (10 al 1).");

        mostrarMensaje("\nTURNO DEL JUGADOR:");
        mostrarMensaje("  1. Debes jugar 1 o 2 cartas de tu mano.");
        mostrarMensaje("  2. Repones tu mano al final del turno (vuelves a tener 2 cartas).");

        mostrarMensaje("\nEL TRUCO DE LA MARCHA ATRÁS (REVERSE):");
        mostrarMensaje("  - Normalmente debes respetar el orden (subir en Ascendente, bajar en Descendente).");
        mostrarMensaje("  - PERO: Si juegas una carta del MISMO COLOR exacto que la que está en la mesa,");
        mostrarMensaje("    puedes ignorar el orden y 'retroceder'.");
        mostrarMensaje("    (Ej: En el mazo Ascendente hay un 7 Verde, puedes jugar un 2 Verde encima).");

        mostrarMensaje("\nCOMUNICACIÓN:");
        mostrarMensaje("  - ¡Hablen entre ustedes!");
        mostrarMensaje("  - PROHIBIDO decir números exactos ('Tengo el 9 rojo').");
        mostrarMensaje("  - PERMITIDO dar pistas vagas ('Tengo una roja alta', 'No toques el mazo descendente').");

        mostrarMensaje("\nMODO PROFESIONAL:");
        mostrarMensaje("  - Solo se juega EXACTAMENTE 1 carta por turno (nunca 2).");
        mostrarMensaje("  - Prohibido dar pistas sobre valores (alto/bajo/medio). Solo colores.");
        mostrarMensaje("=================================================================\n");

        mostrarMensaje(">>> Presiona ENTER para volver...");
        this.viendoRanking = true;
    }

    // --- UTILS (Sin cambios lógicos) ---

    private Carta parserCarta(String colorStr, String numStr) {
        try {
            int num = Integer.parseInt(numStr);
            String c = colorStr.toLowerCase();
            ColorCarta color = null;
            if (c.startsWith("r")) color = ColorCarta.ROJA;
            else if (c.startsWith("v")) color = ColorCarta.VERDE;
            else if (c.startsWith("az")) color = ColorCarta.AZUL;
            else if (c.startsWith("am")) color = ColorCarta.AMARILLO;
            else if (c.startsWith("g")) color = ColorCarta.GRIS;
            else try { color = ColorCarta.valueOf(colorStr.toUpperCase()); } catch(Exception e){}
            return new Carta(num, color);
        } catch (Exception e) { throw new IllegalArgumentException("Error carta:Color desconocido"); }
    }
    private Mazo parserMazo(String mazoStr) {
        String m = mazoStr.toLowerCase();
        if (m.startsWith("a")) return new Mazo(TipoMazo.ASCENDENTE);
        if (m.startsWith("d")) return new Mazo(TipoMazo.DESCENDENTE);
        throw new IllegalArgumentException("Mazo incorrecto. Usa: 'asc' o 'a' / 'des' o 'd'");
    }

    public void actualizarComboChat(boolean juego) {
        if(modoJuegoActivo == juego && comboBox.getItemCount() > 0 && !juego) return;
        modoJuegoActivo = juego;
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        if (!juego) {
            for (MensajesSala m : MensajesSala.values()) modelo.addElement(m);
        } else {
            if (this.esModoProfesional) {
                for (MensajesPro m : MensajesPro.values()) modelo.addElement(m);
            } else {
                for (MensajesNormal m : MensajesNormal.values()) modelo.addElement(m);
            }
        }
        comboBox.setModel(modelo);
    }

    public void setNombre(String n) { this.nombreJugador = n; setTitle("Jugador: " + n); }
    public void iniciar() { setVisible(true); }
    @Override public void mostrarMensaje(String m) { txtSalida.append(m+"\n"); txtSalida.setCaretPosition(txtSalida.getDocument().getLength()); }
    @Override public void mostrarMensajeChat(String m) { txtChat.append(m+"\n"); txtChat.setCaretPosition(txtChat.getDocument().getLength()); }
    @Override public void limpiarPantalla() { txtSalida.setText(""); }
    @Override public void setEstado(EstadoVistaConsola estado) { this.estado = estado; }

    @Override public void mostrarPantallaFin(boolean esVictoria, String mensaje) {
        if (esVictoria) this.estado = EstadoVistaConsola.VICTORIA;
        else this.estado = EstadoVistaConsola.DERROTA;
        mostrarPantallaFin();
    }
    public void mostrarPantallaFin() {
        limpiarPantalla();
        if (this.estado == EstadoVistaConsola.VICTORIA) {
            mostrarMensaje("#################################");
            mostrarMensaje("#          ¡VICTORIA!           #");
            mostrarMensaje("#   HAN VENCIDO AL JUEGO        #");
            mostrarMensaje("#################################");
        } else if (this.estado == EstadoVistaConsola.DERROTA) {
            mostrarMensaje("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            mostrarMensaje("x         GAME OVER             x");
            mostrarMensaje("x   No quedan movimientos.      x");
            mostrarMensaje("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        }
        mostrarMensaje("\n(Escribe algo o cierra la ventana para salir)");
    }
    @Override public void mostrarPantallaFin(String s) {}
    @Override public void setModoJuego(boolean esPro) { this.esModoProfesional = esPro; }
    @Override public void mostrarJuegoPausado(boolean pausado) {
        if(pausado) { limpiarPantalla(); mostrarMensaje("=== JUEGO PAUSADO: ESPERANDO JUGADORES ==="); }
    }
}