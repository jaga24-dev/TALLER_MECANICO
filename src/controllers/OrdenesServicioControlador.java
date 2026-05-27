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

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
* Controlador de la sección "Órdenes de servicio".
* Maneja la lista de órdenes en memoria y las operaciones CRUD.
*/
public class OrdenesServicioControlador {

 private OrdenesServicioVista vista;
 private List<OrdenServicioModelo> ordenes;

 public OrdenesServicioControlador(OrdenesServicioVista vista) {
     this.vista = vista;
     this.ordenes = OrdenServicioModelo.obtenerTodas();

     this.vista.setOrdenes(ordenes);
     inicializarEventos();
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
 }
 
 private java.util.function.Consumer<OrdenServicioModelo> onEditarOrdenReq;

 public void setOnEditarOrdenReq(java.util.function.Consumer<OrdenServicioModelo> listener) {
     this.onEditarOrdenReq = listener;
 }
 /**
  * Avisa al dashboard para abrir la vista de editar.
  */
 private void editarOrden(int row) {
     if (row < 0 || row >= ordenes.size()) return;
     OrdenServicioModelo o = ordenes.get(row);

     if (onEditarOrdenReq != null) {
         onEditarOrdenReq.accept(o);
     }
 }

 /**
  * Elimina una orden después de pedir confirmación.
  */
 private void eliminarOrden(int row) {
     if (row < 0 || row >= ordenes.size()) return;
     int r = JOptionPane.showConfirmDialog(vista,
             "¿Está seguro de eliminar esta orden?", "Confirmar",
             JOptionPane.YES_NO_OPTION);
     if (r == JOptionPane.YES_OPTION) {
         OrdenServicioModelo o = ordenes.get(row);
         if (OrdenServicioModelo.eliminar(o.getId())) {
             ordenes.remove(row);
             vista.setOrdenes(ordenes);
         } else {
             JOptionPane.showMessageDialog(vista, "Error al eliminar la orden de la base de datos.");
         }
     }
 }

 /**
  * Genera un PDF con la información de la orden usando iText.
  */
 private void descargarPDF(int row) {
     if (row < 0 || row >= ordenes.size()) return;
     OrdenServicioModelo o = ordenes.get(row);
     String fileName = "Orden_" + o.getId() + "_" + o.getNombreCliente().replace(" ", "_") + ".pdf";

     try {
         Document document = new Document();
         PdfWriter.getInstance(document, new FileOutputStream(fileName));
         document.open();

         Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
         Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
         Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
         Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

         Paragraph p1 = new Paragraph("U A B C S DEPARTAMENTO DE SISTEMAS COMPUTACIONALES", titleFont);
         p1.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
         document.add(p1);
         Paragraph p2 = new Paragraph("UNIVERSIDAD AUTÓNOMA DE BAJA CALIFORNIA SUR", subTitleFont);
         p2.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
         document.add(p2);
         document.add(new Paragraph("\n"));

         document.add(new Paragraph("Orden de servicio (Recibo generado)", headerFont));
         document.add(new Paragraph("Cliente: " + o.getNombreCliente(), textFont));
         document.add(new Paragraph("Vehículo relacionado: " + o.getVehiculoRelacionado(), textFont));
         document.add(new Paragraph("Fecha de ingreso: " + o.getFechaIngreso(), textFont));
         document.add(new Paragraph("Fecha de entrega estimada: " + (o.getFechaEntregaEstimada() != null ? o.getFechaEntregaEstimada() : "N/A"), textFont));
         
         document.add(new Paragraph("\nCostos:", headerFont));
         document.add(new Paragraph("Costo de refacciones: $" + String.format("%.2f", o.getCostoRefacciones()), textFont));
         document.add(new Paragraph("Costo de mano de obra: $" + String.format("%.2f", o.getCostoManoObra()), textFont));
         
         Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
         document.add(new Paragraph("Monto total: $" + String.format("%.2f", o.getMontoTotal()), totalFont));

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

 /**
  * Agrega una nueva orden a la lista (se llama desde el controlador de "Crear Orden").
  */
 public void agregarOrden(OrdenServicioModelo orden) {
     if (orden.guardar()) {
         // La guardamos en BD y la agregamos a la lista en memoria
         ordenes.add(orden);
         vista.setOrdenes(ordenes);
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
     this.ordenes = OrdenServicioModelo.obtenerTodas();
     vista.setOrdenes(ordenes);
 }
}
