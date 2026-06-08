package controllers;

//Importaciones de iText para generar PDFs
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;

import models.VehiculoModelo;
import models.ClienteModelo;
import views.VehiculosDialog;
import views.VehiculosVista;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controlador de la sección "Vehículos".
 * Maneja la lista de vehículos en memoria y las operaciones CRUD.
 * 
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
            try { anio = Integer.parseInt(dialog.getAnioText()); } catch (NumberFormatException ex) {    }

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
                Font subHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
                Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
                Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

                document.add(new Paragraph("TALLER MECANICO UABCS - Ficha de Vehiculo", titleFont));
                document.add(new Paragraph("\n"));
                
                // Datos del Vehículo
                document.add(new Paragraph("DATOS DEL VEHICULO", headerFont));
                document.add(new Paragraph("Marca: " + v.getMarca(), textFont));
                document.add(new Paragraph("Modelo: " + v.getModelo(), textFont));
                document.add(new Paragraph("Año: " + v.getAnio(), textFont));
                document.add(new Paragraph("Placas: " + v.getPlacas(), textFont));
                document.add(new Paragraph("Número de Serie: " + (v.getNumeroSerie() != null ? v.getNumeroSerie() : "N/A"), textFont));
                
                // Historial de Órdenes y Refacciones
                document.add(new Paragraph("\n────────────────────────────────────────", textFont));
                document.add(new Paragraph("HISTORIAL DE SERVICIOS Y REFACCIONES", headerFont));
                
                java.util.List<models.OrdenServicioModelo> ordenes = models.OrdenServicioModelo.obtenerPorVehiculo(v.getId());
                if (ordenes.isEmpty()) {
                    document.add(new Paragraph("  El vehiculo no tiene historial de ordenes de servicio.", textFont));
                } else {
                    for (models.OrdenServicioModelo o : ordenes) {
                        document.add(new Paragraph("\nOrden ID: " + o.getId() + " - Fecha: " + o.getFechaIngreso() + " - Estado: " + o.getEstado(), subHeaderFont));
                        document.add(new Paragraph("Falla Reportada: " + (o.getFallaReportada() != null && !o.getFallaReportada().isEmpty() ? o.getFallaReportada() : "N/A"), textFont));
                        
                        java.util.List<models.DetalleOrdenModelo> detalles = models.DetalleOrdenModelo.obtenerPorOrden(o.getId());
                        if (detalles.isEmpty()) {
                            document.add(new Paragraph("  - Sin refacciones registradas para esta orden", smallFont));
                        } else {
                            document.add(new Paragraph("  Refacciones utilizadas:", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
                            for (models.DetalleOrdenModelo det : detalles) {
                                document.add(new Paragraph("    - " + det.getConcepto() + "    $" + String.format(java.util.Locale.US, "%.2f", det.getPrecio()), textFont));
                            }
                        }
                    }
                }
                document.add(new Paragraph("\n────────────────────────────────────────", textFont));

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
