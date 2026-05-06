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
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import models.ClienteModelo;

public class ClientesVista extends JPanel {

    private static final Color BG_LIGHT = Color.decode("#EBF0F5");
    private static final Color HEADER_BG = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color TABLE_HEADER_BG = Color.decode("#00314A");
    private static final Color ROW_BG_1 = Color.decode("#E4C25E"); // Similar to screenshot row 1
    private static final Color ROW_BG_2 = Color.decode("#014461"); // Similar to screenshot row 2

    private DefaultTableModel tableModel;
    private JTable tablaClientes;
    private JButton btnAgregar;
    private JTextField txtBuscar;

    public interface AccionListener {
        void onEditar(int row);
        void onDescargarPDF(int row);
        void onEliminar(int row);
        void onVerVehiculos(int row);
    }

    private AccionListener accionListener;

    public ClientesVista() {
        setBackground(BG_LIGHT); // Color de fondo general
        setLayout(new BorderLayout()); // Layout principal (Norte, Sur, Este, Oeste, Centro)

        // Agregamos la cabecera (Header) en la parte superior (Norte)
        add(createHeader(), BorderLayout.NORTH);

        // Creamos un panel central que contendrá la barra de búsqueda y la tabla
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false); // Transparente para ver el fondo
        centerPanel.setBorder(new EmptyBorder(15, 20, 20, 20)); // Márgenes (Arriba, Izquierda, Abajo, Derecha)

        // Agregamos la barra de herramientas (búsqueda y botón agregar) arriba del centro
        centerPanel.add(createToolbar(), BorderLayout.NORTH);
        // Agregamos la tabla en el medio del centro
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Finalmente, agregamos este panel central al diseño principal
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Permite al controlador escuchar cuando se hace clic en los botones de acción
     * (Editar, Descargar PDF, Eliminar).
     */
    public void setAccionListener(AccionListener listener) {
        this.accionListener = listener;
    }

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

        JLabel title = new JLabel("TABLA DE REGISTROS");
        title.setFont(new Font("Inter", Font.BOLD, 13));
        title.setForeground(GOLD);

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMM yyyy", new Locale("es", "MX")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        JLabel dateLabel = new JLabel(fecha);
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        dateLabel.setForeground(GOLD);

        leftPanel.add(title);
        leftPanel.add(dateLabel);

        JLabel techLabel = new JLabel("  Técnico: Juan Angel  ");
        techLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        techLabel.setForeground(GOLD);
        techLabel.setIcon(IconoManager.cargarIcono("tecnico.png", 16, 16));
        techLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(techLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        JLabel searchIcon = new JLabel(IconoManager.cargarIcono("buscar.png", 20, 20));
        txtBuscar = new JTextField(25);
        txtBuscar.setBackground(HEADER_BG);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER_BG, 8),
                BorderFactory.createMatteBorder(0, 0, 1, 0, GOLD)
        ));
        
        JPanel wrapSearch = new JPanel(new BorderLayout(5, 0));
        wrapSearch.setBackground(HEADER_BG);
        wrapSearch.setBorder(new EmptyBorder(5, 10, 5, 10));
        wrapSearch.add(searchIcon, BorderLayout.WEST);
        wrapSearch.add(txtBuscar, BorderLayout.CENTER);
        // Hacer esquinas redondeadas simuladas
        JPanel containerSearch = new JPanel(){
             @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HEADER_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        containerSearch.setOpaque(false);
        containerSearch.setLayout(new BorderLayout());
        containerSearch.add(wrapSearch);
        
        searchPanel.add(containerSearch);

        // Botón Agregar
        btnAgregar = new JButton("Agregar cliente nuevo");
        btnAgregar.setIcon(IconoManager.cargarIcono("agregar.png", 18, 18));
        btnAgregar.setForeground(Color.decode("#00314A"));
        btnAgregar.setBackground(GOLD);
        btnAgregar.setFont(new Font("Inter", Font.BOLD, 12));
        //btnAgregar.setFocusPainted(false);
        btnAgregar.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(btnAgregar, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        String[] columnas = {" ", "ID CLIENTE", "NOMBRE COMPLETO", "TÉLEFONO", "CORREO", "HISTORIAL DE VEHICULOS", "ACCION"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 5; // Solo acciones e historial clickeable
            }
        };

        tablaClientes = new JTable(tableModel);
        tablaClientes.setRowHeight(35);
        tablaClientes.setIntercellSpacing(new Dimension(0, 0));
        tablaClientes.setShowGrid(true);
        tablaClientes.setGridColor(Color.WHITE);

        // Estilos Header
        tablaClientes.getTableHeader().setBackground(TABLE_HEADER_BG);
        tablaClientes.getTableHeader().setForeground(GOLD);
        tablaClientes.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));
        tablaClientes.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Renderer para filas de colores alternos y texto centrado
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ROW_BG_1 : ROW_BG_2);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        // Renderers y Editors
        tablaClientes.getColumnModel().getColumn(0).setCellRenderer(new CircleRenderer());
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(30);
        
        for (int i = 1; i <= 4; i++) {
            tablaClientes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Historial Renderer (simular ComboBox)
        tablaClientes.getColumnModel().getColumn(5).setCellRenderer(new HistorialRenderer());
        tablaClientes.getColumnModel().getColumn(5).setCellEditor(new HistorialEditor());
        tablaClientes.getColumnModel().getColumn(5).setPreferredWidth(150);

        // Acciones Renderer/Editor
        tablaClientes.getColumnModel().getColumn(6).setCellRenderer(new AccionesRenderer());
        tablaClientes.getColumnModel().getColumn(6).setCellEditor(new AccionesEditor());
        tablaClientes.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_LIGHT);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(createPagination(), BorderLayout.SOUTH);

        return tablePanel;
    }

    private JPanel createPagination() {
        JPanel pagPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pagPanel.setBackground(HEADER_BG);
        
        JLabel left = new JLabel(IconoManager.cargarIcono("izq.png", 16, 16));
        JLabel page1 = new JLabel(" 1 "); page1.setForeground(Color.BLACK); page1.setBackground(GOLD); page1.setOpaque(true);
        JLabel page2 = new JLabel(" 2 "); page2.setForeground(Color.WHITE);
        JLabel page3 = new JLabel(" 3 "); page3.setForeground(Color.WHITE);
        JLabel dots = new JLabel(" ... "); dots.setForeground(Color.WHITE);
        JLabel page5 = new JLabel(" 5 "); page5.setForeground(Color.WHITE);
        JLabel right = new JLabel(IconoManager.cargarIcono("der.png", 16, 16));

        pagPanel.add(left);
        pagPanel.add(page1);
        pagPanel.add(page2);
        pagPanel.add(page3);
        pagPanel.add(dots);
        pagPanel.add(page5);
        pagPanel.add(right);

        return pagPanel;
    }

    public void setClientes(List<ClienteModelo> clientes) {
        tableModel.setRowCount(0);
        for (ClienteModelo c : clientes) {
            tableModel.addRow(new Object[]{
                    "", c.getId(), c.getNombreCompleto(), c.getTelefono(), c.getCorreo(), c.getResumenVehiculos(), ""
            });
        }
    }

    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    // --- Clases Internas para Renderers ---
    // NOTA: 
    // Los "Renderers" (dibujadores) se usan para cambiar cómo se ve una celda en una JTable.
    // Los "Editors" (editores) se usan para controlar qué pasa cuando el usuario hace clic en la celda.

    /**
     * Dibuja un círculo vacío ("○") en la primera columna por estética.
     */
    class CircleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? ROW_BG_1 : ROW_BG_2));
            JLabel circle = new JLabel("○");
            circle.setFont(new Font("Inter", Font.BOLD, 18));
            p.add(circle);
            return p;
        }
    }

    /**
     * Muestra el resumen de vehículos (ej: "Ford Explorer ▼").
     */
    class HistorialRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? ROW_BG_1 : ROW_BG_2));
            JLabel lbl = new JLabel(value != null ? value.toString() + " ▼" : "Sin vehículos ▼");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Inter", Font.PLAIN, 12));
            p.add(lbl, BorderLayout.CENTER);
            return p;
        }
    }

    /**
     * Detecta cuando el usuario hace clic en el historial y avisa al controlador
     * para que abra la ventana detallada de vehículos.
     */
    class HistorialEditor extends AbstractCellEditor implements TableCellEditor {
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Si hay alguien escuchando (el controlador), avisamos que se quieren ver los vehículos de esta fila
            if (accionListener != null) {
                SwingUtilities.invokeLater(() -> accionListener.onVerVehiculos(row));
            }
            return new JLabel(value != null ? value.toString() : "");
        }
        @Override public Object getCellEditorValue() { return null; }
    }

    /**
     * Dibuja los 3 botones (Lápiz, PDF, Equis) en la última columna.
     */
    class AccionesRenderer extends JPanel implements TableCellRenderer {
        public AccionesRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 8));
            setBackground(ROW_BG_1);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? ROW_BG_1 : ROW_BG_2));
            
            JLabel lblEdit = new JLabel(IconoManager.cargarIcono("editar.png", 20, 20));
            JLabel lblPdf = new JLabel(IconoManager.cargarIcono("pdf.png", 20, 20));
            JLabel lblDelete = new JLabel(IconoManager.cargarIcono("eliminar.png", 20, 20));
            
            lblEdit.setOpaque(true); lblEdit.setBackground(Color.decode("#005064"));
            lblPdf.setOpaque(true); lblPdf.setBackground(Color.decode("#005064"));
            lblDelete.setOpaque(true); lblDelete.setBackground(Color.decode("#D32F2F"));

            add(lblEdit); add(lblPdf); add(lblDelete);
            return this;
        }
    }

    /**
     * Controla los clics reales en los 3 botones (Lápiz, PDF, Basurero).
     */
    class AccionesEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private int currentRow;

        public AccionesEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton btnEdit = new JButton(IconoManager.cargarIcono("editar.png", 20, 20));
            JButton btnPdf = new JButton(IconoManager.cargarIcono("pdf.png", 20, 20));
            JButton btnDelete = new JButton(IconoManager.cargarIcono("eliminar.png", 20, 20));
            
            btnEdit.setBackground(Color.decode("#005064"));
            btnPdf.setBackground(Color.decode("#005064"));
            btnDelete.setBackground(Color.decode("#D32F2F"));

            Dimension d = new Dimension(28, 28);
            for (JButton b : new JButton[]{btnEdit, btnPdf, btnDelete}) {
                b.setPreferredSize(d);
                b.setFocusPainted(false);
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
                panel.add(b);
            }

            btnEdit.addActionListener(e -> { 
                fireEditingStopped(); // Detenemos la edición para que la tabla no se trabe
                if (accionListener!=null) accionListener.onEditar(currentRow); 
            });
            btnPdf.addActionListener(e -> { 
                fireEditingStopped(); 
                if (accionListener!=null) accionListener.onDescargarPDF(currentRow); 
            });
            btnDelete.addActionListener(e -> { 
                fireEditingStopped(); 
                if (accionListener!=null) accionListener.onEliminar(currentRow); 
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override public Object getCellEditorValue() { return null; }
    }
}
