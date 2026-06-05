package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import models.DetalleOrdenModelo;

/**
 * Panel reutilizable para agregar/eliminar servicios o productos con su precio.
 * Calcula el subtotal automáticamente y notifica cambios al controlador.
 */
public class PanelListaServicios extends JPanel {

    private static final Color TEAL = Color.decode("#005064");
    private static final Color GOLD = Color.decode("#E4C25E");

    private JPanel panelItems;
    private JTextField txtConcepto;
    private JTextField txtPrecio;
    private JButton btnAgregar;

    private List<DetalleOrdenModelo> listaDetalles;
    private Runnable onListChanged;

    public PanelListaServicios() {
        listaDetalles = new ArrayList<>();
        setLayout(new BorderLayout(0, 5));
        setOpaque(false);

        // --- Panel superior: campos para agregar ---
        JPanel panelAgregar = new JPanel(new BorderLayout(5, 0));
        panelAgregar.setOpaque(false);

        txtConcepto = new JTextField();
        txtConcepto.setFont(new Font("Inter", Font.PLAIN, 12));
        txtConcepto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1, true),
                new EmptyBorder(4, 6, 4, 6)));
        txtConcepto.setToolTipText("Nombre del servicio o producto");

        txtPrecio = new JTextField("0.00");
        txtPrecio.setFont(new Font("Inter", Font.PLAIN, 12));
        txtPrecio.setPreferredSize(new Dimension(80, 28));
        txtPrecio.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPrecio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 1, true),
                new EmptyBorder(4, 6, 4, 6)));
        txtPrecio.setToolTipText("Precio");

        btnAgregar = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnAgregar.setContentAreaFilled(false);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Inter", Font.BOLD, 14));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.setPreferredSize(new Dimension(36, 28));
        btnAgregar.addActionListener(e -> agregarElemento());

        // Enter en precio también agrega
        txtPrecio.addActionListener(e -> agregarElemento());
        txtConcepto.addActionListener(e -> txtPrecio.requestFocusInWindow());

        JPanel rightAdd = new JPanel(new BorderLayout(4, 0));
        rightAdd.setOpaque(false);
        rightAdd.add(txtPrecio, BorderLayout.CENTER);
        rightAdd.add(btnAgregar, BorderLayout.EAST);

        panelAgregar.add(txtConcepto, BorderLayout.CENTER);
        panelAgregar.add(rightAdd, BorderLayout.EAST);

        // --- Panel lista de items ---
        panelItems = new JPanel();
        panelItems.setLayout(new BoxLayout(panelItems, BoxLayout.Y_AXIS));
        panelItems.setOpaque(false);

        JScrollPane scroll = new JScrollPane(panelItems);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL, 2, true),
                new EmptyBorder(4, 4, 4, 4)));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(8);

        add(panelAgregar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /** Registra un callback que se ejecuta cuando la lista cambia. */
    public void setOnListChanged(Runnable onListChanged) {
        this.onListChanged = onListChanged;
    }

    private void agregarElemento() {
        String concepto = txtConcepto.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        if (concepto.isEmpty()) return;

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.",
                    "Precio inválido", JOptionPane.WARNING_MESSAGE);
            txtPrecio.requestFocusInWindow();
            return;
        }

        DetalleOrdenModelo detalle = new DetalleOrdenModelo(null, null, concepto, precio);
        listaDetalles.add(detalle);
        renderizarLista();

        txtConcepto.setText("");
        txtPrecio.setText("0.00");
        txtConcepto.requestFocusInWindow();

        if (onListChanged != null) onListChanged.run();
    }

    private void renderizarLista() {
        panelItems.removeAll();
        for (int i = 0; i < listaDetalles.size(); i++) {
            final int index = i;
            DetalleOrdenModelo detalle = listaDetalles.get(i);

            JPanel item = new JPanel(new BorderLayout()) {
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(Integer.MAX_VALUE, 30);
                }
            };
            item.setOpaque(false);
            item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            JLabel lblNombre = new JLabel("  " + detalle.getConcepto());
            lblNombre.setFont(new Font("Inter", Font.PLAIN, 13));

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
            right.setOpaque(false);

            JLabel lblPrecio = new JLabel(String.format("$%.2f", detalle.getPrecio()));
            lblPrecio.setFont(new Font("Inter", Font.BOLD, 13));
            lblPrecio.setForeground(TEAL);

            JButton btnEliminar = new JButton("✕") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(220, 53, 69));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            btnEliminar.setContentAreaFilled(false);
            btnEliminar.setBorderPainted(false);
            btnEliminar.setFocusPainted(false);
            btnEliminar.setFont(new Font("Inter", Font.BOLD, 10));
            btnEliminar.setForeground(Color.WHITE);
            btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEliminar.setPreferredSize(new Dimension(24, 22));
            btnEliminar.addActionListener(e -> {
                listaDetalles.remove(index);
                renderizarLista();
                if (onListChanged != null) onListChanged.run();
            });

            right.add(lblPrecio);
            right.add(btnEliminar);

            item.add(lblNombre, BorderLayout.CENTER);
            item.add(right, BorderLayout.EAST);
            panelItems.add(item);
        }
        panelItems.revalidate();
        panelItems.repaint();
    }

    /** Carga una lista existente de detalles (para editar). */
    public void setDetalles(List<DetalleOrdenModelo> detalles) {
        this.listaDetalles = new ArrayList<>(detalles);
        renderizarLista();
    }

    public List<DetalleOrdenModelo> getDetalles() {
        return listaDetalles;
    }

    /** Calcula la suma de todos los precios de la lista. */
    public double calcularSubtotal() {
        double sub = 0;
        for (DetalleOrdenModelo d : listaDetalles) {
            sub += d.getPrecio();
        }
        return sub;
    }

    public void limpiar() {
        listaDetalles.clear();
        renderizarLista();
        txtConcepto.setText("");
        txtPrecio.setText("0.00");
    }
}
