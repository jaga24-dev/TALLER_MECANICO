package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import models.ClienteModelo;
import models.VehiculoModelo;

/**
 * Diálogo de formulario para Agregar o Editar un Vehículo.
 * Diseño de dos columnas que coincide con el mockup proporcionado.
 */
public class VehiculosDialog extends JDialog {

	private static final Color TEAL = Color.decode("#005064");
	private static final Color BLUE_BTN = Color.decode("#006DB2");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color BG_RIGHT = Color.decode("#EBEBEB");
    private static final Color GRAY_BTN = Color.decode("#9DB2BF");
    private static final Color RED_TEXT = Color.decode("#D32F2F");

    // Campos del formulario
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtAnio;
    private JTextField txtPlacas;
    private JTextField txtNumSerie;
    private javax.swing.JComboBox<String> cmbCliente;
    private java.util.List<models.ClienteModelo> clientes;
    private JButton btnSubirImagen;
    private JLabel lblImgNombre;
    private String rutaImagenSeleccionada = null;

    // Indica si el usuario guardó o canceló
    private boolean guardado = false;

    // Vehículo que se está editando (null si es nuevo)
    private VehiculoModelo vehiculoOriginal;

    /**
     * Constructor.
     * @param owner Ventana padre.
     * @param vehiculoAEditar null para crear uno nuevo, o un VehiculoModelo existente para editarlo.
     */
    public VehiculosDialog(Window owner, VehiculoModelo vehiculoAEditar, java.util.List<models.ClienteModelo> clientes) {
    	super(owner, vehiculoAEditar == null ? "Registrar Vehículo" : "Editar Vehículo", ModalityType.APPLICATION_MODAL);
        this.vehiculoOriginal = vehiculoAEditar;
        this.clientes = clientes;

        setSize(850, 620);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.setColor(TEAL);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
            }
        };
        bgPanel.setOpaque(false);
        bgPanel.setLayout(new BorderLayout());
        bgPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 0, 15));

        // ================= PANEL IZQUIERDO =================
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = crearLabel("INGRESA LOS SIGUIENTES DATOS:", 14);
        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(20));

        // Fila 1: Marca + Modelo
        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 0));
        row1.setOpaque(false);
        txtMarca = crearTextField("");
        txtModelo = crearTextField("");
        row1.add(crearInputGroup("MARCA", txtMarca));
        row1.add(crearInputGroup("MODELO", txtModelo));
        leftPanel.add(row1);
        leftPanel.add(Box.createVerticalStrut(10));

        // Fila 2: Año + Placas
        JPanel row2 = new JPanel(new GridLayout(1, 2, 15, 0));
        row2.setOpaque(false);
        txtAnio = crearTextField("");
        txtPlacas = crearTextField("");
        row2.add(crearInputGroup("AÑO", txtAnio));
        row2.add(crearInputGroup("PLACAS", txtPlacas));
        leftPanel.add(row2);
        leftPanel.add(Box.createVerticalStrut(10));

        // Fila 3: Número de Serie
        txtNumSerie = crearTextField("");
        JPanel pnlSerie = crearInputGroup("NUMERO DE SERIE", txtNumSerie);
        pnlSerie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        leftPanel.add(pnlSerie);
        leftPanel.add(Box.createVerticalStrut(20));

        // Historial de Servicios (visual)
        leftPanel.add(crearLabel("HISTORIAL DE SERVICIOS", 13));
        leftPanel.add(Box.createVerticalStrut(5));

        List<models.OrdenServicioModelo> ordenes = vehiculoAEditar != null ? models.OrdenServicioModelo.obtenerPorVehiculo(vehiculoAEditar.getId()) : new ArrayList<>();
        DefaultTableModel modelHistorial = new DefaultTableModel(new String[]{"Servicio", "Costo", " "}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (models.OrdenServicioModelo o : ordenes) {
            String resumenFalla = o.getFallaReportada() != null && o.getFallaReportada().length() > 20 ? o.getFallaReportada().substring(0, 20) + "..." : o.getFallaReportada();
            modelHistorial.addRow(new Object[]{resumenFalla, "$" + String.format("%.2f", o.getMontoTotal()), "Ver"});
        }
        if (ordenes.isEmpty()) {
            modelHistorial.addRow(new Object[]{"Sin historial", "-", "-"});
        }

        JTable tablaHistorial = new JTable(modelHistorial);
        tablaHistorial.setRowHeight(30);
        tablaHistorial.setShowGrid(true);
        tablaHistorial.setGridColor(Color.LIGHT_GRAY);
        tablaHistorial.setFont(new Font("Inter", Font.PLAIN, 12));
        tablaHistorial.getTableHeader().setUI(null);
        tablaHistorial.setBorder(BorderFactory.createLineBorder(TEAL, 2, true));
        
        // Evento doble clic para ver resumen
        tablaHistorial.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tablaHistorial.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < ordenes.size()) {
                        models.OrdenServicioModelo o = ordenes.get(row);
                        JOptionPane.showMessageDialog(VehiculosDialog.this, 
                            "Orden ID: " + o.getId() + "\nFecha: " + o.getFechaIngreso() + "\nFalla: " + o.getFallaReportada() + "\nTotal: $" + o.getMontoTotal(), 
                            "Detalle de Servicio", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setForeground(TEAL);
                l.setFont(new Font("Inter", Font.BOLD, 12));
                return l;
            }
        });

        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBorder(BorderFactory.createEmptyBorder());
        scrollHistorial.setPreferredSize(new Dimension(350, 150));
        leftPanel.add(scrollHistorial);

        // ================= PANEL DERECHO =================
        JPanel rightPanelWrap = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_RIGHT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
            }
        };
        rightPanelWrap.setOpaque(false);
        rightPanelWrap.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // 1. Tarjeta de información
        JPanel cardInfo = new JPanel(new BorderLayout());
        cardInfo.setBackground(Color.WHITE);
        cardInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblIconUser = new JLabel("👤 ");
        lblIconUser.setFont(new Font("Inter", Font.PLAIN, 24));
        lblIconUser.setForeground(TEAL);

        JLabel lblNombre = new JLabel("<html><b>DATOS DEL VEHÍCULO</b></html>");
        lblNombre.setFont(new Font("Inter", Font.BOLD, 13));
        lblNombre.setForeground(GOLD);

        JLabel lblEliminar = new JLabel("X Eliminar");
        lblEliminar.setFont(new Font("Inter", Font.BOLD, 11));
        lblEliminar.setForeground(RED_TEXT);
        lblEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblEliminar.setVerticalAlignment(SwingConstants.TOP);

        JPanel pnlUserText = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlUserText.setOpaque(false);
        pnlUserText.add(lblIconUser);
        pnlUserText.add(lblNombre);

        cardInfo.add(pnlUserText, BorderLayout.CENTER);
        cardInfo.add(lblEliminar, BorderLayout.EAST);
        cardInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        rightPanel.add(cardInfo);
        rightPanel.add(Box.createVerticalStrut(15));

        // 2. Cliente Propietario
        rightPanel.add(crearLabel("CLIENTE PROPIETARIO", 12));
        rightPanel.add(Box.createVerticalStrut(5));
        
        cmbCliente = new javax.swing.JComboBox<>();
        cmbCliente.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbCliente.setBackground(Color.WHITE);
        cmbCliente.setBorder(BorderFactory.createLineBorder(TEAL, 2, true));
        
        if (clientes != null) {
            for (models.ClienteModelo c : clientes) {
                cmbCliente.addItem(c.getNombreCompleto() + " - " + c.getCorreo());
            }
        }
        
        JPanel pnlCliente = new JPanel(new BorderLayout());
        pnlCliente.setOpaque(false);
        pnlCliente.add(cmbCliente, BorderLayout.CENTER);
        pnlCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        rightPanel.add(pnlCliente);
        rightPanel.add(Box.createVerticalStrut(15));

        // 4. Imagen del auto
        JPanel pnlCarImage = new JPanel(new BorderLayout());
        pnlCarImage.setBackground(Color.WHITE);
        pnlCarImage.setBorder(BorderFactory.createLineBorder(TEAL, 2, true));
        JLabel lblCarImage = new JLabel("🚗 IMAGEN VEHÍCULO");
        lblCarImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblCarImage.setFont(new Font("Inter", Font.PLAIN, 16));
        lblCarImage.setForeground(Color.GRAY);
        try {
            if (vehiculoAEditar != null && vehiculoAEditar.getImagen() != null) {
                lblCarImage.setIcon(new ImageIcon(new ImageIcon(vehiculoAEditar.getImagen()).getImage().getScaledInstance(250, 130, Image.SCALE_SMOOTH)));
                lblCarImage.setText("");
            } else {
                javax.swing.Icon carIcon = IconoManager.cargarIcono("car.png", 250, 130);
                if (carIcon != null) {
                    lblCarImage.setIcon(carIcon);
                    lblCarImage.setText("");
                }
            }
        } catch (Exception e) { /* usar texto por defecto */ }
        pnlCarImage.add(lblCarImage, BorderLayout.CENTER);
        pnlCarImage.setPreferredSize(new Dimension(300, 150));
        rightPanel.add(pnlCarImage);
        
        btnSubirImagen = new JButton("Subir Imagen del Vehículo");
        btnSubirImagen.setFont(new Font("Inter", Font.BOLD, 12));
        btnSubirImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubirImagen.addActionListener(e -> seleccionarImagenVehiculo(lblCarImage));
        
        lblImgNombre = new JLabel("Ninguna imagen seleccionada", SwingConstants.CENTER);
        lblImgNombre.setFont(new Font("Inter", Font.PLAIN, 11));
        
        JPanel pnlImgBotones = new JPanel(new BorderLayout());
        pnlImgBotones.setOpaque(false);
        pnlImgBotones.add(btnSubirImagen, BorderLayout.NORTH);
        pnlImgBotones.add(lblImgNombre, BorderLayout.SOUTH);
        
        rightPanel.add(pnlImgBotones);

        rightPanelWrap.add(rightPanel, BorderLayout.CENTER);

        // ================= ENSAMBLE =================
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanelWrap);
        bgPanel.add(contentPanel, BorderLayout.CENTER);

        // ================= BOTONES =================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        btnPanel.setOpaque(false);

        JButton btnCancelar = crearBotonRedondeado("X  CANCELAR", GRAY_BTN, Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(220, 50));
        btnCancelar.setFont(new Font("Inter", Font.BOLD, 15));
        btnCancelar.addActionListener(e -> dispose());

        String textoBtn = vehiculoAEditar == null ? "REGISTRAR VEHICULO" : "GUARDAR CAMBIOS";
        JButton btnRegistrar = crearBotonRedondeado(textoBtn, BLUE_BTN, Color.WHITE);
        btnRegistrar.setPreferredSize(new Dimension(270, 50));
        btnRegistrar.setFont(new Font("Inter", Font.BOLD, 15));
        try {
            btnRegistrar.setIcon(IconoManager.cargarIcono("editar.png", 18, 18));
        } catch (Exception e) { /* sin icono */ }
        btnRegistrar.addActionListener(e -> guardar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnRegistrar);
        bgPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bgPanel);
        

        // Si estamos editando, rellenar los campos con los datos existentes
        if (vehiculoAEditar != null) {
            txtMarca.setText(vehiculoAEditar.getMarca());
            txtModelo.setText(vehiculoAEditar.getModelo());
            txtAnio.setText(String.valueOf(vehiculoAEditar.getAnio()));
            txtPlacas.setText(vehiculoAEditar.getPlacas());
            txtNumSerie.setText(vehiculoAEditar.getNumeroSerie() != null ? vehiculoAEditar.getNumeroSerie() : "");
            
            // Seleccionar cliente en el combobox
            if (clientes != null && vehiculoAEditar.getIdCliente() != null) {
                for (int i = 0; i < clientes.size(); i++) {
                    if (clientes.get(i).getId().equals(vehiculoAEditar.getIdCliente())) {
                        cmbCliente.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }
    
    // ================= MÉTODOS AUXILIARES =================

    private JLabel crearLabel(String texto, int size) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Inter", Font.BOLD, size));
        label.setForeground(GOLD);
        return label;
    }

    private JTextField crearTextField(String text) {
    	JTextField textField = new JTextField(text);
        textField.setFont(new Font("Inter", Font.PLAIN, 13));
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createLineBorder(TEAL, 2, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return textField;
    }

    private JPanel crearInputGroup(String label, Component input) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.add(crearLabel(label, 12), BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
    
    private JButton crearBotonRedondeado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override 
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 40, 40));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(fg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void seleccionarImagenVehiculo(JLabel lblCarImage) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = chooser.getSelectedFile();
            try {
                File dir = new File("imagenes_vehiculos");
                if (!dir.exists()) dir.mkdirs();
                
                File destino = new File(dir, System.currentTimeMillis() + "_" + archivoSeleccionado.getName());
                Files.copy(archivoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                rutaImagenSeleccionada = destino.getAbsolutePath();
                lblImgNombre.setText(archivoSeleccionado.getName());
                
                lblCarImage.setText("");
                lblCarImage.setIcon(new ImageIcon(new ImageIcon(rutaImagenSeleccionada).getImage().getScaledInstance(250, 130, Image.SCALE_SMOOTH)));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al copiar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Método que se ejecuta al pulsar "Registrar/Guardar".
     * Valida campos, actualiza el vehículo original (si edita) o marca guardado=true.
     */
    private void guardar() {
        if (txtMarca.getText().trim().isEmpty() || txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Marca y Modelo son obligatorios.");
            return;
        }

        if (rutaImagenSeleccionada == null && (vehiculoOriginal == null || vehiculoOriginal.getImagen() == null)) {
            JOptionPane.showMessageDialog(this, "Debe subir una imagen del vehículo.", "Falta Imagen", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anio = 0;
        try {
        	if (!txtAnio.getText().trim().isEmpty())
                anio = Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Año inválido.");
            return;
        }

        if (vehiculoOriginal != null) {
            // Modo edición: actualizar el objeto existente
            vehiculoOriginal.setMarca(txtMarca.getText().trim());
            vehiculoOriginal.setModelo(txtModelo.getText().trim());
            vehiculoOriginal.setAnio(anio);
            vehiculoOriginal.setPlacas(txtPlacas.getText().trim());
            vehiculoOriginal.setNumeroSerie(txtNumSerie.getText().trim());
            // vehiculoOriginal.setFallaReportada(txtFalla.getText().trim());
        }

        guardado = true;
        dispose();
    }

 // ================= GETTERS PÚBLICOS =================

    public boolean isGuardado() {
        return guardado;
    }

    /** Devuelve los datos ingresados como marca */
    public String getMarca() { return txtMarca.getText().trim(); }
    public String getModelo() { return txtModelo.getText().trim(); }
    public String getAnioText() { return txtAnio.getText().trim(); }
    public String getPlacas() { return txtPlacas.getText().trim(); }
    public String getNumSerie() { return txtNumSerie.getText().trim(); }
    public String getRutaImagen() { return rutaImagenSeleccionada; }
    
    public String getIdClienteSeleccionado() {
        if (clientes == null || clientes.isEmpty()) return "1";
        int idx = cmbCliente.getSelectedIndex();
        if (idx >= 0 && idx < clientes.size()) {
            return clientes.get(idx).getId();
        }
        return "1";
    }
}
