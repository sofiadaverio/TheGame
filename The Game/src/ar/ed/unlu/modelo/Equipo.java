package ar.ed.unlu.modelo;

import java.util.ArrayList;
import java.util.List;

public class Equipo implements java.io.Serializable{
    private ArrayList<Jugador> jugadores;
    private String nombre;
    private static final int MAX_JUGADORES = 5;

    public Equipo() {
        this.jugadores = new ArrayList<>();
    }

    public boolean agregarJugador(Jugador jugador) {
        if (jugadores.size() >= MAX_JUGADORES) {
            return false;
        }
        if (jugadores.contains(jugador)) {
            return false;
        }
        jugadores.add(jugador);
        return true;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
          this.nombre = nombre;
    }

    public Jugador obtenerJugadorActual(int turno) {

        return jugadores.get(turno % jugadores.size());
    }

    public int pasarTurno(int turnoActual) {
        return (turnoActual + 1) % jugadores.size();
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }


}
