package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Vista principal del Login.
 * Pantalla dividida: imagen de fondo a la izquierda, formulario a la derecha.
 * Incluye logo TMU, campos de usuario/contraseña y botón ACCEDER.
 */
public class LoginVista extends JFrame {

    private static final Color GOLD = Color.decode("#FFB800");
    private static final Color TEXT_LIGHT = Color.decode("#D5B578");

    private CampoTexto campoUsuario;
    private CampoTexto campoPassword;
    private BotonAcceder botonAcceder;
    private PanelFondo panelFondo;

    public LoginVista() {
        setTitle("UABCS - Taller Mecánico | Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 560);
        setMinimumSize(new Dimension(700, 480));
        setLocationRelativeTo(null);

        // Panel de fondo con imagen
        panelFondo = new PanelFondo("src" + java.io.File.separator + "img" + java.io.File.separator + "fondo.png");
        panelFondo.setLayout(new GridBagLayout());
        setContentPane(panelFondo);

        GridBagConstraints gbc = new GridBagConstraints();

        // Espaciador izquierdo (50% para la imagen)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelFondo.add(Box.createGlue(), gbc);

        // Panel del formulario (50% derecho)
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        JPanel formulario = crearFormulario();
        panelFondo.add(formulario, gbc);
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // Logo TMU (carga logo.png si existe, si no dibuja con texto)
        // TM
        JLabel tm = new JLabel("TM");
        tm.setFont(new Font("Inter", Font.BOLD, 45));
        tm.setForeground(GOLD);
        tm.setAlignmentX(Component.CENTER_ALIGNMENT);
        //U
        JLabel logoLabel = new JLabel(IconoManager.cargarIcono("U.png", 100, 80));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setPreferredSize(new Dimension(280, 100));
        logoPanel.setMaximumSize(new Dimension(280, 100));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(logoLabel);

        // Título
        JLabel titulo = new JLabel("UABCS - TALLER MECANICO");
        titulo.setFont(new Font("Inter", Font.BOLD, 15));
        titulo.setForeground(GOLD);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Campo USUARIO ---
        JPanel labelUsuario = crearLabelConIcono("USUARIO / CORREO", "icon user.png");
        campoUsuario = new CampoTexto(false);
        campoUsuario.setMaximumSize(new Dimension(280, 65));
        campoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Campo CONTRASEÑA ---
        JPanel labelPassword = crearLabelConIcono("CONTRASEÑA", "icon pass.png");
        campoPassword = new CampoTexto(true);
        campoPassword.setMaximumSize(new Dimension(280, 65));
        campoPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Botón ACCEDER ---
        botonAcceder = new BotonAcceder();
        botonAcceder.setMaximumSize(new Dimension(280, 45));
        botonAcceder.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ensamblar
        contenido.add(tm);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(logoPanel);
        contenido.add(Box.createVerticalStrut(5));
        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(25));
        contenido.add(labelUsuario);
        contenido.add(Box.createVerticalStrut(3));
        contenido.add(campoUsuario);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(labelPassword);
        contenido.add(Box.createVerticalStrut(3));
        contenido.add(campoPassword);
        contenido.add(Box.createVerticalStrut(25));
        contenido.add(botonAcceder);

        panel.add(contenido);
        return panel;
    }

    /** Crea un JLabel con icono PNG + texto. */
    private JPanel crearLabelConIcono(String texto, String iconoPng) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panel.setMaximumSize(new Dimension(280, 24));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(IconoManager.cargarIcono(iconoPng, 18, 18));
        panel.add(iconLabel);

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Inter", Font.PLAIN, 12));
        lbl.setForeground(TEXT_LIGHT);
        panel.add(lbl);

        return panel;
    }

    // === Getters para el Controlador ===

    public CampoTexto getCampoUsuario() {
        return campoUsuario;
    }

    public CampoTexto getCampoPassword() {
        return campoPassword;
    }

    public BotonAcceder getBotonAcceder() {
        return botonAcceder;
    }
}
