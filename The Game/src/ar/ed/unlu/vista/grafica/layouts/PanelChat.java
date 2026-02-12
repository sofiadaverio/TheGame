package ar.ed.unlu.vista.grafica.layouts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;

public class PanelChat extends JPanel {

    private JTextArea txtHistorial;
    private JComboBox<Object> cmbMensajes;
    private JButton btnEnviar;
    private Font fuenteTitulo; // Solo necesitamos la del título, el texto usa Segoe UI

    public PanelChat(Object[] mensajesEnum, ActionListener accionEnviar) {
        cargarFuente();
        setLayout(new BorderLayout());

        setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        JPanel panelCaja = new JPanel(new BorderLayout(5, 5));
        panelCaja.setBackground(new Color(0, 0, 0, 240)); // Negro casi opaco


        panelCaja.setBorder(crearBordeConTitulo("CHAT DE SALA"));

        txtHistorial = new JTextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setLineWrap(true);
        txtHistorial.setWrapStyleWord(true);
        txtHistorial.setBackground(new Color(10, 10, 10)); // Fondo oscuro
        txtHistorial.setForeground(Color.WHITE);           // Letra blanca
        txtHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 14));


        JScrollPane scroll = new JScrollPane(txtHistorial);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(10, 10, 10));
        scroll.setOpaque(false);


        JPanel panelScrollWrapper = new JPanel(new BorderLayout());
        panelScrollWrapper.setOpaque(false);
        panelScrollWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelScrollWrapper.add(scroll, BorderLayout.CENTER);

        panelCaja.add(panelScrollWrapper, BorderLayout.CENTER);


        JPanel panelInput = new JPanel(new BorderLayout(5, 0));
        panelInput.setOpaque(false);
        panelInput.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Margen interno abajo


        cmbMensajes = new JComboBox<>(mensajesEnum);
        cmbMensajes.setBackground(new Color(30, 30, 30));
        cmbMensajes.setForeground(Color.WHITE);
        cmbMensajes.setFont(new Font("Arial", Font.BOLD, 12));
        ((JLabel)cmbMensajes.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);


        try {
            btnEnviar = new BotonCristal(">", "BOTON_AZUL.png");
            btnEnviar.setPreferredSize(new Dimension(50, 30));
        } catch (Exception e) {
            btnEnviar = new JButton(">");
            btnEnviar.setBackground(Color.DARK_GRAY);
            btnEnviar.setForeground(Color.WHITE);
        }
        btnEnviar.addActionListener(accionEnviar);

        panelInput.add(cmbMensajes, BorderLayout.CENTER);
        panelInput.add(btnEnviar, BorderLayout.EAST);

        panelCaja.add(panelInput, BorderLayout.SOUTH);


        add(panelCaja, BorderLayout.CENTER);
    }

    private TitledBorder crearBordeConTitulo(String titulo) {
        Border lineaBlanca = BorderFactory.createLineBorder(Color.WHITE, 1);

        TitledBorder bordeTitulo = BorderFactory.createTitledBorder(
                lineaBlanca,
                titulo,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                fuenteTitulo.deriveFont(20f),
                Color.WHITE
        );
        return bordeTitulo;
    }

    public Object getMensajeSeleccionado() {
        return cmbMensajes.getSelectedItem();
    }

    public void agregarMensaje(String msg) {
        txtHistorial.append(msg + "\n");
        txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
    }

    private void cargarFuente() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/fuentes/dealerplate.otf");
            if (is != null) {
                Font fontBase = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fontBase);
                fuenteTitulo = fontBase;
            } else {
                fuenteTitulo = new Font("Arial", Font.BOLD, 18);
            }
        } catch (Exception e) {
            fuenteTitulo = new Font("Arial", Font.BOLD, 18);
        }
    }
}