package ar.ed.unlu.modelo;

import java.io.*;

public class GestorPersistencia {

    // Guarda cualquier objeto serializable en un archivo
    public static void guardar(Object objeto, String nombreArchivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(objeto);
        }
    }

    // Carga un objeto desde un archivo
    public static Object cargar(String nombreArchivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            return ois.readObject();
        }
    }
}