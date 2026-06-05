package controllers;

//Importaciones de iText para generar PDFs
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;

import models.OrdenServicioModelo;
import views.OrdenesServicioVista;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controlador de la sección "Órdenes de servicio".
 * Maneja la lista de órdenes en memoria y las operaciones CRUD.
 */
public class OrdenesServicioControlador {

    private OrdenesServicioVista vista;
    private List<OrdenServicioModelo> todasLasOrdenes;
    private List<OrdenServicioModelo> ordenesFiltradas;
    private List<OrdenServicioModelo> ordenesMostradas;
    
    private int paginaActual = 1;
    private int elementosPorPagina = 8;

    public OrdenesServicioControlador(OrdenesServicioVista vista) {
        this.vista = vista;
        this.todasLasOrdenes = OrdenServicioModelo.obtenerTodas();
        this.ordenesFiltradas = new ArrayList<>(this.todasLasOrdenes);
        this.ordenesMostradas = new ArrayList<>();
        
        inicializarEventos();
        actualizarVista();
    }

    private void inicializarEventos() {
        vista.setAccionListener(new OrdenesServicioVista.AccionListener() {
            @Override
            public void onEditar(int row) {
                editarOrden(row);
            }

            @Override
            public void onDescargarPDF(int row) {
                descargarPDF(row);
            }

            @Override
            public void onEliminar(int row) {
                eliminarOrden(row);
            }
        });

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
            ordenesFiltradas = new ArrayList<>(todasLasOrdenes);
        } else {
            ordenesFiltradas = new ArrayList<>();
            for (OrdenServicioModelo o : todasLasOrdenes) {
                if ((o.getNombreCliente() != null && o.getNombreCliente().toLowerCase().contains(texto)) ||
                    (o.getVehiculoRelacionado() != null && o.getVehiculoRelacionado().toLowerCase().contains(texto)) ||
                    (o.getEstado() != null && o.getEstado().toLowerCase().contains(texto)) ||
                    String.valueOf(o.getId()).toLowerCase().contains(texto)) {
                    ordenesFiltradas.add(o);
                }
            }
        }
        paginaActual = 1;
        actualizarVista();
    }

    private void actualizarVista() {
        int totalElementos = ordenesFiltradas.size();
        int totalPaginas = (int) Math.ceil((double) totalElementos / elementosPorPagina);
        if (totalPaginas == 0) totalPaginas = 1;
        
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        int inicio = (paginaActual - 1) * elementosPorPagina;
        int fin = Math.min(inicio + elementosPorPagina, totalElementos);

        ordenesMostradas = new ArrayList<>(ordenesFiltradas.subList(inicio, fin));
        vista.setOrdenes(ordenesMostradas);
        vista.actualizarPaginacion(paginaActual, totalPaginas);
    }

    private java.util.function.Consumer<OrdenServicioModelo> onEditarOrdenReq;

    public void setOnEditarOrdenReq(java.util.function.Consumer<OrdenServicioModelo> listener) {
        this.onEditarOrdenReq = listener;
    }

    /**
     * Avisa al dashboard para abrir la vista de editar.
     */
    private void editarOrden(int row) {
        if (row < 0 || row >= ordenesMostradas.size()) return;
        OrdenServicioModelo o = ordenesMostradas.get(row);
        if (onEditarOrdenReq != null) {
            onEditarOrdenReq.accept(o);
        }
    }

    /**
     * Elimina una orden después de pedir confirmación.
     */
    private void eliminarOrden(int row) {
        if (row < 0 || row >= ordenesMostradas.size()) return;
        int r = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar esta orden?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            OrdenServicioModelo o = ordenesMostradas.get(row);
            if (OrdenServicioModelo.eliminar(o.getId())) {
                todasLasOrdenes.remove(o);
                buscar();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar la orden de la base de datos.");
            }
        }
    }

    /**
     * Genera un PDF con la información completa de la orden usando iText.
     * Incluye desglose de refacciones, servicios, impuesto y total.
     */
    private void descargarPDF(int row) {
        if (row < 0 || row >= ordenesMostradas.size()) return;
        OrdenServicioModelo o = ordenesMostradas.get(row);
        String fileName = "Orden_" + o.getId() + "_" + o.getNombreCliente().replace(" ", "_") + ".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(0, 80, 100));

            // --- Encabezado ---
            document.add(new Paragraph("TALLER MECANICO UABCS - Orden de Servicio", titleFont));
            document.add(new Paragraph("\n"));

            // --- Datos de la Orden ---
            document.add(new Paragraph("DATOS DE LA ORDEN", headerFont));
            document.add(new Paragraph("ID: " + o.getId(), textFont));
            document.add(new Paragraph("Cliente: " + o.getNombreCliente(), textFont));
            document.add(new Paragraph("Vehiculo: " + o.getVehiculoRelacionado(), textFont));
            document.add(new Paragraph("Fecha Ingreso: " + o.getFechaIngreso(), textFont));
            document.add(new Paragraph("Fecha Entrega Estimada: " + (o.getFechaEntregaEstimada() != null ? o.getFechaEntregaEstimada() : "N/A"), textFont));
            document.add(new Paragraph("Estado: " + o.getEstado(), textFont));

            // --- Datos del Vehiculo ---
            document.add(new Paragraph("\nDATOS DEL VEHICULO", headerFont));
            document.add(new Paragraph("Tipo de Requerimiento: " + (o.getTipoRequerimiento() != null ? o.getTipoRequerimiento() : "N/A"), textFont));
            document.add(new Paragraph("Kilometraje: " + o.getKilometraje() + " Kms", textFont));
            document.add(new Paragraph("Nivel de Combustible: " + (o.getNivelCombustible() != null ? o.getNivelCombustible() : "N/A"), textFont));
            if (o.getFallaReportada() != null && !o.getFallaReportada().isEmpty()) {
                document.add(new Paragraph("Falla Reportada: " + o.getFallaReportada(), textFont));
            }

            // --- Detalle de Refacciones ---
            document.add(new Paragraph("\nDETALLE DE REFACCIONES", headerFont));
            java.util.List<models.DetalleOrdenModelo> detalles = models.DetalleOrdenModelo.obtenerPorOrden(o.getId());
            if (detalles.isEmpty()) {
                document.add(new Paragraph("  Sin refacciones registradas", smallFont));
            } else {
                for (models.DetalleOrdenModelo det : detalles) {
                    document.add(new Paragraph("  - " + det.getConcepto() + "    $" + String.format(java.util.Locale.US, "%.2f", det.getPrecio()), textFont));
                }
            }

            // --- Desglose de Costos ---
            document.add(new Paragraph("\n────────────────────────────────────────", textFont));
            document.add(new Paragraph("DESGLOSE DE COSTOS", headerFont));
            document.add(new Paragraph("Refacciones:              $" + String.format(java.util.Locale.US, "%.2f", o.getCostoRefacciones()), textFont));
            document.add(new Paragraph("Servicios (Mano de Obra): $" + String.format(java.util.Locale.US, "%.2f", o.getCostoManoObra()), textFont));
            document.add(new Paragraph("Subtotal:                 $" + String.format(java.util.Locale.US, "%.2f", o.getSubtotal()), textFont));
            document.add(new Paragraph("Impuesto:                 $" + String.format(java.util.Locale.US, "%.2f", o.getImpuesto()), textFont));
            document.add(new Paragraph("────────────────────────────────────────", textFont));
            document.add(new Paragraph("TOTAL:                    $" + String.format(java.util.Locale.US, "%.2f", o.getMontoTotal()), totalFont));

            document.close();
            JOptionPane.showMessageDialog(vista, "Historial guardado exitosamente como: " + fileName, "PDF Creado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Agrega una nueva orden a la lista (se llama desde el controlador de "Crear Orden").
     */
    public void agregarOrden(OrdenServicioModelo orden) {
        if (orden.guardar()) {
            // La guardamos en BD y la agregamos a la lista en memoria
            todasLasOrdenes.add(orden);
            buscar();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar la orden en la base de datos.");
        }
    }

    public OrdenesServicioVista getVista() { return vista; }
    
    /**
     * Refresca la tabla con los datos actuales.
     */
    public void refrescarTabla() {
        // Recargar desde la BD en lugar de solo setear la lista
        this.todasLasOrdenes = OrdenServicioModelo.obtenerTodas();
        buscar();
    }
}
