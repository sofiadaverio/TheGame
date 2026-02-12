package ar.ed.unlu.modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Juego extends ObservableRemoto implements IJuego, java.io.Serializable {
    private ArrayList<Mazo> mazos;
    private MazoPrincipal mazoPrincipal;
    private Equipo equipo;
    private Integer turnoActual = 0;
    private EstadoJuego estadoJuego;
    private transient List<String> mensajes; //No se guardan los mensajes del chat
    private int cartasJugadasTurnoActual = 0;
    private boolean modoProfesional = false;
    private long tiempoInicio;
    private transient Set<String> jugadoresConectadosActualmente;

    public Juego() {
        this.estadoJuego = EstadoJuego.ESPERANDO;
        this.equipo = new Equipo();
        this.mensajes = new ArrayList<>();
        this.mazos = new ArrayList<>();
        this.mazoPrincipal = new MazoPrincipal();
        this.jugadoresConectadosActualmente = new HashSet<>();
    }

    @Override
    public void iniciarJuego(boolean modoProfesional) throws RemoteException {
        if (this.equipo.getJugadores().size() < 2) {
            throw new RemoteException("Se necesitan mínimo 2 jugadores.");
        }

        // Reinicio completo
        this.mazos = new ArrayList<>();
        this.mazos.add(new Mazo(TipoMazo.ASCENDENTE));
        this.mazos.add(new Mazo(TipoMazo.DESCENDENTE));
        this.mazoPrincipal = new MazoPrincipal();

        for(Jugador jugador : this.equipo.getJugadores()) {
            jugador.getMano().clear();
            jugador.robarCarta(this.mazoPrincipal);
        }

        this.cartasJugadasTurnoActual = 0;
        this.tiempoInicio = System.currentTimeMillis();
        this.modoProfesional = modoProfesional;
        this.estadoJuego = EstadoJuego.EN_PROCESO;
        this.turnoActual = 0;

        // CAMBIO: Usamos Enum en lugar de String
        notificarObservadores(Evento.JUEGO_INICIADO);
    }

    @Override
    public boolean jugarTurno(Carta cartaSelecionada, Mazo mazoSelecionado) throws RemoteException {
        if (this.estadoJuego != EstadoJuego.EN_PROCESO) return false;

        Mazo mazoReal = null;
        for (Mazo m : this.mazos) {
            if (m.getTipoMazo() == mazoSelecionado.getTipoMazo()) {
                mazoReal = m;
                break;
            }
        }
        if (mazoReal == null) return false;

        Jugador jugadorActual = this.equipo.obtenerJugadorActual(this.turnoActual);
        Carta cartaReal = null;

        for (Carta c : jugadorActual.getMano()) {
            if (c.getColor() == cartaSelecionada.getColor() &&
                    c.getNumero().intValue() == cartaSelecionada.getNumero().intValue()) {
                cartaReal = c;
                break;
            }
        }

        if (cartaReal == null) return false;

        try {
            jugadorActual.colocarCarta(cartaReal, mazoReal);
            this.cartasJugadasTurnoActual++;
            this.verificarFin();

            // CAMBIO: Si el juego sigue, notificamos jugada realizada con Enum
            if (this.estadoJuego == EstadoJuego.EN_PROCESO) {
                notificarObservadores(Evento.JUGADA_REALIZADA);
            }
            return true;

        } catch (Exception e) {
            System.err.println("Error jugada: " + e.getMessage());
            return false;
        }
    }

    public void setNombreEquipo(String nombre) {
        this.equipo.setNombre(nombre);
    }

    public String getNombreEquipo() {
        return this.equipo.getNombre();
    }

    @Override
    public void conectarJugador(String nombre) throws RemoteException {
        if (jugadoresConectadosActualmente == null) {
            jugadoresConectadosActualmente = new HashSet<>();
        }

        // PARTIDA NUEVA
        if (this.estadoJuego == EstadoJuego.ESPERANDO) {
            for (Jugador j : equipo.getJugadores()) {
                if (j.getNombre().equalsIgnoreCase(nombre)) {
                    throw new RemoteException("¡El nombre '" + nombre + "' ya está en uso!");
                }
            }
            this.equipo.agregarJugador(new Jugador(nombre));
            this.jugadoresConectadosActualmente.add(nombre);
            notificarObservadores(Evento.JUGADOR_CONECTADO);
        }

        // RECONECTANDO A PARTIDA GUARDADA
        else if (this.estadoJuego == EstadoJuego.ESPERANDO_RECONEXION) {
            boolean existe = false;
            for (Jugador j : equipo.getJugadores()) {
                if (j.getNombre().equalsIgnoreCase(nombre)) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                throw new RemoteException("Esta partida ya empezó y tu nombre no está en la lista de jugadores originales.");
            }

            if (jugadoresConectadosActualmente.contains(nombre)) {
                throw new RemoteException("El jugador " + nombre + " ya está conectado.");
            }

            jugadoresConectadosActualmente.add(nombre);
            transmisionMensaje(MensajesSala.SISTEMA, "SERVIDOR: " + nombre + " se ha reconectado.");

            if (jugadoresConectadosActualmente.size() == equipo.getJugadores().size()) {
                this.estadoJuego = EstadoJuego.EN_PROCESO;
                transmisionMensaje(MensajesSala.SISTEMA, "SERVIDOR: ¡Todos conectados! El juego se reanuda.");
                notificarObservadores(Evento.JUEGO_INICIADO);
            } else {
                int faltan = equipo.getJugadores().size() - jugadoresConectadosActualmente.size();
                transmisionMensaje(MensajesSala.SISTEMA, "SERVIDOR: Esperando a " + faltan + " jugadores más...");
                notificarObservadores(Evento.JUGADOR_CONECTADO);
            }
        }
    }

    @Override
    public void terminarTurno() throws RemoteException {
        Jugador jugadorQueTermina = this.getJugadorActual();
        jugadorQueTermina.robarCarta(this.mazoPrincipal);
        this.pasarTurno();
        this.cartasJugadasTurnoActual = 0;
        this.verificarFin();

        // CAMBIO: Si el juego sigue, notificamos turno cambiado
        if (this.estadoJuego == EstadoJuego.EN_PROCESO) {
            notificarObservadores(Evento.TURNO_CAMBIADO);
        }
    }

    @Override
    public void desconectarJugador(String nombre) throws RemoteException {
        if (jugadoresConectadosActualmente != null) {
            jugadoresConectadosActualmente.remove(nombre);
        }

        if (this.estadoJuego == EstadoJuego.EN_PROCESO) {
            this.estadoJuego = EstadoJuego.ESPERANDO_RECONEXION;
            transmisionMensaje(MensajesSala.SISTEMA, "SERVIDOR: " + nombre + " se desconectó. PAUSANDO JUEGO.");
            notificarObservadores(Evento.TURNO_CAMBIADO);
        } else {
            if (this.estadoJuego == EstadoJuego.ESPERANDO) {
                equipo.getJugadores().removeIf(j -> j.getNombre().equals(nombre));
                notificarObservadores(Evento.JUGADOR_CONECTADO);
            }
        }
    }

    @Override
    public Jugador getJugadorActual() throws RemoteException {
        if (this.equipo.getJugadores().isEmpty()) return null;
        return this.equipo.obtenerJugadorActual(this.turnoActual);
    }

    @Override
    public List<Mazo> getMazos() throws RemoteException {
        return this.mazos;
    }

    @Override
    public List<Carta> getCartasJugador(String nombre) throws RemoteException {
        for (Jugador j : equipo.getJugadores()) {
            if (j.getNombre().equals(nombre)) return j.getMano();
        }
        return new ArrayList<>();
    }

    @Override
    public EstadoJuego getEstadoJuego() throws RemoteException {
        return this.estadoJuego;
    }

    @Override
    public List<String> getNombresJugadores() throws RemoteException {
        List<String> nombres = new ArrayList<>();
        for (Jugador j : equipo.getJugadores()) nombres.add(j.getNombre());
        return nombres;
    }

    @Override
    public boolean tieneMovimientoValidos(Jugador jugador) throws RemoteException {
        return jugador.tieneMovimientosValidos(this.mazos);
    }

    public void verificarFin() throws RemoteException {
        if (this.estadoJuego == EstadoJuego.GANADO || this.estadoJuego == EstadoJuego.PERDIDO) {
            return;
        }

        boolean mazoVacio = this.mazoPrincipal.isVacio();
        boolean manosVacias = this.equipo.getJugadores().stream().allMatch(j -> j.getMano().isEmpty());

        if (mazoVacio && manosVacias) {
            this.estadoJuego = EstadoJuego.GANADO;

            long tiempoFin = System.currentTimeMillis();
            long duracionSegundos = (tiempoFin - tiempoInicio) / 1000;

            GestorRanking.guardarPuntaje(this.equipo.getNombre(), duracionSegundos);

            notificarObservadores(Evento.FIN_DE_PARTIDA);
            return;
        }

        Jugador actual = this.getJugadorActual();

        if (actual.getMano().isEmpty()) {
            return;
        }

        if (this.cartasJugadasTurnoActual > 0) {
            return;
        }
        if (!actual.tieneMovimientosValidos(this.mazos)) {
            this.estadoJuego = EstadoJuego.PERDIDO;

            notificarObservadores(Evento.FIN_DE_PARTIDA);
        }
    }



    public void transmisionMensaje(MensajesPro mensaje, String emisor) throws RemoteException {
        enviarObjetoChat(mensaje.getMensaje(), emisor);
    }

    public void transmisionMensaje(MensajesSala mensaje, String emisor) throws RemoteException {
        enviarObjetoChat(mensaje.getMensaje(), emisor);
    }

    public void transmisionMensaje(MensajesNormal mensaje, String emisor) throws RemoteException {
        enviarObjetoChat(mensaje.getMensaje(), emisor);
    }

    // Helper privado para evitar repetir código
    private void enviarObjetoChat(String texto, String emisor) throws RemoteException {
        notificarObservadores("CHAT:" + emisor + ": " + texto);
    }

    public int pasarTurno() {
        if (this.equipo.getJugadores().isEmpty()) return 0;
        this.turnoActual = (this.turnoActual + 1) % this.equipo.getJugadores().size();
        return this.turnoActual;
    }

    public boolean esModoProfesional() throws RemoteException {
        return this.modoProfesional;
    }

    @Override
    public List<RegistroRanking> obtenerRanking() throws RemoteException {
        return GestorRanking.leerRanking();
    }

    public void guardarPartida() throws RemoteException {
        try {
            String path = System.getProperty("user.dir") + File.separator + "partida_" + this.equipo.getNombre().replaceAll("\\s+", "_") + ".save";

            GestorPersistencia.guardar(this, path);
            System.out.println("Partida guardada en: " + path);

            try {
                transmisionMensaje(MensajesSala.SISTEMA, "SERVIDOR: Partida guardada.");
            } catch (Exception ex) {
                System.out.println("No se pudo notificar a algunos clientes (posible desconexión), pero el juego se guardó.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error crítico al guardar en disco: " + e.getMessage());
        }
    }

    public void restaurarEstadoDespuesDeCargar() {
        if (this.mensajes == null) this.mensajes = new ArrayList<>();
        this.jugadoresConectadosActualmente = new HashSet<>();
        this.estadoJuego = EstadoJuego.ESPERANDO_RECONEXION;
    }
}