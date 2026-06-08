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

import java.awt.Rectangle;

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
import javax.swing.Scrollable;
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
    private JComboBox<String> cmbClientes;
    private JComboBox<models.VehiculoModelo> cmbVehiculos;

    private ButtonGroup grupoFalla;       // Radio buttons: Reparación, Mantención, Garantía
    private JRadioButton rbReparacion;
    private JRadioButton rbMantencion;
    private JRadioButton rbGarantia;

    private JTextArea txtDescripcionFalla;
    private PanelListaServicios panelListaServicios;

    private JTextField txtKilometraje;
    private JComboBox<String> cmbCombustible;
    private JTextArea txtCondicionVehiculo;
    private JTextField txtFechaIngreso;
    private JTextField txtFechaEntrega;

    private JTextField txtCostoRefacciones; // Auto-calculado del PanelListaServicios
    private JTextField txtCostoServicios;   // Campo manual para mano de obra
    private JTextField txtImpuesto;
    private JTextField txtTotal;

    private JComboBox<String> cmbEstado;  // LISTO, EN REPARACIÓN, EN ESPERA

    private JButton btnCrearOrden;
    private JButton btnSubirImagen;
    private JLabel lblImgNombre;

    // Constructor
    public CrearOrdenVista() {
        setBackground(BG_LIGHT);
        setLayout(new BorderLayout());

        // Cabecera
        add(createHeader(), BorderLayout.NORTH);

     // Cuerpo del formulario con Scrollable para ser responsivo
        class ScrollablePanel extends JPanel implements Scrollable {
            public ScrollablePanel() { super(new BorderLayout(15, 15)); }
            @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
            @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
            @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
            @Override public boolean getScrollableTracksViewportWidth() { return true; }
            @Override public boolean getScrollableTracksViewportHeight() { 
                if (getParent() instanceof javax.swing.JViewport) {
                    return ((javax.swing.JViewport)getParent()).getHeight() > getPreferredSize().height;
                }
                return false;
            }
        }
        ScrollablePanel body = new ScrollablePanel();
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(5, 20, 5, 20));

        // Panel central: lado izquierdo y lado derecho
        JPanel formContent = new JPanel(new GridLayout(1, 2, 15, 0));
        formContent.setOpaque(false);

        formContent.add(createLeftPanel());   // Datos de falla y costos
        formContent.add(createRightPanel());  // Datos del vehículo

        body.add(formContent, BorderLayout.CENTER);
        
        // Hacer el panel responsive
        formContent.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                if (formContent.getWidth() < 650) {
                    formContent.setLayout(new GridLayout(2, 1, 0, 15));
                } else {
                    formContent.setLayout(new GridLayout(1, 2, 15, 0));
                }
                formContent.revalidate();
            }
        });
        
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
        add(btnPanel, BorderLayout.SOUTH); // Botones fijos abajo

        // Scroll por si la ventana es pequeña
        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        cmbEstado.setFont(new Font("Inter", Font.BOLD, 11));
        cmbEstado.setBackground(Color.WHITE);

        Color colorListo = new Color(40, 167, 69);      // Verde
        Color colorReparacion = new Color(220, 53, 69); // Rojo
        Color colorEspera = new Color(242, 156, 31);    // Amarillo/Dorado

        cmbEstado.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null && !isSelected) {
                    String status = value.toString();
                    if (status.equals("LISTO")) c.setForeground(colorListo);
                    else if (status.equals("EN REPARACIÓN")) c.setForeground(colorReparacion);
                    else if (status.equals("EN ESPERA")) c.setForeground(colorEspera);
                }
                return c;
            }
        });

        cmbEstado.addActionListener(e -> {
            String status = (String) cmbEstado.getSelectedItem();
            if ("LISTO".equals(status)) cmbEstado.setForeground(colorListo);
            else if ("EN REPARACIÓN".equals(status)) cmbEstado.setForeground(colorReparacion);
            else if ("EN ESPERA".equals(status)) cmbEstado.setForeground(colorEspera);
        });

        cmbEstado.setSelectedIndex(2); // "EN ESPERA" por defecto

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
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);

        // --- Nombre del cliente ---
        JPanel clientePanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        clientePanel.setLayout(new BoxLayout(clientePanel, BoxLayout.X_AXIS));
        clientePanel.setOpaque(false);
        clientePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconCliente = new JLabel(IconoManager.cargarIcono("clientes.png", 30, 30));
        cmbClientes = new JComboBox<>();
        cmbClientes.setEditable(true);
        cmbClientes.setFont(new Font("Inter", Font.BOLD, 16));
        cmbClientes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cmbClientes.setOpaque(false);
        
        cmbClientes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        clientePanel.add(iconCliente);
        clientePanel.add(Box.createHorizontalStrut(10));
        clientePanel.add(cmbClientes);
        panel.add(clientePanel);
        panel.add(Box.createVerticalStrut(18));

        // --- Tipo de falla (Radio buttons) ---
        JLabel lblFalla = new JLabel("FALLA O REQUERIMIENTO");
        lblFalla.setFont(new Font("Inter", Font.BOLD, 13));
        lblFalla.setForeground(GOLD);
        lblFalla.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblFalla);
        panel.add(Box.createVerticalStrut(2));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbReparacion = new JRadioButton("Reparación");
        rbMantencion = new JRadioButton("Mantenimiento"); // Cambiado texto a Mantenimiento
        rbGarantia = new JRadioButton("Garantía");

        grupoFalla = new ButtonGroup();
        grupoFalla.add(rbReparacion);
        grupoFalla.add(rbMantencion);
        grupoFalla.add(rbGarantia);

        rbReparacion.setOpaque(false); rbReparacion.setFont(new Font("Inter", Font.PLAIN, 13));
        rbMantencion.setOpaque(false); rbMantencion.setFont(new Font("Inter", Font.PLAIN, 13));
        rbGarantia.setOpaque(false); rbGarantia.setFont(new Font("Inter", Font.PLAIN, 13));
        
        rbReparacion.setSelected(true);

        radioPanel.add(rbReparacion);
        radioPanel.add(rbMantencion);
        radioPanel.add(rbGarantia);
        panel.add(radioPanel);
        panel.add(Box.createVerticalStrut(5));

        // --- Descripción de la falla ---
        txtDescripcionFalla = new JTextArea(2, 20);
        txtDescripcionFalla.setLineWrap(true);
        txtDescripcionFalla.setWrapStyleWord(true);
        txtDescripcionFalla.setFont(new Font("Inter", Font.PLAIN, 13));
        txtDescripcionFalla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        txtDescripcionFalla.setToolTipText("Agregar falla o requerimientos...");
        JScrollPane scrollFalla = new JScrollPane(txtDescripcionFalla);
        scrollFalla.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollFalla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(scrollFalla);
        panel.add(Box.createVerticalStrut(10));

        // --- Refacciones (lista interactiva) ---
        JLabel lblRefacciones = new JLabel("REFACCIONES");
        lblRefacciones.setFont(new Font("Inter", Font.BOLD, 13));
        lblRefacciones.setForeground(GOLD);
        lblRefacciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblRefacciones);
        panel.add(Box.createVerticalStrut(2));

        panelListaServicios = new PanelListaServicios();
        panelListaServicios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelListaServicios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        panelListaServicios.setPreferredSize(new Dimension(0, 140));
        panel.add(panelListaServicios);
        panel.add(Box.createVerticalStrut(10));

        // --- Costos (Refacciones, Servicios M.O., Impuesto, Total) ---
        // Fila 1: Refacciones (auto) + Servicios M.O. (manual)
        JPanel costosRow1 = new JPanel(new GridLayout(1, 2, 15, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        costosRow1.setOpaque(false);
        costosRow1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Refacciones (auto-calculado del PanelListaServicios)
        JPanel refPanel = new JPanel(new GridLayout(2, 1));
        refPanel.setOpaque(false);
        JLabel lRef = new JLabel("REFACCIONES:"); lRef.setFont(new Font("Inter", Font.PLAIN, 10)); lRef.setForeground(GOLD);
        txtCostoRefacciones = new JTextField(5);
        txtCostoRefacciones.setFont(new Font("Inter", Font.PLAIN, 13));
        txtCostoRefacciones.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        txtCostoRefacciones.setOpaque(false);
        txtCostoRefacciones.setEditable(false); // Auto-calculado
        refPanel.add(lRef); refPanel.add(txtCostoRefacciones);
        costosRow1.add(refPanel);

        // Servicios / Mano de Obra (campo manual)
        JPanel srvPanel = new JPanel(new GridLayout(2, 1));
        srvPanel.setOpaque(false);
        JLabel lSrv = new JLabel("SERVICIOS (M.O.):"); lSrv.setFont(new Font("Inter", Font.PLAIN, 10)); lSrv.setForeground(GOLD);
        txtCostoServicios = new JTextField(5);
        txtCostoServicios.setFont(new Font("Inter", Font.PLAIN, 13));
        txtCostoServicios.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        txtCostoServicios.setOpaque(false);
        srvPanel.add(lSrv); srvPanel.add(txtCostoServicios);
        costosRow1.add(srvPanel);

        panel.add(costosRow1);
        panel.add(Box.createVerticalStrut(5));

        // Fila 2: Impuesto + Total
        JPanel costosRow2 = new JPanel(new GridLayout(1, 2, 15, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        costosRow2.setOpaque(false);
        costosRow2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Impuesto
        JPanel impPanel = new JPanel(new GridLayout(2, 1));
        impPanel.setOpaque(false);
        JLabel lImp = new JLabel("IMPUESTO:"); lImp.setFont(new Font("Inter", Font.PLAIN, 10)); lImp.setForeground(GOLD);
        txtImpuesto = new JTextField(5);
        txtImpuesto.setFont(new Font("Inter", Font.PLAIN, 13));
        txtImpuesto.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        txtImpuesto.setOpaque(false);
        impPanel.add(lImp); impPanel.add(txtImpuesto);
        costosRow2.add(impPanel);

        // Total
        JPanel totPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                g2.setColor(Color.decode("#005064"));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 25, 25));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        totPanel.setOpaque(false);
        totPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
        
        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(new Font("Inter", Font.BOLD, 13));
        lblTotal.setForeground(GOLD);
        
        txtTotal = new JTextField(6);
        txtTotal.setFont(new Font("Inter", Font.BOLD, 13));
        txtTotal.setForeground(Color.decode("#005064"));
        txtTotal.setEditable(false);
        txtTotal.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        txtTotal.setOpaque(false);
        
        totPanel.add(lblTotal);
        totPanel.add(txtTotal);
        
        costosRow2.add(totPanel);

        panel.add(costosRow2);

        return panel;
    }

    // ==================== PANEL DERECHO ====================
    // Contiene: Datos del vehículo, kilometraje, combustible, condición, imágenes

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // --- Datos del vehículo ---
        JPanel vehiculoCard = new JPanel(new BorderLayout(15, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        vehiculoCard.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        vehiculoCard.setBackground(Color.WHITE);
        vehiculoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Icono de selector (Círculo naranja con flecha azul) ---
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#F29C1F")); // Naranja
                g2.fillOval(2, 2, 40, 40);
                g2.setColor(Color.decode("#00A2ED")); // Borde celeste
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.drawOval(2, 2, 40, 40);
                
                // Flecha
                g2.setColor(Color.decode("#00314A")); // Azul oscuro
                int[] xPoints = {15, 29, 22};
                int[] yPoints = {16, 16, 26};
                g2.fillPolygon(xPoints, yPoints, 3);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(46, 46));
        iconPanel.setOpaque(false);
        iconPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- Selector de Vehículo ---
        cmbVehiculos = new JComboBox<>();
        cmbVehiculos.setFont(new Font("Inter", Font.PLAIN, 15));
        cmbVehiculos.setBackground(Color.WHITE);
        cmbVehiculos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        iconPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cmbVehiculos.requestFocusInWindow();
                cmbVehiculos.setPopupVisible(!cmbVehiculos.isPopupVisible());
            }
        });
        
        vehiculoCard.add(iconPanel, BorderLayout.WEST);
        vehiculoCard.add(cmbVehiculos, BorderLayout.CENTER);

        panel.add(vehiculoCard);
        panel.add(Box.createVerticalStrut(10));

        // --- Kilometraje y Nivel de combustible ---
        JPanel kmCombPanel = new JPanel(new GridLayout(1, 2, 20, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        kmCombPanel.setOpaque(false);
        kmCombPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kilometraje
        JPanel kmPanel = new JPanel(new BorderLayout(0, 5));
        kmPanel.setOpaque(false);
        JLabel lblKm = new JLabel("Kilometraje");
        lblKm.setFont(new Font("Inter", Font.ITALIC, 13));
        lblKm.setForeground(GOLD);
        txtKilometraje = new JTextField();
        txtKilometraje.setFont(new Font("Inter", Font.BOLD, 13));
        txtKilometraje.setHorizontalAlignment(SwingConstants.CENTER);
        txtKilometraje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        kmPanel.add(lblKm, BorderLayout.NORTH);
        kmPanel.add(txtKilometraje, BorderLayout.CENTER);

        // Nivel de combustible
        JPanel combPanel = new JPanel(new BorderLayout(0, 5));
        combPanel.setOpaque(false);
        JLabel lblComb = new JLabel("Nivel de combustible");
        lblComb.setFont(new Font("Inter", Font.ITALIC, 13));
        lblComb.setForeground(GOLD);
        cmbCombustible = new JComboBox<>(new String[]{"E", "1/4", "1/2", "3/4", "F"});
        cmbCombustible.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbCombustible.setBackground(Color.WHITE);
        cmbCombustible.setBorder(BorderFactory.createLineBorder(TEAL, 2, true));
        combPanel.add(lblComb, BorderLayout.NORTH);
        combPanel.add(cmbCombustible, BorderLayout.CENTER);

        kmCombPanel.add(kmPanel);
        kmCombPanel.add(combPanel);
        panel.add(kmCombPanel);
        panel.add(Box.createVerticalStrut(10));

        // --- Fechas ---
        JPanel fechasPanel = new JPanel(new GridLayout(1, 2, 20, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
            }
        };
        fechasPanel.setOpaque(false);
        fechasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fecha Ingreso
        JPanel ingresoPanel = new JPanel(new BorderLayout(0, 5));
        ingresoPanel.setOpaque(false);
        JLabel lblFIngreso = new JLabel("Fecha Ingreso (yyyy-mm-dd)");
        lblFIngreso.setFont(new Font("Inter", Font.ITALIC, 11));
        lblFIngreso.setForeground(GOLD);
        txtFechaIngreso = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtFechaIngreso.setFont(new Font("Inter", Font.BOLD, 13));
        txtFechaIngreso.setHorizontalAlignment(SwingConstants.CENTER);
        txtFechaIngreso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        ingresoPanel.add(lblFIngreso, BorderLayout.NORTH);
        ingresoPanel.add(txtFechaIngreso, BorderLayout.CENTER);

        // Fecha Entrega
        JPanel entregaPanel = new JPanel(new BorderLayout(0, 5));
        entregaPanel.setOpaque(false);
        JLabel lblFEntrega = new JLabel("Est. Entrega (yyyy-mm-dd)");
        lblFEntrega.setFont(new Font("Inter", Font.ITALIC, 11));
        lblFEntrega.setForeground(GOLD);
        txtFechaEntrega = new JTextField(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtFechaEntrega.setFont(new Font("Inter", Font.BOLD, 13));
        txtFechaEntrega.setHorizontalAlignment(SwingConstants.CENTER);
        txtFechaEntrega.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        entregaPanel.add(lblFEntrega, BorderLayout.NORTH);
        entregaPanel.add(txtFechaEntrega, BorderLayout.CENTER);

        fechasPanel.add(ingresoPanel);
        fechasPanel.add(entregaPanel);
        panel.add(fechasPanel);
        panel.add(Box.createVerticalStrut(10));

        // --- Condición del vehículo ---
        JLabel lblCondicion = new JLabel("Condición del vehículo");
        lblCondicion.setFont(new Font("Inter", Font.ITALIC, 13));
        lblCondicion.setForeground(GOLD);
        lblCondicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblCondicion);
        panel.add(Box.createVerticalStrut(2));

        txtCondicionVehiculo = new JTextArea(3, 20);
        txtCondicionVehiculo.setLineWrap(true);
        txtCondicionVehiculo.setWrapStyleWord(true);
        txtCondicionVehiculo.setFont(new Font("Inter", Font.PLAIN, 13));
        txtCondicionVehiculo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollCondicion = new JScrollPane(txtCondicionVehiculo);
        scrollCondicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollCondicion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.add(scrollCondicion);
        panel.add(Box.createVerticalStrut(10));

        // --- Agregar imágenes  ---
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imgPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        imgPanel.setBorder(BorderFactory.createLineBorder(TEAL, 2, true));
        imgPanel.setBackground(Color.decode("#D0E0E8"));

        btnSubirImagen = new JButton("Seleccionar Imagen");
        btnSubirImagen.setFont(new Font("Inter", Font.BOLD, 13));
        btnSubirImagen.setForeground(HEADER_BG);
        btnSubirImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubirImagen.setFocusPainted(false);
        
        lblImgNombre = new JLabel("Ninguna imagen seleccionada", SwingConstants.CENTER);
        lblImgNombre.setFont(new Font("Inter", Font.PLAIN, 11));
        lblImgNombre.setForeground(TEAL);

        JLabel lblImg = new JLabel("Agregar imágenes", SwingConstants.CENTER);
        lblImg.setFont(new Font("Inter", Font.ITALIC, 13));
        lblImg.setForeground(GOLD);
        lblImg.setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnSubirImagen);

        imgPanel.add(lblImg, BorderLayout.NORTH);
        imgPanel.add(btnWrapper, BorderLayout.CENTER);
        imgPanel.add(lblImgNombre, BorderLayout.SOUTH);

        panel.add(imgPanel);

        return panel;
    }

    // ==================== GETTERS para el Controlador ====================

    public JButton getBtnCrearOrden() { return btnCrearOrden; }
    public JComboBox<String> getCmbClientes() { return cmbClientes; }
    public JButton getBtnSubirImagen() { return btnSubirImagen; }
    public JLabel getLblImgNombre() { return lblImgNombre; }
    public JComboBox<models.VehiculoModelo> getCmbVehiculos() { return cmbVehiculos; }
    public JTextArea getTxtDescripcionFalla() { return txtDescripcionFalla; }
    public PanelListaServicios getPanelListaServicios() { return panelListaServicios; }
    public JTextField getTxtKilometraje() { return txtKilometraje; }
    public JComboBox<String> getCmbCombustible() { return cmbCombustible; }
    public JTextArea getTxtCondicionVehiculo() { return txtCondicionVehiculo; }
    public JTextField getTxtCostoRefacciones() { return txtCostoRefacciones; }
    public JTextField getTxtCostoServicios() { return txtCostoServicios; }
    public JTextField getTxtImpuesto() { return txtImpuesto; }
    public JTextField getTxtTotal() { return txtTotal; }
    
    public JTextField getTxtFechaIngreso() { return txtFechaIngreso; }
    public JTextField getTxtFechaEntrega() { return txtFechaEntrega; }
    
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
        if (cmbClientes.getItemCount() > 0) {
            cmbClientes.setSelectedIndex(0);
        }
        if (cmbVehiculos.getItemCount() > 0) {
            cmbVehiculos.setSelectedIndex(0);
        }
        txtDescripcionFalla.setText("");
        panelListaServicios.limpiar();
        txtKilometraje.setText("");
        txtCondicionVehiculo.setText("");
        txtCostoRefacciones.setText("");
        txtCostoServicios.setText("");
        txtImpuesto.setText("");
        txtTotal.setText("");
        grupoFalla.clearSelection();
        cmbEstado.setSelectedIndex(2);
        cmbCombustible.setSelectedIndex(0);
        lblImgNombre.setText("Ninguna imagen seleccionada");
    }
}
