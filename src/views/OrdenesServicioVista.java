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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import models.OrdenServicioModelo;

/**
 * Vista de la sección "Órdenes de servicio" del menú lateral.
 * Muestra una tabla con todas las órdenes de servicio del taller.
 * 
 * COLUMNAS (según la imagen):
 * Círculo | NOMBRE DE CLIENTE | VEHÍCULO RELACIONADO | FECHA DE INGRESO | FECHA DE ENTREGA |
 * COSTO MANO DE OBRA | COSTO DE REFACCIONES | MONTO TOTAL + (IVA) | ESTADO | Acción
 */
public class OrdenesServicioVista extends JPanel {

    // --- Colores del diseño ---
    private static final Color BG_LIGHT = Color.decode("#EBF0F5");
    private static final Color HEADER_BG = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#FFB800");
    private static final Color ROW_BG_1 = Color.decode("#E3CAA5");
    private static final Color ROW_BG_2 = Color.decode("#9DB2BF");

    // --- Componentes de la vista ---
    private DefaultTableModel tableModel;
    private JTable tablaOrdenes;

    /**
     * Interfaz para que el controlador escuche las acciones del usuario.
     */
    public interface AccionListener {
        void onEditar(int row);
        void onDescargarPDF(int row);
        void onEliminar(int row);
    }

    private AccionListener accionListener;

    // Constructor
    public OrdenesServicioVista() {
        setBackground(BG_LIGHT);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 20, 20, 20));

        centerPanel.add(createToolbar(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void setAccionListener(AccionListener listener) {
        this.accionListener = listener;
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

        JLabel title = new JLabel("TABLA DE ÓRDENES DE SERVICIO");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(GOLD);

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMM yyyy", new Locale("es", "MX")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        JLabel dateLabel = new JLabel(fecha);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateLabel.setForeground(Color.decode("#B4C8D2"));

        leftPanel.add(title);
        leftPanel.add(dateLabel);

        JLabel techLabel = new JLabel("  Técnico: Juan Angel  ");
        techLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
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

    // ==================== BARRA DE HERRAMIENTAS ====================

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Solo barra de búsqueda (sin botón agregar, porque las órdenes se crean desde "Crear orden")
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        JLabel searchIcon = new JLabel(IconoManager.cargarIcono("buscar.png", 20, 20));
        JTextField txtBuscar = new JTextField(25);
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

        JPanel containerSearch = new JPanel() {
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

        toolbar.add(searchPanel, BorderLayout.WEST);
        return toolbar;
    }

    // ==================== TABLA ====================

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        // Columnas según la imagen de "Órdenes de Servicio"
        String[] columnas = {" ", "NOMBRE DE CLIENTE", "VEHICULO RELACIONADO",
                "FECHA DE INGRESO", "FECHA DE ENTREGA",
                "COSTO MANO DE OBRA", "COSTO DE REFACCIONES",
                "MONTO TOTAL + (IVA)", "ESTADO", "Acción"};

        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Solo la columna de acciones
            }
        };

        tablaOrdenes = new JTable(tableModel);
        tablaOrdenes.setRowHeight(40);
        tablaOrdenes.setIntercellSpacing(new Dimension(0, 0));
        tablaOrdenes.setShowGrid(true);
        tablaOrdenes.setGridColor(Color.WHITE);

        // Estilo del encabezado
        tablaOrdenes.getTableHeader().setBackground(HEADER_BG);
        tablaOrdenes.getTableHeader().setForeground(GOLD);
        tablaOrdenes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        tablaOrdenes.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Renderer para filas de colores alternos
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ROW_BG_1 : ROW_BG_2);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        // Círculo estético en la primera columna
        tablaOrdenes.getColumnModel().getColumn(0).setCellRenderer(new CircleRenderer());
        tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(30);

        // Renderer centrado para las columnas de datos
        for (int i = 1; i <= 8; i++) {
            tablaOrdenes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Renderer y Editor de acciones en la última columna
        tablaOrdenes.getColumnModel().getColumn(9).setCellRenderer(new AccionesRenderer());
        tablaOrdenes.getColumnModel().getColumn(9).setCellEditor(new AccionesEditor());
        tablaOrdenes.getColumnModel().getColumn(9).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tablaOrdenes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_LIGHT);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(createPagination(), BorderLayout.SOUTH);
        return tablePanel;
    }

    // ==================== PAGINACIÓN ====================

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

        pagPanel.add(left); pagPanel.add(page1); pagPanel.add(page2);
        pagPanel.add(page3); pagPanel.add(dots); pagPanel.add(page5); pagPanel.add(right);
        return pagPanel;
    }

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Carga la lista de órdenes en la tabla.
     */
    public void setOrdenes(List<OrdenServicioModelo> ordenes) {
        tableModel.setRowCount(0);
        for (OrdenServicioModelo o : ordenes) {
            tableModel.addRow(new Object[]{
                    "",
                    o.getNombreCliente(),
                    o.getVehiculoRelacionado(),
                    o.getFechaIngreso(),
                    o.getFechaEntrega(),
                    String.format("$%.2f", o.getCostoManoObra()),
                    String.format("$%.2f", o.getCostoRefacciones()),
                    String.format("$%.2f", o.getMontoTotal()),
                    o.getEstado(),
                    ""
            });
        }
    }

    // ==================== RENDERERS Y EDITORS ====================

    class CircleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? ROW_BG_1 : ROW_BG_2));
            JLabel circle = new JLabel("○");
            circle.setFont(new Font("SansSerif", Font.BOLD, 18));
            p.add(circle);
            return p;
        }
    }

    class AccionesRenderer extends JPanel implements TableCellRenderer {
        public AccionesRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
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
                fireEditingStopped();
                if (accionListener != null) accionListener.onEditar(currentRow);
            });
            btnPdf.addActionListener(e -> {
                fireEditingStopped();
                if (accionListener != null) accionListener.onDescargarPDF(currentRow);
            });
            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                if (accionListener != null) accionListener.onEliminar(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return null; }
    }
}
