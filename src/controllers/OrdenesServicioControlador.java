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
     this.ordenes = new ArrayList<>();

     cargarDatosPrueba();
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

 /**
  * Abre un formulario para editar la orden de servicio.
  */
 private void editarOrden(int row) {
     if (row < 0 || row >= ordenes.size()) return;
     OrdenServicioModelo o = ordenes.get(row);

     JTextField txtCliente = new JTextField(o.getNombreCliente());
     JTextField txtVehiculo = new JTextField(o.getVehiculoRelacionado());
     JTextField txtIngreso = new JTextField(o.getFechaIngreso());
     JTextField txtEntrega = new JTextField(o.getFechaEntrega());
     JTextField txtManoObra = new JTextField(String.valueOf(o.getCostoManoObra()));
     JTextField txtRefacciones = new JTextField(String.valueOf(o.getCostoRefacciones()));
     JTextField txtTotal = new JTextField(String.valueOf(o.getMontoTotal()));
     JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"EN ESPERA", "EN REPARACIÓN", "LISTO"});
     cmbEstado.setSelectedItem(o.getEstado());

     Object[] campos = {
             "Cliente:", txtCliente,
             "Vehículo:", txtVehiculo,
             "Fecha Ingreso:", txtIngreso,
             "Fecha Entrega:", txtEntrega,
             "Costo Mano de Obra:", txtManoObra,
             "Costo Refacciones:", txtRefacciones,
             "Monto Total + IVA:", txtTotal,
             "Estado:", cmbEstado
     };

     int resultado = JOptionPane.showConfirmDialog(vista, campos, "Editar Orden de Servicio",
             JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

     if (resultado == JOptionPane.OK_OPTION) {
         o.setNombreCliente(txtCliente.getText().trim());
         o.setVehiculoRelacionado(txtVehiculo.getText().trim());
         o.setFechaIngreso(txtIngreso.getText().trim());
         o.setFechaEntrega(txtEntrega.getText().trim());
         try { o.setCostoManoObra(Double.parseDouble(txtManoObra.getText().trim())); } catch (NumberFormatException ex) { /* ignorar */ }
         try { o.setCostoRefacciones(Double.parseDouble(txtRefacciones.getText().trim())); } catch (NumberFormatException ex) { /* ignorar */ }
         try { o.setMontoTotal(Double.parseDouble(txtTotal.getText().trim())); } catch (NumberFormatException ex) { /* ignorar */ }
         o.setEstado((String) cmbEstado.getSelectedItem());
         vista.setOrdenes(ordenes);
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
         ordenes.remove(row);
         vista.setOrdenes(ordenes);
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

         Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
         Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
         Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

         document.add(new Paragraph("TALLER MECÁNICO UABCS - Orden de Servicio", titleFont));
         document.add(new Paragraph("\n"));
         document.add(new Paragraph("DATOS DE LA ORDEN", headerFont));
         document.add(new Paragraph("ID: " + o.getId(), textFont));
         document.add(new Paragraph("Cliente: " + o.getNombreCliente(), textFont));
         document.add(new Paragraph("Vehículo: " + o.getVehiculoRelacionado(), textFont));
         document.add(new Paragraph("Fecha Ingreso: " + o.getFechaIngreso(), textFont));
         document.add(new Paragraph("Fecha Entrega: " + o.getFechaEntrega(), textFont));
         document.add(new Paragraph("\nCOSTOS", headerFont));
         document.add(new Paragraph("Mano de Obra: $" + String.format("%.2f", o.getCostoManoObra()), textFont));
         document.add(new Paragraph("Refacciones: $" + String.format("%.2f", o.getCostoRefacciones()), textFont));
         document.add(new Paragraph("Monto Total (IVA): $" + String.format("%.2f", o.getMontoTotal()), textFont));
         document.add(new Paragraph("\nEstado: " + o.getEstado(), textFont));

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
     ordenes.add(orden);
     vista.setOrdenes(ordenes);
 }

 public OrdenesServicioVista getVista() { return vista; }

 private void cargarDatosPrueba() {
     ordenes.add(new OrdenServicioModelo(
             "ORD-001", "S.De Anda", "Ford Explorer",
             "31/3/2026", "4/4/2026",
             300.00, 500.00, 820.00, "LISTO"
     ));
 }
}
