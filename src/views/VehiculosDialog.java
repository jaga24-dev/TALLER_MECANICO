package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import models.ClienteModelo;
import models.VehiculoModelo;

public class VehiculosDialog extends JDialog {

    private static final Color BG_DARK = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#FFB800");
    private static final Color BG_LIGHT = Color.decode("#EBF0F5");

    private ClienteModelo cliente;
    private JTable tablaVehiculos;
    private DefaultTableModel tableModel;

    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtAnio;
    private JTextField txtPlacas;

    public VehiculosDialog(Window owner, ClienteModelo cliente) {
        super(owner, "Vehículos - " + cliente.getNombreCompleto(), ModalityType.APPLICATION_MODAL);
        this.cliente = cliente;

        setSize(500, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabla de vehículos
        tableModel = new DefaultTableModel(new String[]{"ID", "Marca", "Modelo", "Año", "Placas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaVehiculos = new JTable(tableModel);
        tablaVehiculos.removeColumn(tablaVehiculos.getColumnModel().getColumn(0)); // Ocultar ID
        JScrollPane scrollPane = new JScrollPane(tablaVehiculos);
        scrollPane.setPreferredSize(new Dimension(450, 150));
        
        cargarVehiculos();

        JPanel tablaPanel = new JPanel(new BorderLayout());
        tablaPanel.setBackground(BG_LIGHT);
        tablaPanel.add(new JLabel("Vehículos Registrados:"), BorderLayout.NORTH);
        tablaPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarVehiculoSeleccionado());
        tablaPanel.add(btnEliminar, BorderLayout.SOUTH);

        // Formulario para agregar vehículo
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        formPanel.setBackground(BG_LIGHT);
        formPanel.setBorder(BorderFactory.createTitledBorder("Agregar Nuevo Vehículo"));

        formPanel.add(new JLabel("Marca:"));
        txtMarca = new JTextField();
        formPanel.add(txtMarca);

        formPanel.add(new JLabel("Modelo:"));
        txtModelo = new JTextField();
        formPanel.add(txtModelo);

        formPanel.add(new JLabel("Año:"));
        txtAnio = new JTextField();
        formPanel.add(txtAnio);

        formPanel.add(new JLabel("Placas:"));
        txtPlacas = new JTextField();
        formPanel.add(txtPlacas);

        JButton btnAgregar = new JButton("Agregar Vehículo");
        btnAgregar.setBackground(GOLD);
        btnAgregar.setForeground(BG_DARK);
        btnAgregar.addActionListener(e -> agregarVehiculo());
        formPanel.add(new JLabel("")); // Spacer
        formPanel.add(btnAgregar);

        mainPanel.add(tablaPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_LIGHT);
        btnPanel.add(btnCerrar);
        
        add(mainPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void cargarVehiculos() {
        tableModel.setRowCount(0);
        for (VehiculoModelo v : cliente.getVehiculos()) {
            tableModel.addRow(new Object[]{v.getId(), v.getMarca(), v.getModelo(), v.getAnio(), v.getPlacas()});
        }
    }

    private void agregarVehiculo() {
        if (txtMarca.getText().trim().isEmpty() || txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Marca y Modelo son obligatorios.");
            return;
        }

        int anio = 0;
        try {
            anio = Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Año inválido.");
            return;
        }

        VehiculoModelo v = new VehiculoModelo(
                UUID.randomUUID().toString(),
                txtMarca.getText().trim(),
                txtModelo.getText().trim(),
                anio,
                txtPlacas.getText().trim()
        );

        cliente.agregarVehiculo(v);
        cargarVehiculos();
        
        txtMarca.setText("");
        txtModelo.setText("");
        txtAnio.setText("");
        txtPlacas.setText("");
    }

    private void eliminarVehiculoSeleccionado() {
        int row = tablaVehiculos.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0); // La columna 0 tiene el ID aunque esté oculta
            cliente.getVehiculos().removeIf(v -> v.getId().equals(id));
            cargarVehiculos();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la tabla para eliminar.");
        }
    }
}
