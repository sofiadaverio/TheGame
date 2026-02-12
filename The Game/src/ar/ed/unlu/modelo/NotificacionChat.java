package ar.ed.unlu.modelo;

import java.io.Serializable;

public class NotificacionChat implements Serializable {
    private String emisor;
    private String mensaje;

    public NotificacionChat(String emisor, String mensaje) {
        this.emisor = emisor;
        this.mensaje = mensaje;
    }

    public String getTextoCompleto() {
        return emisor + ": " + mensaje;
    }
}