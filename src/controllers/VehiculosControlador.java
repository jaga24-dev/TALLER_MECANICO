package controllers;

//Importaciones de iText para generar PDFs
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;

import models.ClienteModelo;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controlador de la sección "Vehículos".
 * Maneja la lista de vehículos en memoria y las operaciones CRUD.
 * 
 * NOTA PARA NOVATOS:
 * Este controlador conecta la vista (lo que ve el usuario) con los datos (modelos).
 * Cuando el usuario hace clic en un botón, el controlador decide qué hacer.
 */
public class VehiculosControlador {

    private VehiculosVista vista;
    private List<VehiculoModelo> todosLosVehiculos;
    private List<VehiculoModelo> vehiculosFiltrados;
    private List<VehiculoModelo> vehiculosMostrados;
    
    private int paginaActual = 1;
    private int elementosPorPagina = 8;

    public VehiculosControlador(VehiculosVista vista) {
        this.vista = vista;
        this.todosLosVehiculos = VehiculoModelo.obtenerTodos();
        this.vehiculosFiltrados = new ArrayList<>(this.todosLosVehiculos);
        this.vehiculosMostrados = new ArrayList<>();
        
        inicializarEventos();
        actualizarVista();
    }

    /**
     * Conecta cada botón de la vista con la función correspondiente del controlador.
     */
    private void inicializarEventos() {
        // Escuchar clics en los botones de acción de la tabla
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

        // Escuchar botón Agregar nuevo vehículo
        vista.getBtnAgregar().addActionListener(e -> agregarVehiculo());

        // Paginación
        vista.setPaginacionListener(nuevaPagina -> {
            paginaActual = nuevaPagina;
            actualizarVista();
        });

        // Búsqueda
        vista.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { buscar(); }
            @Override public void removeUpdate(DocumentEvent e) { buscar(); }
            @Override public void changedUpdate(DocumentEvent e) { buscar(); }
        });
    }

    private void buscar() {
        String texto = vista.getTxtBuscar().getText().toLowerCase().trim();
        if (texto.isEmpty()) {
            vehiculosFiltrados = new ArrayList<>(todosLosVehiculos);
        } else {
            vehiculosFiltrados = new ArrayList<>();
            for (VehiculoModelo v : todosLosVehiculos) {
                if (v.getMarca().toLowerCase().contains(texto) ||
                    v.getModelo().toLowerCase().contains(texto) ||
                    v.getPlacas().toLowerCase().contains(texto) ||
                    String.valueOf(v.getAnio()).contains(texto)) {
                    vehiculosFiltrados.add(v);
                }
            }
        }
        paginaActual = 1;
        actualizarVista();
    }

    private void actualizarVista() {
        int totalElementos = vehiculosFiltrados.size();
        int totalPaginas = (int) Math.ceil((double) totalElementos / elementosPorPagina);
        if (totalPaginas == 0) totalPaginas = 1;
        
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        int inicio = (paginaActual - 1) * elementosPorPagina;
        int fin = Math.min(inicio + elementosPorPagina, totalElementos);

        vehiculosMostrados = new ArrayList<>(vehiculosFiltrados.subList(inicio, fin));
        vista.setVehiculos(vehiculosMostrados);
        vista.actualizarPaginacion(paginaActual, totalPaginas);
    }

    /**
     * Abre el formulario estilizado para agregar un nuevo vehículo.
     */
    private void agregarVehiculo() {
        List<ClienteModelo> clientes = ClienteModelo.obtenerTodos();
    	VehiculosDialog dialog = new VehiculosDialog(SwingUtilities.getWindowAncestor(vista), null, clientes);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            int anio = 0;
            try { anio = Integer.parseInt(dialog.getAnioText()); } catch (NumberFormatException ex) { /* ignorar */ }

            VehiculoModelo nuevo = new VehiculoModelo(
                    null, // ID generado por la BD
                    dialog.getIdClienteSeleccionado(),
                    dialog.getMarca(),
                    dialog.getModelo(),
                    anio,
                    dialog.getPlacas(),
                    dialog.getNumSerie(),
                    dialog.getRutaImagen()
            );

            if (nuevo.guardar()) {
                todosLosVehiculos.add(nuevo);
                buscar();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el vehículo en la base de datos.");
            }
        }
    }

    /**
     * Abre el formulario estilizado para editar un vehículo existente.
     */
    private void editarVehiculo(int row) {
        if (row >= 0 && row < vehiculosMostrados.size()) {
            VehiculoModelo v = vehiculosMostrados.get(row);

            List<ClienteModelo> clientes = ClienteModelo.obtenerTodos();
            VehiculosDialog dialog = new VehiculosDialog(SwingUtilities.getWindowAncestor(vista), v, clientes);
            dialog.setVisible(true);

            if (dialog.isGuardado()) {
                v.setIdCliente(dialog.getIdClienteSeleccionado());
                if (v.actualizar()) {
                    actualizarVista();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al actualizar el vehículo.");
                }
            }
        }
    }

    /**
     * Elimina un vehículo después de pedir confirmación.
     */
    private void eliminarVehiculo(int row) {
        if (row >= 0 && row < vehiculosMostrados.size()) {
            int r = JOptionPane.showConfirmDialog(vista,
                    "¿Está seguro de eliminar este vehículo?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                VehiculoModelo v = vehiculosMostrados.get(row);
                if (VehiculoModelo.eliminar(v.getId())) {
                    todosLosVehiculos.remove(v);
                    buscar();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el vehículo.");
                }
            }
        }
    }

    /**
     * Genera un PDF con la información del vehículo usando iText.
     */
    private void descargarPDF(int row) {
        if (row >= 0 && row < vehiculosMostrados.size()) {
            VehiculoModelo v = vehiculosMostrados.get(row);
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
    }

    public VehiculosVista getVista() { return vista; }
    
    public void refrescarTabla() {
        this.todosLosVehiculos = VehiculoModelo.obtenerTodos();
        buscar();
    }
}
