package ar.ed.unlu.controlador;

import ar.ed.unlu.modelo.*;
import ar.ed.unlu.vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controlador implements IControladorRemoto {

    private IJuego juego;
    private Map<String, IVista> vistasPorJugador;
    private EstadoTurno estadoTurno;
    private String mensajeFeedback = "";
    private List<String> nombresJugadores;
    private boolean esmodoProfesional;

    public Controlador() {
        this.vistasPorJugador = new HashMap<>();
        this.estadoTurno = EstadoTurno.PRIMER_CARTA;
    }

    public void iniciarPartida(boolean modoProfesional) {
        try {
            this.juego.iniciarJuego(modoProfesional);
            this.esmodoProfesional = modoProfesional;
        } catch (RemoteException e) {
            String mensajeError = e.getMessage();
            if (e.detail != null && e.detail.getMessage() != null) {
                mensajeError = e.detail.getMessage();
            }
            if (mensajeError.contains("mínimo 2 jugadores")) {
                mensajeError = "Se necesitan mínimo 2 jugadores para iniciar.";
            }
            for (IVista vista : vistasPorJugador.values()) {
                vista.mostrarMensaje(mensajeError);
            }
        }
    }

    public void realizarJugada(Carta carta, Mazo mazo, String nombreJugador) {
        try {
            if (!validarTurno(nombreJugador)) return;

            // 1. JUGAR LA CARTA
            boolean exito = this.juego.jugarTurno(carta, mazo);

            if (exito) {
                this.mensajeFeedback = "¡Jugada exitosa!";

                EstadoJuego estadoActual = this.juego.getEstadoJuego();
                if (estadoActual == EstadoJuego.GANADO || estadoActual == EstadoJuego.PERDIDO) {
                    actualizarVistas();
                    return;
                }

                boolean esPro = this.juego.esModoProfesional();

                if (esPro) {
                    // CASO 1: MODO PROFESIONAL
                    terminarTurno();
                } else {
                    // CASO 2: MODO NORMAL
                    if (this.estadoTurno == EstadoTurno.PRIMER_CARTA) {

                        if (this.juego.tieneMovimientoValidos(this.juego.getJugadorActual())) {
                            this.estadoTurno = EstadoTurno.CONSULAR_MOVIMIENTO;
                            actualizarVistas();
                        } else {
                            terminarTurno();
                        }
                    } else {
                        terminarTurno();
                    }
                }

            } else {
                this.mensajeFeedback = "Movimiento inválido.";
                actualizarVistas();
            }
        } catch (Exception e) {
            this.mensajeFeedback = "Error: " + e.getMessage();
            try { actualizarVistas(); } catch (Exception ex) {}
        }
    }

    // Método para el botón "Pasar" (Click en mazo)
    public void solicitarPasarTurno(String nombreJugador) {
        try {
            if (!validarTurno(nombreJugador)) return;

            // Solo permitimos pasar si ya jugó al menos una carta.
            if (this.estadoTurno == EstadoTurno.PRIMER_CARTA) {
                this.mensajeFeedback = "¡DEBES JUGAR AL MENOS 1 CARTA!";
                actualizarVistas();
            } else if (this.estadoTurno == EstadoTurno.CONSULAR_MOVIMIENTO || this.estadoTurno == EstadoTurno.SEGUNDA_CARTA) {
                terminarTurno();
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    // Método para compatibilidad con Consola
    public void confirmarSegundaCarta(boolean quiereJugarOtra, String nombreJugador) {
        try {
            if (!validarTurno(nombreJugador)) return;
            if (quiereJugarOtra) {
                this.estadoTurno = EstadoTurno.SEGUNDA_CARTA;
                this.mensajeFeedback = "Jugá tu segunda carta.";
                actualizarVistas();
            } else {
                terminarTurno();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void terminarTurno() throws RemoteException {
        this.juego.terminarTurno();
        this.estadoTurno = EstadoTurno.PRIMER_CARTA;
        this.mensajeFeedback = "";
        actualizarVistas();
    }

    private boolean validarTurno(String nombreJugador) throws RemoteException {
        Jugador actual = this.juego.getJugadorActual();
        return actual != null && actual.getNombre().equals(nombreJugador);
    }

    public void enviarChat(MensajesPro m, String emisor) {
        try { this.juego.transmisi0nMensaje(m, emisor); }
        catch (RemoteException e) { e.printStackTrace(); }
    }

    public void enviarChat(MensajesNormal m, String emisor) {
        try { this.juego.transmisi0nMensaje(m, emisor); }
        catch (RemoteException e) { e.printStackTrace(); }
    }

    public void enviarChat(MensajesSala m, String emisor) {
        try { this.juego.transmisi0nMensaje(m, emisor); }
        catch (RemoteException e) { e.printStackTrace(); }
    }

    public void enviarMensajeChat(Object m, String emisor) {
        try {
            if (m instanceof MensajesSala) {
                this.juego.transmisi0nMensaje((MensajesSala) m, emisor);
            } else if (m instanceof MensajesNormal) {
                this.juego.transmisi0nMensaje((MensajesNormal) m, emisor);
            } else if (m instanceof MensajesPro) {
                this.juego.transmisi0nMensaje((MensajesPro) m, emisor);
            } else if (m instanceof String) {
                // Para la consola si escriben manualmente, o si el combo devuelve String
                String texto = (String) m;
                // Opción A: Enviar como texto plano (necesitas un método en el modelo)
                // Opción B: Buscar el Enum por su contenido de texto (getMensaje())
                for (MensajesSala ms : MensajesSala.values()) {
                    if (ms.getMensaje().equals(texto) || ms.name().equals(texto)) {
                        this.juego.transmisi0nMensaje(ms, emisor);
                        return;
                    }
                }
                // Repetir para Normal y Pro...
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void actualizarVistas() {
        try {
            EstadoJuego estadoGral = this.juego.getEstadoJuego();
            List<String> nombres = this.juego.getNombresJugadores();
            this.nombresJugadores = nombres;
            boolean esPro = this.juego.esModoProfesional();
            this.esmodoProfesional = esPro;

            if (estadoGral == EstadoJuego.ESPERANDO_RECONEXION) { // Si agregaste este enum
                for (IVista v : vistasPorJugador.values()) v.mostrarJuegoPausado(true);
                return;
            }

            for (IVista v : vistasPorJugador.values()) v.mostrarJuegoPausado(false);

            if (estadoGral == EstadoJuego.ESPERANDO) {
                for (IVista v : vistasPorJugador.values()) v.mostrarSalaEspera(nombres);
                return;
            }

            if (estadoGral == EstadoJuego.GANADO || estadoGral == EstadoJuego.PERDIDO) {
                boolean victoria = (estadoGral == EstadoJuego.GANADO);
                String mensajeFin = victoria ? "VICTORIA" : "DERROTA";

                for (IVista v : vistasPorJugador.values()) {
                    v.mostrarPantallaFin(victoria, mensajeFin);
                }
                return;
            }

            Jugador actual = this.juego.getJugadorActual();
            if (actual == null) return;
            List<Mazo> mazos = this.juego.getMazos();

            for (Map.Entry<String, IVista> entry : vistasPorJugador.entrySet()) {
                String nombre = entry.getKey();
                IVista vista = entry.getValue();

                vista.setModoJuego(esPro);

                boolean esTurno = nombre.equals(actual.getNombre());
                String fb = (esTurno) ? mensajeFeedback : "";

                EstadoTurno estadoParaEnviar;
                if (!esTurno) {
                    estadoParaEnviar = EstadoTurno.TURNO_FINALIZADO;
                } else {
                    estadoParaEnviar = this.estadoTurno;
                }

                vista.mostrarJuego(this.juego.getCartasJugador(nombre), mazos, actual.getNombre(), estadoParaEnviar, fb);
            }
            if (!mensajeFeedback.startsWith("Error")) mensajeFeedback = "";

        } catch (RemoteException e) { e.printStackTrace(); }
    }

    @Override
    public void actualizar(IObservableRemoto modelo, Object evento) throws RemoteException {
        if (evento instanceof String && ((String) evento).startsWith("CHAT:")) {
            String msg = ((String) evento).substring(5);
            for (IVista v : vistasPorJugador.values()) v.mostrarMensajeChat(msg);
        } else {
            actualizarVistas();
        }
    }


    public void agregarVista(String nombre, IVista vista) {
        vistasPorJugador.put(nombre, vista);
        try {
            this.juego.conectarJugador(nombre);
            actualizarVistas();
        } catch (RemoteException e) {
            String mensajeLimpio = e.getMessage();
            if (e.detail != null && e.detail.getMessage() != null) {
                mensajeLimpio = e.detail.getMessage();
            }
            throw new RuntimeException(mensajeLimpio);
        }
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException { this.juego = (IJuego) modeloRemoto; }
    public List<String> getNombresJugadores() { return nombresJugadores; }
    public boolean esModoProfesional() { return this.esmodoProfesional; }
    public Map<String, Integer> obtenerCantidadCartasRivales() {
        Map<String, Integer> cantidades = new HashMap<>();
        try {
            if (this.juego == null || this.nombresJugadores == null) return cantidades;
            for (String jugador : this.nombresJugadores) {
                List<Carta> mano = this.juego.getCartasJugador(jugador);
                cantidades.put(jugador, (mano != null) ? mano.size() : 0);
            }
        } catch (Exception e) {}
        return cantidades;
    }
    public List<RegistroRanking> getRanking() {
        try { return this.juego.obtenerRanking(); } catch (RemoteException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public void cerrarPartida(boolean guardar, String nombreJugador) {
        try {
            if (guardar) {
                // Intentamos guardar en el servidor
                this.juego.guardarPartida();
            }

            // Intentamos avisar que nos vamos
            this.juego.desconectarJugador(nombreJugador);

        } catch (RemoteException e) {
            System.out.println("Desconexión forzada (Servidor no responde).");
        } finally {
            // ESTO SE EJECUTA SIEMPRE (Haya error o no)
            System.exit(0);
        }
    }

    public EstadoJuego getEstadoJuego() throws RemoteException {
        return juego.getEstadoJuego();
    }


}