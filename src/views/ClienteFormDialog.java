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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import models.ClienteModelo;

public class ClienteFormDialog extends JDialog {

	private static final Color TEAL = Color.decode("#005064");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color BG_FIELD = Color.decode("#EBEBEB");

    private JTextField txtNombre;
    private JComboBox<String> cmbHistorial;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    
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
        
        setSize(520, 350);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));
                g2.setColor(TEAL);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));
                g2.dispose();
            }
        };
        bgPanel.setOpaque(false);
        bgPanel.setLayout(new BorderLayout());
        bgPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Row 1
        JPanel pnlRow1 = new JPanel(new BorderLayout());
        pnlRow1.setOpaque(false);

        JPanel pnlNombre = new JPanel(new BorderLayout()); pnlNombre.setOpaque(false);
        pnlNombre.add(crearLabel("NOMBRE COMPLETO"), BorderLayout.NORTH);
        txtNombre = crearTextField();
        pnlNombre.add(txtNombre, BorderLayout.CENTER);

        pnlRow1.add(pnlNombre, BorderLayout.CENTER);

        // Row 2
        JPanel pnlRow2 = new JPanel(new BorderLayout());
        pnlRow2.setOpaque(false);
        pnlRow2.add(crearLabel("HISTORIAL DE VEHICULOS"), BorderLayout.NORTH);
        cmbHistorial = new JComboBox<>(new String[]{" "});
        cmbHistorial.setBackground(BG_FIELD);
        cmbHistorial.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbHistorial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1, true),
                new EmptyBorder(2, 2, 2, 2)
        ));
        pnlRow2.add(cmbHistorial, BorderLayout.CENTER);

        // Row 3
        JPanel pnlRow3 = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlRow3.setOpaque(false);
        JPanel pnlCorreo = new JPanel(new BorderLayout()); pnlCorreo.setOpaque(false);
        pnlCorreo.add(crearLabel("CORREO"), BorderLayout.NORTH);
        txtCorreo = crearTextField();
        pnlCorreo.add(txtCorreo, BorderLayout.CENTER);

        JPanel pnlTel = new JPanel(new BorderLayout()); pnlTel.setOpaque(false);
        pnlTel.add(crearLabel("TÉLEFONO"), BorderLayout.NORTH);
        txtTelefono = crearTextField();
        pnlTel.add(txtTelefono, BorderLayout.CENTER);

        pnlRow3.add(pnlCorreo);
        pnlRow3.add(pnlTel);

        mainPanel.add(pnlRow1);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(pnlRow2);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(pnlRow3);

        if (clienteAEditar != null) {
            txtNombre.setText(cliente.getNombreCompleto());
            txtTelefono.setText(cliente.getTelefono());
            txtCorreo.setText(cliente.getCorreo());
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JButton btnCancelar = new JButton("X  CANCELAR") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#9DB2BF"));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 35, 35));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Inter", Font.BOLD, 13));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(160, 40));
        btnCancelar.addActionListener(e -> dispose());

        String btnText = clienteAEditar == null ? "  AGREGAR CLIENTE" : "  GUARDAR CAMBIOS";
        JButton btnGuardar = new JButton(btnText) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GOLD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 35, 35));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setForeground(TEAL);
        btnGuardar.setFont(new Font("Inter", Font.BOLD, 13));
        // Intenta cargar icono si es posible, o dejalo sin icono si no hay gestor disponible aca
        try {
            btnGuardar.setIcon(views.IconoManager.cargarIcono("agregar.png", 18, 18)); 
        } catch(Exception ex){}
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(210, 40));
        btnGuardar.addActionListener(e -> guardar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);

        bgPanel.add(mainPanel, BorderLayout.CENTER);
        bgPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bgPanel);
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(GOLD);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JTextField crearTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Inter", Font.PLAIN, 13));
        textField.setBackground(BG_FIELD);
        textField.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createLineBorder(TEAL, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
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

    public boolean isGuardado() { return guardado; }
    public ClienteModelo getCliente() { return cliente; }
}
