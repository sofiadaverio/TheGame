package ar.ed.unlu.modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorRanking {
    private static final String ARCHIVO = "ranking.ser";


    public static void guardarPuntaje(String equipo, long segundos) {
        List<RegistroRanking> lista = leerRanking();

        lista.add(new RegistroRanking(equipo, segundos));

        Collections.sort(lista);

        if (lista.size() > 5) {
            lista = lista.subList(0, 5);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.err.println("Error guardando ranking: " + e.getMessage());
        }
    }

    // Lee la lista del archivo
    public static List<RegistroRanking> leerRanking() {
        File f = new File(ARCHIVO);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<RegistroRanking>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}