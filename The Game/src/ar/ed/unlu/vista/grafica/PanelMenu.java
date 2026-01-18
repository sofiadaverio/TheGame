package ar.ed.unlu.vista.grafica;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.function.Consumer;

public class PanelMenu extends JPanel {

    private BufferedImage fondo, tituloImg;
    private JTextField txtNombre;
    private Font fuenteMenu;
    private Consumer<String> accionJugar; // Acción que pasa el nombre al controlador

    public PanelMenu(Consumer<String> accionAlJugar) {
        this.accionJugar = accionAlJugar;
        setLayout(new GridBagLayout()); // Centra todo verticalmente
        cargarRecursos();
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margen entre elementos
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Para que los botones tengan el mismo ancho

        // 1. TÍTULO (Logo del juego)
        JLabel lblTitulo = new JLabel();
        if (tituloImg != null) {
            Image img = tituloImg.getScaledInstance(400, 380, Image.SCALE_SMOOTH);
            lblTitulo.setIcon(new ImageIcon(img));
        } else {
            lblTitulo.setText("THE GAME");
            lblTitulo.setForeground(Color.WHITE);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 60));
            lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        }
        add(lblTitulo, gbc);

        // 2. TEXTO "INGRESA TU NOMBRE"
        gbc.gridy++;
        JLabel lblInstruccion = new JLabel("INGRESA TU NOMBRE:");
        lblInstruccion.setForeground(Color.WHITE);
        lblInstruccion.setHorizontalAlignment(SwingConstants.CENTER);
        if (fuenteMenu != null) lblInstruccion.setFont(fuenteMenu.deriveFont(Font.BOLD, 24f));
        else lblInstruccion.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblInstruccion, gbc);

        // 3. CAMPO DE TEXTO (Input)
        gbc.gridy++;
        txtNombre = new JTextField(15);
        txtNombre.setFont(new Font("Arial", Font.BOLD, 20));
        txtNombre.setHorizontalAlignment(JTextField.CENTER);
        txtNombre.setPreferredSize(new Dimension(300, 45));
        txtNombre.setBackground(new Color(0, 0, 0, 150)); // Negro semitransparente
        txtNombre.setForeground(Color.YELLOW);
        txtNombre.setCaretColor(Color.WHITE);
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        add(txtNombre, gbc);

        // --- ZONA DE BOTONES ---

        // 4. BOTÓN JUGAR (Verde)
        gbc.gridy++;
        gbc.insets = new Insets(30, 10, 10, 10); // Más espacio arriba

        // Usamos tu clase BotonCristal
        BotonCristal btnJugar = new BotonCristal("JUGAR", "BOTON_VERDE.png");
        btnJugar.setPreferredSize(new Dimension(250, 60));

        btnJugar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (!nombre.isEmpty()) {
                accionJugar.accept(nombre); // ¡Avisa a VistaGrafica para cambiar de pantalla!
            } else {
                JOptionPane.showMessageDialog(this, "¡Escribe tu nombre para empezar!");
            }
        });
        add(btnJugar, gbc);

        // 5. BOTÓN REGLAS (Turquesa)
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10); // Espacio normal

        BotonCristal btnReglas = new BotonCristal("REGLAS", "BOTON_TURQUESA.png");
        btnReglas.setPreferredSize(new Dimension(250, 60));

        btnReglas.addActionListener(e -> mostrarReglas());
        add(btnReglas, gbc);

        // 6. BOTÓN SALIR (Rojo)
        gbc.gridy++;

        BotonCristal btnSalir = new BotonCristal("SALIR", "BOTON_ROJO.png");
        btnSalir.setPreferredSize(new Dimension(250, 60));

        btnSalir.addActionListener(e -> System.exit(0)); // Cierra la app
        add(btnSalir, gbc);
    }

    private void mostrarReglas() {
        String reglas = "REGLAS DE THE GAME - QUICK & EASY\n\n" +
                "1. El objetivo es colocar todas las cartas en los mazos centrales.\n" +
                "2. Hay dos mazos ASCENDENTES (1 -> 10) y dos DESCENDENTES (10 -> 1).\n" +
                "3. En tu turno debes jugar al menos 1 carta.\n" +
                "4. TRUCO: Puedes retroceder si la carta es del MISMO COLOR.\n" +
                "   (Ej: En ascendente, si hay un 7 Rojo, puedes poner un 5 Rojo).\n\n" +
                "¡Trabajen en equipo y no revelen números exactos!";

        JOptionPane.showMessageDialog(this, reglas, "Cómo Jugar", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibujamos el fondo violeta
        if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        else { g.setColor(new Color(30, 0, 30)); g.fillRect(0, 0, getWidth(), getHeight()); }
    }

    private void cargarRecursos() {
        try {
            fondo = cargar("tablero/FONDO_VIOLETA.png");
            tituloImg = cargar("tablero/TITULO_THE_GAME.png");

            InputStream is = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (is != null) {
                fuenteMenu = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteMenu);
            }
        } catch (Exception e) { }
    }

    private BufferedImage cargar(String path) {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/" + path);
            if (is == null) is = getClass().getResourceAsStream("/resources/" + path.replace(".png", ".PNG"));
            return ImageIO.read(is);
        } catch (Exception e) { return null; }
    }
}