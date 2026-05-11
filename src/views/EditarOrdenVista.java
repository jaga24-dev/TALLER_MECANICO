package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import models.OrdenServicioModelo;

/**
 * Vista para Editar una Orden de Servicio (Diseño de la Imagen 2).
 */
public class EditarOrdenVista extends JPanel {

    // --- Colores ---
    private static final Color BG_LIGHT = Color.decode("#EBF0F5");
    private static final Color HEADER_BG = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color TEAL = Color.decode("#005064");

    // --- Campos de interfaz ---
    private JLabel lblNombreCliente;
    private JLabel lblVehiculoDesc;
    
    private ButtonGroup grupoFalla;
    private JRadioButton rbReparacion;
    private JRadioButton rbMantenimiento;
    private JRadioButton rbGarantia;

    private JTextArea txtDescripcionFalla;
    private JTextArea txtServicioProducto; // Opcionalmente una lista, pero usaremos un TextArea para simplificar o un panel personalizado.
    
    // Panel de servicios (simulado como en la imagen)
    private JPanel panelServicios;

    private JLabel lblSubtotalVal;
    private JLabel lblImpuestoVal;
    private JLabel lblTotalVal;

    private JTextField txtKilometraje;
    private JProgressBar barraCombustible;
    private JTextArea txtCondicionVehiculo;

    private JComboBox<String> cmbEstado;

    private JButton btnConfirmar;
    private JButton btnCancelar;

    // Constructor
    public EditarOrdenVista() {
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

        // Botones "Cancelar" y "Confirmar Cambios" en la parte inferior
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnCancelar = new JButton("X  CANCELAR") {
            @Override
            protected void paintComponent(Graphics g) {
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
        btnCancelar.setFont(new Font("Inter", Font.BOLD, 14));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(220, 45));

        btnConfirmar = new JButton("CONFIRMAR CAMBIOS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#0077C0")); // Azul botón
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 35, 35));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnConfirmar.setContentAreaFilled(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new Font("Inter", Font.BOLD, 14));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setIcon(IconoManager.cargarIcono("editar.png", 18, 18)); // Usar un icono de editar
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.setPreferredSize(new Dimension(250, 45));

        btnPanel.add(btnCancelar);
        btnPanel.add(btnConfirmar);
        
        add(btnPanel, BorderLayout.SOUTH); // Botones fijos abajo

        // Scroll
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

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("TABLA CONSULTAR VEHICULO");
        title.setFont(new Font("Inter", Font.BOLD, 13));
        title.setForeground(GOLD);

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMM yyyy", new Locale("es", "MX")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        JLabel dateLabel = new JLabel(fecha);
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        dateLabel.setForeground(GOLD);

        leftPanel.add(title);
        leftPanel.add(dateLabel);

        cmbEstado = new JComboBox<>(new String[]{"LISTO", "EN REPARACION", "EN ESPERA"});       
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
                    else if (status.equals("EN REPARACION")) c.setForeground(colorReparacion);
                    else if (status.equals("EN ESPERA")) c.setForeground(colorEspera);
                }
                return c;
            }
        });

        cmbEstado.addActionListener(e -> {
            String status = (String) cmbEstado.getSelectedItem();
            if ("LISTO".equals(status)) cmbEstado.setForeground(colorListo);
            else if ("EN REPARACION".equals(status)) cmbEstado.setForeground(colorReparacion);
            else if ("EN ESPERA".equals(status)) cmbEstado.setForeground(colorEspera);
        });

        cmbEstado.setSelectedIndex(0); 

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(cmbEstado);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ==================== PANEL IZQUIERDO ====================
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
        JPanel clientePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        clientePanel.setOpaque(false);
        clientePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconCliente = new JLabel(IconoManager.cargarIcono("clientes.png", 32, 32));
        lblNombreCliente = new JLabel("Cargando Cliente...");
        lblNombreCliente.setFont(new Font("Inter", Font.BOLD, 20));

        clientePanel.add(iconCliente);
        clientePanel.add(lblNombreCliente);
        panel.add(clientePanel);
        panel.add(Box.createVerticalStrut(5));

        // --- Tipo de falla ---
        JLabel lblFalla = new JLabel("FALLA O REQUIRIMIENTO");
        lblFalla.setFont(new Font("Inter", Font.BOLD, 13));
        lblFalla.setForeground(Color.decode("#F29C1F")); // Naranja/Dorado
        lblFalla.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblFalla);
        panel.add(Box.createVerticalStrut(2));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbReparacion = new JRadioButton("Reparación");
        rbMantenimiento = new JRadioButton("Mantenimiento");
        rbGarantia = new JRadioButton("Garantía");

        grupoFalla = new ButtonGroup();
        grupoFalla.add(rbReparacion);
        grupoFalla.add(rbMantenimiento);
        grupoFalla.add(rbGarantia);

        rbReparacion.setOpaque(false); rbReparacion.setFont(new Font("Inter", Font.PLAIN, 13));
        rbMantenimiento.setOpaque(false); rbMantenimiento.setFont(new Font("Inter", Font.PLAIN, 13));
        rbGarantia.setOpaque(false); rbGarantia.setFont(new Font("Inter", Font.PLAIN, 13));
        
        rbReparacion.setSelected(true);

        radioPanel.add(rbReparacion);
        radioPanel.add(rbMantenimiento);
        radioPanel.add(rbGarantia);
        panel.add(radioPanel);
        panel.add(Box.createVerticalStrut(5));

        // Text area falla
        txtDescripcionFalla = new JTextArea(2, 20);
        txtDescripcionFalla.setLineWrap(true);
        txtDescripcionFalla.setWrapStyleWord(true);
        txtDescripcionFalla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#005064"), 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        txtDescripcionFalla.setFont(new Font("Inter", Font.PLAIN, 13));
        JScrollPane scrollFalla = new JScrollPane(txtDescripcionFalla);
        scrollFalla.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollFalla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(scrollFalla);
        panel.add(Box.createVerticalStrut(5));

        // --- Servicio o Producto ---
        JLabel lblServicio = new JLabel("SERVICIO O PRODUCTO");
        lblServicio.setFont(new Font("Inter", Font.BOLD, 13));
        lblServicio.setForeground(Color.decode("#F29C1F"));
        lblServicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblServicio);
        panel.add(Box.createVerticalStrut(2));

        // Lista de servicios interactiva (simulada)
        panelServicios = new JPanel();
        panelServicios.setLayout(new BoxLayout(panelServicios, BoxLayout.Y_AXIS));
        panelServicios.setOpaque(false);
        
        // Simular elementos de la lista
        panelServicios.add(crearItemServicio("Aceite Mobil 5W30", "$500.00"));
        panelServicios.add(crearItemServicio("Cambio de aceite", "$300.00"));
        
        // Nuevo elemento
        JTextField txtNuevoServicio = new JTextField("Agregar nuevo...");
        txtNuevoServicio.setForeground(Color.GRAY);
        txtNuevoServicio.setBorder(new EmptyBorder(5, 5, 5, 5));
        txtNuevoServicio.setOpaque(false);
        panelServicios.add(txtNuevoServicio);

        JScrollPane scrollServicios = new JScrollPane(panelServicios);
        scrollServicios.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#005064"), 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        scrollServicios.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollServicios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scrollServicios.setPreferredSize(new Dimension(0, 80));
        panel.add(scrollServicios);
        panel.add(Box.createVerticalStrut(5));

        // --- Costos (Subtotal, Impuesto, Total) ---
        JPanel costosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        costosPanel.setOpaque(false);
        costosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        costosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Subtotal
        JPanel subPanel = new JPanel(new GridLayout(2, 1));
        subPanel.setOpaque(false);
        JLabel lSub = new JLabel("SUBTOTAL:"); lSub.setFont(new Font("Inter", Font.PLAIN, 10)); lSub.setForeground(Color.decode("#F29C1F"));
        lblSubtotalVal = new JLabel("$800.00"); lblSubtotalVal.setFont(new Font("Inter", Font.PLAIN, 13));
        subPanel.add(lSub); subPanel.add(lblSubtotalVal);
        costosPanel.add(subPanel);

        // Impuesto
        JPanel impPanel = new JPanel(new GridLayout(2, 1));
        impPanel.setOpaque(false);
        JLabel lImp = new JLabel("IMPUESTO:"); lImp.setFont(new Font("Inter", Font.PLAIN, 10)); lImp.setForeground(Color.decode("#F29C1F"));
        lblImpuestoVal = new JLabel("$20.00"); lblImpuestoVal.setFont(new Font("Inter", Font.PLAIN, 13));
        impPanel.add(lImp); impPanel.add(lblImpuestoVal);
        costosPanel.add(impPanel);

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
        totPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        lblTotalVal = new JLabel("TOTAL: 820.00");
        lblTotalVal.setFont(new Font("Inter", Font.BOLD, 13));
        lblTotalVal.setForeground(Color.decode("#005064"));
        totPanel.add(lblTotalVal);
        costosPanel.add(totPanel);

        panel.add(costosPanel);

        return panel;
    }
    
    private JPanel crearItemServicio(String nombre, String precio) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel lblNombre = new JLabel(" " + nombre);
        lblNombre.setFont(new Font("Inter", Font.PLAIN, 13));
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        right.setOpaque(false);
        JLabel lblPrecio = new JLabel(precio);
        lblPrecio.setFont(new Font("Inter", Font.PLAIN, 13));
        
        // Botones simulados
        JLabel btnCheck = new JLabel("✔"); btnCheck.setForeground(Color.GRAY);
        JLabel btnMinus = new JLabel("━"); btnMinus.setForeground(Color.WHITE); btnMinus.setOpaque(true); btnMinus.setBackground(Color.decode("#005064"));
        
        right.add(lblPrecio);
        right.add(btnCheck);
        right.add(btnMinus);
        
        item.add(lblNombre, BorderLayout.CENTER);
        item.add(right, BorderLayout.EAST);
        return item;
    }

    // ==================== PANEL DERECHO ====================
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // --- Datos del vehículo (Tarjeta estática) ---
        JPanel vehiculoCard = new JPanel(new BorderLayout(10, 0));
        vehiculoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        vehiculoCard.setBackground(Color.WHITE);
        vehiculoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        vehiculoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel iconFord = new JLabel(IconoManager.cargarIcono("LogoFord.png", 50, 25)); // Simulando logo Ford
        
        JPanel infoVehiculo = new JPanel(new GridLayout(3, 1));
        infoVehiculo.setOpaque(false);
        lblVehiculoDesc = new JLabel("Ford Explorer"); lblVehiculoDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        JLabel lblPlaca = new JLabel("JKMN21"); lblPlaca.setFont(new Font("Inter", Font.PLAIN, 13));
        JLabel lblDetalle = new JLabel("2021 - Gris Oscuro"); lblDetalle.setFont(new Font("Inter", Font.PLAIN, 13));
        infoVehiculo.add(lblVehiculoDesc);
        infoVehiculo.add(lblPlaca);
        infoVehiculo.add(lblDetalle);
        
        JLabel lblEliminar = new JLabel("X Eliminar");
        lblEliminar.setForeground(Color.RED);
        lblEliminar.setFont(new Font("Inter", Font.PLAIN, 12));
        lblEliminar.setVerticalAlignment(SwingConstants.TOP);

        vehiculoCard.add(iconFord, BorderLayout.WEST);
        vehiculoCard.add(infoVehiculo, BorderLayout.CENTER);
        vehiculoCard.add(lblEliminar, BorderLayout.EAST);

        panel.add(vehiculoCard);
        panel.add(Box.createVerticalStrut(5));

        // --- Kilometraje y Combustible ---
        JPanel kmCombPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        kmCombPanel.setOpaque(false);
        kmCombPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        kmCombPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Kilometraje
        JPanel kmPanel = new JPanel(new BorderLayout(0, 5));
        kmPanel.setOpaque(false);
        JLabel lblKm = new JLabel("Kilometraje");
        lblKm.setFont(new Font("Inter", Font.PLAIN, 13));
        lblKm.setForeground(Color.decode("#F29C1F"));
        txtKilometraje = new JTextField("23,000 Kms.");
        txtKilometraje.setFont(new Font("Inter", Font.BOLD, 13));
        txtKilometraje.setHorizontalAlignment(SwingConstants.CENTER);
        txtKilometraje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        txtKilometraje.setEditable(false);
        kmPanel.add(lblKm, BorderLayout.NORTH);
        kmPanel.add(txtKilometraje, BorderLayout.CENTER);

        // Combustible
        JPanel combPanel = new JPanel(new BorderLayout(0, 5));
        combPanel.setOpaque(false);
        JLabel lblComb = new JLabel("Nivel de combustible");
        lblComb.setFont(new Font("Inter", Font.PLAIN, 13));
        lblComb.setForeground(Color.decode("#F29C1F"));
        
        JPanel progressContainer = new JPanel(new BorderLayout(5, 0));
        progressContainer.setOpaque(false);
        JLabel lblE = new JLabel("E"); lblE.setFont(new Font("Inter", Font.PLAIN, 10));
        JLabel lblF = new JLabel("F"); lblF.setFont(new Font("Inter", Font.PLAIN, 10));
        
        barraCombustible = new JProgressBar(0, 100);
        barraCombustible.setValue(38); // Como en la imagen
        barraCombustible.setForeground(TEAL);
        barraCombustible.setBackground(Color.WHITE);
        barraCombustible.setBorder(BorderFactory.createLineBorder(TEAL, 1, true));
        barraCombustible.setPreferredSize(new Dimension(100, 15));
        
        JPanel labelsBar = new JPanel(new BorderLayout());
        labelsBar.setOpaque(false);
        labelsBar.add(lblE, BorderLayout.WEST);
        JLabel lblMid = new JLabel("1/2"); lblMid.setFont(new Font("Inter", Font.PLAIN, 8)); lblMid.setHorizontalAlignment(SwingConstants.CENTER);
        labelsBar.add(lblMid, BorderLayout.CENTER);
        labelsBar.add(lblF, BorderLayout.EAST);
        
        JPanel barWrap = new JPanel(new BorderLayout());
        barWrap.setOpaque(false);
        barWrap.add(barraCombustible, BorderLayout.CENTER);
        barWrap.add(labelsBar, BorderLayout.SOUTH);
        
        JLabel lblPct = new JLabel("38%"); lblPct.setFont(new Font("Inter", Font.PLAIN, 11));
        
        progressContainer.add(barWrap, BorderLayout.CENTER);
        progressContainer.add(lblPct, BorderLayout.EAST);

        combPanel.add(lblComb, BorderLayout.NORTH);
        combPanel.add(progressContainer, BorderLayout.CENTER);

        kmCombPanel.add(kmPanel);
        kmCombPanel.add(combPanel);
        panel.add(kmCombPanel);
        panel.add(Box.createVerticalStrut(5));

        // --- Condición del vehículo ---
        JLabel lblCondicion = new JLabel("Condición del vehículo");
        lblCondicion.setFont(new Font("Inter", Font.PLAIN, 13));
        lblCondicion.setForeground(Color.decode("#F29C1F"));
        lblCondicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblCondicion);
        panel.add(Box.createVerticalStrut(2));

        txtCondicionVehiculo = new JTextArea(2, 20);
        txtCondicionVehiculo.setText("-Rayon de 26cm en puerta derecha trasera\n-Llantas con bajo niveles de aire");
        txtCondicionVehiculo.setLineWrap(true);
        txtCondicionVehiculo.setWrapStyleWord(true);
        txtCondicionVehiculo.setFont(new Font("Inter", Font.PLAIN, 13));
        txtCondicionVehiculo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollCondicion = new JScrollPane(txtCondicionVehiculo);
        scrollCondicion.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollCondicion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(scrollCondicion);
        panel.add(Box.createVerticalStrut(5));

        // --- Imagen del vehículo ---
        JLabel imgVehiculo = new JLabel(IconoManager.cargarIcono("truck.png", 350, 110)); 
        imgVehiculo.setAlignmentX(Component.LEFT_ALIGNMENT);
        imgVehiculo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.add(imgVehiculo);

        return panel;
    }

    // ==================== METODOS PARA EL CONTROLADOR ====================
    public void cargarDatosOrden(OrdenServicioModelo orden) {
        lblNombreCliente.setText(orden.getNombreCliente());
        lblVehiculoDesc.setText(orden.getVehiculoRelacionado());
        
        txtDescripcionFalla.setText("Mantenimiento programado."); // Falla simulada ya que el modelo no tiene este campo detallado.
        
        lblSubtotalVal.setText("$" + String.format("%.2f", orden.getMontoTotal() - 20.0)); // Simulando
        lblImpuestoVal.setText("$20.00");
        lblTotalVal.setText("TOTAL: " + String.format("%.2f", orden.getMontoTotal()));
        
        cmbEstado.setSelectedItem(orden.getEstado());
    }

    public void actualizarModelo(OrdenServicioModelo orden) {
        orden.setEstado((String) cmbEstado.getSelectedItem());
        orden.setMontoTotal(Double.parseDouble(lblTotalVal.getText().replace("TOTAL: ", "").trim()));
        // Otros campos se actualizarían aquí si el modelo los soportara
    }

    public JButton getBtnConfirmar() { return btnConfirmar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
