package ar.ed.unlu.modelo;

public enum MensajesNormal {

    BAJA_ROJO("Tengo una carta BAJA de color ROJO."),
    BAJA_AZUL("Tengo una carta BAJA de color AZUL."),
    BAJA_AMARILLO("Tengo una carta BAJA de color AMARILLO."),
    BAJA_VERDE("Tengo una carta BAJA de color VERDE."),
    BAJA_GRIS("Tengo una carta BAJA de color GRIS."),

    INTERMEDIA_ROJO("Tengo una carta MEDIA de color ROJO."),
    INTERMEDIA_AZUL("Tengo una carta MEDIA de color AZUL."),
    INTERMEDIA_AMARILLO("Tengo una carta MEDIA de color AMARILLO."),
    INTERMEDIA_VERDE("Tengo una carta MEDIA de color VERDE."),
    INTERMEDIA_GRIS("Tengo una carta MEDIA de color GRIS."),

    ALTA_ROJO("Tengo una carta ALTA de color ROJO."),
    ALTA_AZUL("Tengo una carta ALTA de color AZUL."),
    ALTA_AMARILLO("Tengo una carta ALTA de color AMARILLO."),
    ALTA_VERDE("Tengo una carta ALTA de color VERDE."),
    ALTA_GRIS("Tengo una carta ALTA de color GRIS."),

    // --- ESTRATEGIA Y BLOQUEOS (Lo que pediste) ---
    NO_TOCAR_ASC("¡NO toquen el mazo ASCENDENTE!"),
    NO_TOCAR_DES("¡NO toquen el mazo DESCENDENTE!"),
    PUEDO_JUGAR_ASC("Puedo jugar bien en el Ascendente."),
    PUEDO_JUGAR_DES("Puedo jugar bien en el Descendente."),

    // --- EL TRUCO (Retroceder) ---
    TRUCO_ASC("¡Tengo el salto para recuperar el ASCENDENTE!"),
    TRUCO_DES("¡Tengo el salto para recuperar el DESCENDENTE!");
    ;

    private final String mensaje;

    MensajesNormal(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}

