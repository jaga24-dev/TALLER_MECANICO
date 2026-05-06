package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Vista de la sección "Crear orden" del menú lateral.
 * Es un FORMULARIO para crear una nueva orden de trabajo.
 * 
 * ESTRUCTURA (según la imagen):
 * - Cabecera: "ORDEN DE TRABAJO #1" + Estado (LISTO / EN REPARACIÓN / EN ESPERA)
 * - Panel Izquierdo: Nombre cliente, Tipo de falla, Descripción, Servicio/Producto, Subtotal/Impuesto/Total
 * - Panel Derecho: Datos del vehículo, Kilometraje, Nivel combustible, Condición, Agregar imágenes
 * - Botón "Crear Orden" al final
 */
public class CrearOrdenVista extends JPanel {

    // --- Colores ---
    private static final Color BG_LIGHT = Color.decode("#EBF0F5");
    private static final Color HEADER_BG = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color TEAL = Color.decode("#005064");

    // --- Campos del formulario ---
    private JTextField txtNombreCliente;
    private JTextField txtNombreVehiculo;
    private JTextField txtPlaca;
    private JTextField txtNumSerie;
    private JTextField txtAnio;
    private JTextField txtColor;

    private ButtonGroup grupoFalla;       // Radio buttons: Reparación, Mantención, Garantía
    private JRadioButton rbReparacion;
    private JRadioButton rbMantencion;
    private JRadioButton rbGarantia;

    private JTextArea txtDescripcionFalla;
    private JTextArea txtServicioProducto;

    private JTextField txtKilometraje;
    private JComboBox<String> cmbCombustible;
    private JTextArea txtCondicionVehiculo;

    private JTextField txtSubtotal;
    private JTextField txtImpuesto;
    private JTextField txtTotal;

    private JComboBox<String> cmbEstado;  // LISTO, EN REPARACIÓN, EN ESPERA

    private JButton btnCrearOrden;

    // Constructor
    public CrearOrdenVista() {
        setBackground(BG_LIGHT);
        setLayout(new BorderLayout());

        // Cabecera
        add(createHeader(), BorderLayout.NORTH);

        // Cuerpo del formulario
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(15, 20, 20, 20));

        // Panel central: lado izquierdo y lado derecho
        JPanel formContent = new JPanel(new GridLayout(1, 2, 15, 0));
        formContent.setOpaque(false);

        formContent.add(createLeftPanel());   // Datos de falla y costos
        formContent.add(createRightPanel());  // Datos del vehículo

        body.add(formContent, BorderLayout.CENTER);

        // Botón "Crear Orden" en la parte inferior
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnCrearOrden = new JButton("   Crear Orden   ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GOLD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnCrearOrden.setContentAreaFilled(false);
        btnCrearOrden.setBorderPainted(false);
        btnCrearOrden.setFocusPainted(false);
        btnCrearOrden.setFont(new Font("Inter", Font.BOLD, 14));
        btnCrearOrden.setForeground(HEADER_BG);
        btnCrearOrden.setIcon(IconoManager.cargarIcono("agregar.png", 18, 18));
        btnCrearOrden.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCrearOrden.setPreferredSize(new Dimension(250, 45));

        btnPanel.add(btnCrearOrden);
        body.add(btnPanel, BorderLayout.SOUTH);

        // Scroll por si la ventana es pequeña
        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ==================== CABECERA ====================

    private JPanel createHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_BG, getWidth(), 0, Color.decode("#004664"));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(10, 5, getWidth() - 20, getHeight() - 10, 16, 16));
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 60));
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 25, 5, 25));

        // Lado izquierdo: título
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ORDEN DE TRABAJO #1");
        title.setFont(new Font("Inter", Font.BOLD, 13));
        title.setForeground(GOLD);

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMM yyyy", new Locale("es", "MX")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        JLabel dateLabel = new JLabel(fecha);
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        dateLabel.setForeground(GOLD);

        leftPanel.add(title);
        leftPanel.add(dateLabel);

        // Lado derecho: selector de estado
        cmbEstado = new JComboBox<>(new String[]{"LISTO", "EN REPARACIÓN", "EN ESPERA"});
        cmbEstado.setSelectedIndex(2); // "EN ESPERA" por defecto
        cmbEstado.setBackground(GOLD);
        cmbEstado.setForeground(HEADER_BG);
        cmbEstado.setFont(new Font("Inter", Font.BOLD, 11));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(cmbEstado);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ==================== PANEL IZQUIERDO ====================
    // Contiene: Nombre cliente, tipo de falla, descripción, servicio, costos

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // --- Nombre del cliente ---
        JPanel clientePanel = new JPanel(new BorderLayout(5, 0));
        clientePanel.setOpaque(false);
        clientePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel iconCliente = new JLabel(IconoManager.cargarIcono("clientes.png", 20, 20));
        txtNombreCliente = new JTextField();
        txtNombreCliente.setFont(new Font("Inter", Font.BOLD, 13));
        txtNombreCliente.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        txtNombreCliente.setToolTipText("Nombre de cliente...");

        clientePanel.add(iconCliente, BorderLayout.WEST);
        clientePanel.add(txtNombreCliente, BorderLayout.CENTER);
        panel.add(clientePanel);
        panel.add(Box.createVerticalStrut(12));

        // --- Tipo de falla (Radio buttons) ---
        JLabel lblFalla = new JLabel("FALLA O REQUERIMIENTO");
        lblFalla.setFont(new Font("Inter", Font.BOLD, 12));
        lblFalla.setForeground(GOLD);
        lblFalla.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblFalla);
        panel.add(Box.createVerticalStrut(5));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        radioPanel.setOpaque(false);
        radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbReparacion = new JRadioButton("Reparación");
        rbMantencion = new JRadioButton("Mantención");
        rbGarantia = new JRadioButton("Garantía");

        grupoFalla = new ButtonGroup();
        grupoFalla.add(rbReparacion);
        grupoFalla.add(rbMantencion);
        grupoFalla.add(rbGarantia);

        rbReparacion.setOpaque(false);
        rbMantencion.setOpaque(false);
        rbGarantia.setOpaque(false);

        radioPanel.add(rbReparacion);
        radioPanel.add(rbMantencion);
        radioPanel.add(rbGarantia);
        panel.add(radioPanel);
        panel.add(Box.createVerticalStrut(8));

        // --- Descripción de la falla ---
        txtDescripcionFalla = new JTextArea(3, 20);
        txtDescripcionFalla.setLineWrap(true);
        txtDescripcionFalla.setWrapStyleWord(true);
        txtDescripcionFalla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        txtDescripcionFalla.setBackground(Color.decode("#D0E0E8"));
        txtDescripcionFalla.setToolTipText("Agregar falla o requerimientos...");
        JScrollPane scrollFalla = new JScrollPane(txtDescripcionFalla);
        scrollFalla.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollFalla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.add(scrollFalla);
        panel.add(Box.createVerticalStrut(12));

        // --- Servicio o Producto ---
        JLabel lblServicio = new JLabel("SERVICIO O PRODUCTO");
        lblServicio.setFont(new Font("Inter", Font.BOLD, 12));
        lblServicio.setForeground(GOLD);
        lblServicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblServicio);
        panel.add(Box.createVerticalStrut(5));

        txtServicioProducto = new JTextArea(4, 20);
        txtServicioProducto.setLineWrap(true);
        txtServicioProducto.setWrapStyleWord(true);
        txtServicioProducto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        txtServicioProducto.setBackground(Color.decode("#D0E0E8"));
        txtServicioProducto.setToolTipText("Agregar nuevo...");
        JScrollPane scrollServicio = new JScrollPane(txtServicioProducto);
        scrollServicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollServicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.add(scrollServicio);
        panel.add(Box.createVerticalStrut(12));

        // --- Costos (Subtotal, Impuesto, Total) ---
        JPanel costosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        costosPanel.setOpaque(false);
        costosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        costosPanel.setBorder(BorderFactory.createLineBorder(TEAL, 1, true));
        costosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        costosPanel.add(new JLabel("SUBTOTAL:"));
        txtSubtotal = new JTextField(5);
        costosPanel.add(txtSubtotal);

        costosPanel.add(new JLabel("IMPUESTO:"));
        txtImpuesto = new JTextField(5);
        costosPanel.add(txtImpuesto);

        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(new Font("Inter", Font.BOLD, 12));
        costosPanel.add(lblTotal);
        txtTotal = new JTextField(6);
        txtTotal.setEditable(false); // El total se calcula automáticamente
        txtTotal.setBackground(Color.WHITE);
        costosPanel.add(txtTotal);

        panel.add(costosPanel);

        return panel;
    }

    // ==================== PANEL DERECHO ====================
    // Contiene: Datos del vehículo, kilometraje, combustible, condición, imágenes

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // --- Datos del vehículo ---
        JPanel vehiculoCard = new JPanel();
        vehiculoCard.setLayout(new BoxLayout(vehiculoCard, BoxLayout.Y_AXIS));
        vehiculoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        vehiculoCard.setBackground(Color.WHITE);
        vehiculoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        vehiculoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel vehiculoRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        vehiculoRow1.setOpaque(false);
        JLabel iconV = new JLabel(IconoManager.cargarIcono("vehiculos.png", 20, 20));
        txtNombreVehiculo = new JTextField(15);
        txtNombreVehiculo.setToolTipText("Nombre de vehículo...");
        vehiculoRow1.add(iconV);
        vehiculoRow1.add(txtNombreVehiculo);

        JPanel vehiculoRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        vehiculoRow2.setOpaque(false);
        txtPlaca = new JTextField(8);
        txtPlaca.setToolTipText("Placa");
        txtNumSerie = new JTextField(10);
        txtNumSerie.setToolTipText("Número de serie (VIN)");
        vehiculoRow2.add(new JLabel("Placa:")); vehiculoRow2.add(txtPlaca);
        vehiculoRow2.add(new JLabel("  N° Serie:")); vehiculoRow2.add(txtNumSerie);

        JPanel vehiculoRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        vehiculoRow3.setOpaque(false);
        txtAnio = new JTextField(5);
        txtAnio.setToolTipText("Año");
        txtColor = new JTextField(8);
        txtColor.setToolTipText("Color");
        vehiculoRow3.add(new JLabel("Año:")); vehiculoRow3.add(txtAnio);
        vehiculoRow3.add(new JLabel("  Color:")); vehiculoRow3.add(txtColor);

        vehiculoCard.add(vehiculoRow1);
        vehiculoCard.add(vehiculoRow2);
        vehiculoCard.add(vehiculoRow3);

        panel.add(vehiculoCard);
        panel.add(Box.createVerticalStrut(10));

        // --- Kilometraje y Nivel de combustible ---
        JPanel kmCombPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        kmCombPanel.setOpaque(false);
        kmCombPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        kmCombPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Kilometraje
        JPanel kmPanel = new JPanel(new BorderLayout(5, 2));
        kmPanel.setOpaque(false);
        JLabel lblKm = new JLabel("Kilometraje");
        lblKm.setFont(new Font("Inter", Font.ITALIC, 11));
        lblKm.setForeground(GOLD);
        txtKilometraje = new JTextField();
        txtKilometraje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        kmPanel.add(lblKm, BorderLayout.NORTH);
        kmPanel.add(txtKilometraje, BorderLayout.CENTER);

        // Nivel de combustible
        JPanel combPanel = new JPanel(new BorderLayout(5, 2));
        combPanel.setOpaque(false);
        JLabel lblComb = new JLabel("Nivel de combustible");
        lblComb.setFont(new Font("Inter", Font.ITALIC, 11));
        lblComb.setForeground(GOLD);
        cmbCombustible = new JComboBox<>(new String[]{"E", "1/4", "1/2", "3/4", "F"});
        combPanel.add(lblComb, BorderLayout.NORTH);
        combPanel.add(cmbCombustible, BorderLayout.CENTER);

        kmCombPanel.add(kmPanel);
        kmCombPanel.add(combPanel);
        panel.add(kmCombPanel);
        panel.add(Box.createVerticalStrut(10));

        // --- Condición del vehículo ---
        JLabel lblCondicion = new JLabel("Condición del vehículo");
        lblCondicion.setFont(new Font("Inter", Font.ITALIC, 11));
        lblCondicion.setForeground(GOLD);
        lblCondicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblCondicion);
        panel.add(Box.createVerticalStrut(3));

        txtCondicionVehiculo = new JTextArea(2, 20);
        txtCondicionVehiculo.setLineWrap(true);
        txtCondicionVehiculo.setWrapStyleWord(true);
        txtCondicionVehiculo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollCondicion = new JScrollPane(txtCondicionVehiculo);
        scrollCondicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollCondicion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.add(scrollCondicion);
        panel.add(Box.createVerticalStrut(10));

        // --- Agregar imágenes (simulación) ---
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imgPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        imgPanel.setBorder(BorderFactory.createLineBorder(TEAL, 1, true));
        imgPanel.setBackground(Color.decode("#D0E0E8"));

        JLabel lblImg = new JLabel("Agregar imágenes", SwingConstants.CENTER);
        lblImg.setFont(new Font("Inter", Font.ITALIC, 12));
        lblImg.setForeground(GOLD);

        JLabel iconPlus = new JLabel(IconoManager.cargarIcono("agregar.png", 40, 40), SwingConstants.CENTER);

        imgPanel.add(lblImg, BorderLayout.NORTH);
        imgPanel.add(iconPlus, BorderLayout.CENTER);

        panel.add(imgPanel);

        return panel;
    }

    // ==================== GETTERS para el Controlador ====================

    public JButton getBtnCrearOrden() { return btnCrearOrden; }
    public JTextField getTxtNombreCliente() { return txtNombreCliente; }
    public JTextField getTxtNombreVehiculo() { return txtNombreVehiculo; }
    public JTextField getTxtPlaca() { return txtPlaca; }
    public JTextField getTxtNumSerie() { return txtNumSerie; }
    public JTextField getTxtAnio() { return txtAnio; }
    public JTextField getTxtColor() { return txtColor; }
    public JTextArea getTxtDescripcionFalla() { return txtDescripcionFalla; }
    public JTextArea getTxtServicioProducto() { return txtServicioProducto; }
    public JTextField getTxtKilometraje() { return txtKilometraje; }
    public JComboBox<String> getCmbCombustible() { return cmbCombustible; }
    public JTextArea getTxtCondicionVehiculo() { return txtCondicionVehiculo; }
    public JTextField getTxtSubtotal() { return txtSubtotal; }
    public JTextField getTxtImpuesto() { return txtImpuesto; }
    public JTextField getTxtTotal() { return txtTotal; }
    public JComboBox<String> getCmbEstado() { return cmbEstado; }

    /**
     * Devuelve qué tipo de falla fue seleccionado ("Reparación", "Mantención" o "Garantía").
     * Retorna null si no se seleccionó ninguno.
     */
    public String getTipoFallaSeleccionado() {
        if (rbReparacion.isSelected()) return "Reparación";
        if (rbMantencion.isSelected()) return "Mantención";
        if (rbGarantia.isSelected()) return "Garantía";
        return null;
    }

    /**
     * Limpia todos los campos del formulario para crear una nueva orden.
     */
    public void limpiarFormulario() {
        txtNombreCliente.setText("");
        txtNombreVehiculo.setText("");
        txtPlaca.setText("");
        txtNumSerie.setText("");
        txtAnio.setText("");
        txtColor.setText("");
        txtDescripcionFalla.setText("");
        txtServicioProducto.setText("");
        txtKilometraje.setText("");
        txtCondicionVehiculo.setText("");
        txtSubtotal.setText("");
        txtImpuesto.setText("");
        txtTotal.setText("");
        grupoFalla.clearSelection();
        cmbEstado.setSelectedIndex(2);
        cmbCombustible.setSelectedIndex(0);
    }
}
