package ar.ed.unlu.modelo;

public enum MensajesSala {
    HOLA("¡Hola a todos!"),
    LISTO("Estoy listo para jugar."),
    ESPERANDO("Esperando al resto..."),
    BUENA_SUERTE("¡Buena suerte equipo!"),
    SISTEMA("Mensaje de Sistema"),
    ;

    private final String mensaje;

    MensajesSala(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
