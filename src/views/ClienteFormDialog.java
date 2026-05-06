package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import models.ClienteModelo;

public class ClienteFormDialog extends JDialog {

    private static final Color BG_DARK = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color TEXT_DARK = Color.decode("#323C46");
    private static final Color BG_LIGHT = Color.decode("#EBF0F5");

    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    
    // Variable para saber si el usuario guardó los datos o canceló
    private boolean guardado = false;
    
    // Objeto que almacena los datos del cliente que estamos editando o creando
    private ClienteModelo cliente;

    /**
     * Constructor de la ventana.
     * @param owner La ventana principal (padre).
     * @param clienteAEditar Si es null, creamos un nuevo cliente. Si no, mostramos sus datos para editarlos.
     */
    public ClienteFormDialog(Window owner, ClienteModelo clienteAEditar) {
        super(owner, clienteAEditar == null ? "Agregar Cliente Nuevo" : "Editar Cliente", ModalityType.APPLICATION_MODAL);
        this.cliente = clienteAEditar != null ? clienteAEditar : new ClienteModelo();
        
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBackground(BG_LIGHT);

        formPanel.add(crearLabel("Nombre Completo:"));
        txtNombre = crearTextField();
        formPanel.add(txtNombre);

        formPanel.add(crearLabel("Teléfono:"));
        txtTelefono = crearTextField();
        formPanel.add(txtTelefono);

        formPanel.add(crearLabel("Correo Electrónico:"));
        txtCorreo = crearTextField();
        formPanel.add(txtCorreo);

        if (clienteAEditar != null) {
            txtNombre.setText(cliente.getNombreCompleto());
            txtTelefono.setText(cliente.getTelefono());
            txtCorreo.setText(cliente.getCorreo());
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_LIGHT);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new BotonEstilizado("Guardar");
        btnGuardar.addActionListener(e -> guardar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JTextField crearTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Inter", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    /**
     * Método que se ejecuta cuando el usuario hace clic en "Guardar".
     * Extrae el texto de las cajas de texto y lo guarda en el objeto 'cliente'.
     */
    private void guardar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        cliente.setNombreCompleto(txtNombre.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setCorreo(txtCorreo.getText().trim());
        
        guardado = true; // Indicamos que se guardó correctamente
        dispose(); // Cerramos la ventana
    }

    public boolean isGuardado() {
        return guardado;
    }

    public ClienteModelo getCliente() {
        return cliente;
    }

    // Clase interna para botón con estilo redondeado y amarillo
    private static class BotonEstilizado extends JButton {
        public BotonEstilizado(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(BG_DARK);
            setFont(new Font("Inter", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 35));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(GOLD);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            super.paintComponent(g);
            g2.dispose();
        }
    }
}
