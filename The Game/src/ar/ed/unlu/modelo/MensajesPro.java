package ar.ed.unlu.modelo;

public enum MensajesPro {

    // --- MODO PROFESIONAL (Solo Color, prohibido decir valor) ---
    TENGO_ROJO("Tengo una carta ROJA."),
    TENGO_AZUL("Tengo una carta AZUL."),
    TENGO_AMARILLO("Tengo una carta AMARILLA."),
    TENGO_VERDE("Tengo una carta VERDE."),
    TENGO_GRIS("Tengo una carta GRIS."),

    // --- ESTRATEGIA Y BLOQUEOS (Lo que pediste) ---
    NO_TOCAR_ASC("¡NO toquen el mazo ASCENDENTE!"),
    NO_TOCAR_DES("¡NO toquen el mazo DESCENDENTE!"),
    PUEDO_JUGAR_ASC("Puedo jugar bien en el Ascendente."),
    PUEDO_JUGAR_DES("Puedo jugar bien en el Descendente."),

    // --- EL TRUCO (Retroceder) ---
    TRUCO_ASC("¡Tengo el salto para recuperar el ASCENDENTE!"),
    TRUCO_DES("¡Tengo el salto para recuperar el DESCENDENTE!");

    private final String mensaje;

    MensajesPro(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }


}
