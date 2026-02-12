package ar.ed.unlu.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class RegistroRanking implements Serializable, Comparable<RegistroRanking> {
    private String nombreEquipo;
    private long tiempoSegundos;
    private String fecha;

    public RegistroRanking(String nombre, long tiempoSegundos) {
        this.nombreEquipo = nombre;
        this.tiempoSegundos = tiempoSegundos;
        this.fecha = LocalDate.now().toString();
    }

    public String getNombreEquipo() { return nombreEquipo; }


    public String getTiempoFormateado() {
        long minutos = tiempoSegundos / 60;
        long segundos = tiempoSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    @Override
    public int compareTo(RegistroRanking o) {
        // Ordenar por tiempo de menor a mayor (el más rápido gana)
        return Long.compare(this.tiempoSegundos, o.tiempoSegundos);
    }
}
