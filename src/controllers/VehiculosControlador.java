package controllers;

//Importaciones de iText para generar PDFs
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;


import models.VehiculoModelo;
import views.VehiculosDialog;
import views.VehiculosVista;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Controlador de la sección "Vehículos".
 * Maneja la lista de vehículos en memoria y las operaciones CRUD.
 * 
 * NOTA PARA NOVATOS:
 * Este controlador conecta la vista (lo que ve el usuario) con los datos (modelos).
 * Cuando el usuario hace clic en un botón, el controlador decide qué hacer.
 */
public class VehiculosControlador {

    private VehiculosVista vista;             // La pantalla que muestra la tabla
    private List<VehiculoModelo> vehiculos;   // La lista de vehículos en memoria

    public VehiculosControlador(VehiculosVista vista) {
        this.vista = vista;
        this.vehiculos = models.VehiculoModelo.obtenerTodos();
        
        // Mostrar los datos en la tabla
        this.vista.setVehiculos(vehiculos);
        
        // Conectar los botones con sus funciones
        inicializarEventos();
    }

    /**
     * Conecta cada botón de la vista con la función correspondiente del controlador.
     */
    private void inicializarEventos() {
        // Botón "Agregar Vehículo nuevo"
        vista.getBtnAgregar().addActionListener(e -> agregarVehiculo());

        // Botones de acción en la tabla (Editar, PDF, Eliminar)
        vista.setAccionListener(new VehiculosVista.AccionListener() {
            @Override
            public void onEditar(int row) {
                editarVehiculo(row);
            }

            @Override
            public void onDescargarPDF(int row) {
                descargarPDF(row);
            }

            @Override
            public void onEliminar(int row) {
                eliminarVehiculo(row);
            }
        });
    }

    /**
     * Abre el formulario estilizado para agregar un nuevo vehículo.
     */
    private void agregarVehiculo() {
        List<models.ClienteModelo> clientes = models.ClienteModelo.obtenerTodos();
    	VehiculosDialog dialog = new VehiculosDialog(SwingUtilities.getWindowAncestor(vista), null, clientes);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            int anio = 0;
            try { anio = Integer.parseInt(dialog.getAnioText()); } catch (NumberFormatException ex) { /* ignorar */ }

            VehiculoModelo nuevo = new VehiculoModelo(
                    null, // ID generado por la BD
                    dialog.getIdClienteSeleccionado(),  // ID del cliente real seleccionado
                    dialog.getMarca(),
                    dialog.getModelo(),
                    anio,
                    dialog.getPlacas(),
                    dialog.getNumSerie(),
                    dialog.getRutaImagen()
            );

            if (nuevo.guardar()) {
                vehiculos.add(nuevo);
                vista.setVehiculos(vehiculos); // Refrescar la tabla
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el vehículo en la base de datos.");
            }
        }
    }

    /**
     * Abre el formulario estilizado para editar un vehículo existente.
     */
    private void editarVehiculo(int row) {
        if (row < 0 || row >= vehiculos.size()) return;
        VehiculoModelo v = vehiculos.get(row);

        List<models.ClienteModelo> clientes = models.ClienteModelo.obtenerTodos();
        VehiculosDialog dialog = new VehiculosDialog(SwingUtilities.getWindowAncestor(vista), v, clientes);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            v.setIdCliente(dialog.getIdClienteSeleccionado());
            if (v.actualizar()) {
                vista.setVehiculos(vehiculos);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el vehículo.");
            }
        }
    }

    /**
     * Elimina un vehículo después de pedir confirmación.
     */
    private void eliminarVehiculo(int row) {
        if (row < 0 || row >= vehiculos.size()) return;
        int r = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este vehículo?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            VehiculoModelo v = vehiculos.get(row);
            if (VehiculoModelo.eliminar(v.getId())) {
                vehiculos.remove(row);
                vista.setVehiculos(vehiculos);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el vehículo.");
            }
        }
    }

    /**
     * Genera un PDF con la información del vehículo usando iText.
     */
    private void descargarPDF(int row) {
        if (row < 0 || row >= vehiculos.size()) return;
        VehiculoModelo v = vehiculos.get(row);
        String fileName = "Vehiculo_" + v.getMarca() + "_" + v.getModelo() + ".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("TALLER MECÁNICO UABCS - Ficha de Vehículo", titleFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("DATOS DEL VEHÍCULO", headerFont));
            document.add(new Paragraph("Marca: " + v.getMarca(), textFont));
            document.add(new Paragraph("Modelo: " + v.getModelo(), textFont));
            document.add(new Paragraph("Año: " + v.getAnio(), textFont));
            document.add(new Paragraph("Placas: " + v.getPlacas(), textFont));
            document.add(new Paragraph("Número de Serie: " + (v.getNumeroSerie() != null ? v.getNumeroSerie() : "N/A"), textFont));
            document.add(new Paragraph("\nFALLA REPORTADA", headerFont));
            document.add(new Paragraph("Consultar órdenes de servicio para fallas.", textFont));
            document.add(new Paragraph("\nEstado: N/A", textFont));

            document.close();
            JOptionPane.showMessageDialog(vista,
                    "PDF guardado como: " + fileName, "PDF Creado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista,
                    "Error al generar PDF: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public VehiculosVista getVista() { return vista; }
    
    public void refrescarTabla() {
        this.vehiculos = models.VehiculoModelo.obtenerTodos();
        this.vista.setVehiculos(this.vehiculos);
    }
}
