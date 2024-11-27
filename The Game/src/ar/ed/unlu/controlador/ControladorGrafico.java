package ar.ed.unlu.controlador;

import ar.ed.unlu.controlador.EstadoTurno;
import ar.ed.unlu.modelo.*;
import ar.ed.unlu.vista.*;

import java.util.*;
import java.util.stream.Collectors;

public class ControladorGrafico {
    private Juego juego;
    private Map<String, VistaConsolaGrafica> vistasPorJugador = new HashMap<>();
    private EstadoTurno estadoTurno;

    public ControladorGrafico(Juego juego) {
        this.juego = juego;
        this.estadoTurno = EstadoTurno.PRIMER_CARTA;
    }

    public void agregarVistaJugador(String nombreJugador, VistaConsolaGrafica vista) {
        vistasPorJugador.put(nombreJugador, vista);
        vista.iniciar();
    }

    public void procesarMenuPrincipal(String entrada, String nombreJugador) {
        VistaConsolaGrafica vista = vistasPorJugador.get(nombreJugador);
        switch (entrada) {
            case "1": // Agregar jugador
                vista.mostrarMensaje("Introduce el nombre del jugador:");
                vista.setEstado(EstadoVistaConsola.AGREGAR_JUGADOR);
                break;
            case "2": // Comenzar juego
                if (juego.getEquipo().getJugadores().size() < 2) {
                    vista.mostrarMensaje("Se necesitan al menos 2 jugadores para comenzar.");
                } else {
                    iniciarJuego();
                }
                break;
            default:
                vista.mostrarMensaje("Opción inválida.");
        }
    }

    public void procesarAgregarJugador(String nombreJugador) {
        if (!nombreJugador.isBlank()) {
            Jugador nuevoJugador = new Jugador(nombreJugador.trim());
            if (juego.getEquipo().agregarJugador(nuevoJugador)) {
                // Crear una nueva ventana para el jugador
                VistaConsolaGrafica nuevaVista = new VistaConsolaGrafica(nombreJugador, this);
                agregarVistaJugador(nombreJugador, nuevaVista);
                nuevaVista.iniciar();
                vistasPorJugador.get("Administrador").mostrarMensaje("Jugador " + nombreJugador + " agregado.");

                // Volver al menú principal después de agregar el jugador
                vistasPorJugador.get("Administrador").setEstado(EstadoVistaConsola.MENU_PRINCIPAL);
                vistasPorJugador.get("Administrador").mostrarMenuPrincipal(); // Mostrar el menú nuevamente
            } else {
                vistasPorJugador.get("Administrador").mostrarMensaje("No se pueden agregar más jugadores.");
            }
        } else {
            vistasPorJugador.get("Administrador").mostrarMensaje("El nombre no puede estar vacío.");
        }
    }

    // Iniciar turno de un jugador
    public void procesarTurnoJugador(String entrada, String nombreJugador) {
        Jugador jugadorActual = juego.getJugadorActual();

        if (!jugadorActual.getNombre().equals(nombreJugador)) {
            notificarVista(nombreJugador, "No es tu turno.");
            return;
        }

        switch (estadoTurno) {
            case PRIMER_CARTA:
                jugarCarta(entrada, nombreJugador);
                break;
            case SEGUNDA_CARTA:
                jugarCarta(entrada, nombreJugador);
                break;
            case CONSULAR_MOVIMIENTO:
                procesarConfirmacionSegundaJugada(entrada, nombreJugador);
                break;
        }
    }
    public void procesarConfirmacionSegundaJugada(String entrada, String nombreJugador) {
        if (entrada.equalsIgnoreCase("sí")) {
            vistasPorJugador.get(nombreJugador).mostrarMensaje("Introduce tu segunda carta en formato: [color] [número] [mazo]");
            estadoTurno = EstadoTurno.SEGUNDA_CARTA;
        } else if (entrada.equalsIgnoreCase("no")) {
            finalizarTurno(); // Si el jugador no quiere jugar otra carta, finalizamos el turno.
        } else {
            notificarVista(nombreJugador, "Entrada inválida. Responde con 'sí' o 'no'.");
        }
    }

    private void jugarCarta(String entrada, String nombreJugador) {
        try {
            String[] partes = entrada.trim().split("\\s+");

            if (partes.length != 3) {
                throw new IllegalArgumentException("Formato inválido. Usa: [color] [número] [mazo]");
            }

            Carta carta = convertirACarta(partes[0], Integer.parseInt(partes[1]));
            Mazo mazo = convertirAMazo(partes[2]);

            boolean exito = juego.jugarTurno(carta, mazo);
            if (exito) {
                notificarVista(nombreJugador, "Movimiento realizado con éxito.");


                boolean tieneMovimientosValidos = juego.tieneMovimientoValidos(juego.getJugadorActual());
                if (!tieneMovimientosValidos) {
                    finalizarTurno();
                } else {
                    if (estadoTurno == EstadoTurno.PRIMER_CARTA) {
                        estadoTurno = EstadoTurno.CONSULAR_MOVIMIENTO;
                        actualizarMesa();
                        vistasPorJugador.get(nombreJugador)
                                .mostrarMensaje("¿Quieres jugar otra carta? (sí/no): ");
                    }
                }
            } else {
                notificarVista(nombreJugador, "Movimiento inválido. Intenta de nuevo.");
            }
        } catch (Exception e) {
            notificarVista(nombreJugador, "Error: " + e.getMessage());
        }
    }

    private Carta convertirACarta(String color, int numero) {
        try {
            return new Carta(numero, ColorCarta.valueOf(color.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El color '" + color + "' no es válido.");
        }
    }

    private Mazo convertirAMazo(String tipoMazo) {
        for (Mazo mazo : juego.getMazos()) {
            if (mazo.getTipoMazo().toString().equalsIgnoreCase(tipoMazo)) {
                return mazo;
            }
        }
        throw new IllegalArgumentException("Mazo no encontrado.");
    }


    public void enviarMensaje(String nombreJugador, String mensaje) {
        juego.enviarMensaje(nombreJugador, mensaje);
        actualizarVistasMensajes();
    }

    public void iniciarJuego() {
        juego.iniciarJuego();
        for (VistaConsolaGrafica vista : vistasPorJugador.values()) {
            vista.mostrarMensaje("El juego ha comenzado.");
            vista.setEstado(EstadoVistaConsola.TURNO_JUGADOR); // Establece el estado de la vista
        }
        actualizarMesa();
    }

    public void enviarMensaje(String nombreJugador, Mensajes mensaje) {
        Jugador jugador = juego.getEquipo().getJugadores().stream()
                .filter(j -> j.getNombre().equals(nombreJugador))
                .findFirst()
                .orElse(null);

        if (jugador != null) {
            juego.getEquipo().enviarMensaje(jugador, mensaje);
            actualizarVistasMensajes();
        }
    }

    private void actualizarVistasMensajes() {
        ArrayList<Mensajes> mensajes = juego.getEquipo().obtenerMensajes();
        for (VistaConsolaGrafica vista : vistasPorJugador.values()) {
            vista.mostrarMensajes(mensajes);
        }
    }

    private void notificarVista(String nombreJugador, String mensaje) {
        VistaConsolaGrafica vista = vistasPorJugador.get(nombreJugador);
        if (vista != null) {
            vista.mostrarMensaje(mensaje);
        }
    }

    private void finalizarTurno() {
        Jugador jugadorActual = juego.getJugadorActual();
        estadoTurno = EstadoTurno.TURNO_FINALIZADO;

        if (!juego.verificarFinMazo()) {
            jugadorActual.robarCarta(juego.getMazoPrincipal());
        }

        juego.verificarFin();
        if (juego.getEstadoJuego() == EstadoJuego.GANADO) {
            notificarFinJuego("¡Juego terminado! Han ganado.");
            return;
        } else if (juego.getEstadoJuego() == EstadoJuego.PERDIDO) {
            notificarFinJuego("¡Juego terminado! No hay más movimientos válidos. Han perdido.");
            return;
        }

        // Pasar turno
        juego.pasarTurno();
        estadoTurno = EstadoTurno.PRIMER_CARTA;

        Jugador siguienteJugador = juego.getJugadorActual();

        // Actualizar la mesa para todos
        actualizarMesa();

    }


    private void notificarFinJuego(String mensaje) {
        for (VistaConsolaGrafica vista : vistasPorJugador.values()) {
            vista.mostrarMensaje(mensaje);
        }
    }

    private void actualizarMesa() {
        for (Jugador jugador : juego.getEquipo().getJugadores()) {
            VistaConsolaGrafica vista = vistasPorJugador.get(jugador.getNombre());
            vista.limpiarPantalla();
            if (vista != null) {
                // Mostrar turno del jugador actual
                if (jugador == juego.getJugadorActual()) {
                    vista.mostrarMensaje("Es tu turno, " + jugador.getNombre() + ".");
                } else {
                    vista.mostrarMensaje("Turno de " + juego.getJugadorActual().getNombre() + ".");
                }

                List<String> cartasMano = jugador.getMano().stream()
                        .map(c -> c.getNumero() + " " + c.getColor())
                        .collect(Collectors.toList());

                // Si el jugador tiene cartas, mostrar sus cartas
                if (!cartasMano.isEmpty()) {
                    vista.mostrarMensaje("Tus cartas:");
                    cartasMano.forEach(vista::mostrarMensaje);
                } else {
                    vista.mostrarMensaje("No tienes cartas.");
                }

                // Mostrar los mazos disponibles
                vista.mostrarMensaje("Mazos disponibles:");
                for (Mazo mazo : juego.getMazos()) {
                    Carta ultimaCarta = mazo.obtenerUltimaCarta();
                    String contenido;
                    if (ultimaCarta != null) {
                        contenido = ultimaCarta.getColor() + " " + ultimaCarta.getNumero();
                    } else {
                        contenido = "Vacío";
                    }
                    vista.mostrarMensaje("- " + mazo.getTipoMazo().name() + ": " + contenido);
                }

                // Mostrar la solicitud para ingresar un movimiento solo si es el turno del jugador actual
                if (jugador == juego.getJugadorActual() && estadoTurno == EstadoTurno.PRIMER_CARTA) {
                    vista.mostrarMensaje("Introduce tu movimiento en formato [color] [número] [mazo]:");
                }
            }
        }
    }
}



