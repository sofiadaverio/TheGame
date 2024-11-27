package ar.ed.unlu.modelo;

import java.util.ArrayList;
import java.util.List;

public class Juego {
    private ArrayList<Mazo> mazos;
    private MazoPrincipal mazoPrincipal;
    private Equipo equipo;
    private Integer turnoActual;
    private EstadoJuego estadoJuego;
    private List<String> mensajes;


    public Juego() {
        this.mazos = new ArrayList<>();
        this.mazoPrincipal = new MazoPrincipal();
        this.equipo = new Equipo();
        this.turnoActual = 0;
        this.estadoJuego = EstadoJuego.EN_PROCESO;
        this.mensajes = new ArrayList<>();
    }

    public void iniciarJuego() {
        mazos.add(new Mazo(TipoMazo.ASCENDENTE));
        mazos.add(new Mazo(TipoMazo.DESCENDENTE));

        for (Jugador jugador : equipo.getJugadores()) {
                jugador.robarCarta(mazoPrincipal);
        }
    }

    public boolean jugarTurno(Carta carta, Mazo mazoSeleccionado) {
        if (estadoJuego != EstadoJuego.EN_PROCESO) {
            return false;
        }

        Jugador jugadorActual = equipo.obtenerJugadorActual(turnoActual);
        if (mazoSeleccionado.validarMovimiento(carta)) {
            jugadorActual.colocarCarta(carta, mazoSeleccionado);


            verificarFin(); // Verificar si el juego debe terminar

            return true;
        } else {
            return false; // Movimiento inválido
        }
    }

    public void enviarMensaje(String jugador, String mensaje) {
        mensajes.add(jugador + ": " + mensaje);
    }

    public List<String> getMensajes() {
        return new ArrayList<>(mensajes);
    }

    public boolean verificarFinMazo() {
        if (mazoPrincipal.isVacio()) {
            return true;
        }
        return false;
    }

    public void verificarFin() {
        if (mazoPrincipal.isVacio() && equipo.getJugadores().stream().allMatch(j -> j.getMano().isEmpty())) {
            estadoJuego = EstadoJuego.GANADO;
        } else if (!equipo.hayJugadoresConMovimientos(mazos)) {
            estadoJuego = EstadoJuego.PERDIDO;
        }
    }

    public EstadoJuego getEstadoJuego() {
        return estadoJuego;
    }

    public List<Mazo> getMazos() {
        return mazos;
    }

    public Jugador getJugadorActual() {
        return equipo.obtenerJugadorActual(turnoActual);
    }


    public Equipo getEquipo() {
        return this.equipo;
    }

    public MazoPrincipal getMazoPrincipal() {
        return mazoPrincipal;
    }

    public Integer getTurnoActual() {
        return turnoActual;
    }

    public int pasarTurno() {
        turnoActual = (turnoActual + 1) % equipo.getJugadores().size();  // Avanzar al siguiente jugador en el turno
        return turnoActual;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public boolean tieneMovimientoValidos(Jugador jugador){
        return jugador.tieneMovimientosValidos(getMazos());
    }
}

