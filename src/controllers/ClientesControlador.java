package controllers;




import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//Importaciones de iText (es necesario agregar itextpdf-5.5.13.3.jar al classpath)
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

import models.VehiculoModelo;
import models.ClienteModelo;
import views.ClienteFormDialog;
import views.ClientesVista;
import views.VehiculosDialog;

public class ClientesControlador {

 private ClientesVista vista;
 private List<ClienteModelo> clientes;

 public ClientesControlador(ClientesVista vista) {
     this.vista = vista;
     this.clientes = new ArrayList<>();
     cargarDatosPrueba(); // Solo para prototipo
     
     this.vista.setClientes(clientes);
     inicializarEventos();
 }

 private void inicializarEventos() {
     // Cuando se haga clic en "Agregar cliente", llamamos a la función agregarCliente()
     vista.getBtnAgregar().addActionListener(e -> agregarCliente());

     // Escuchamos los clics de la tabla de la vista
     vista.setAccionListener(new ClientesVista.AccionListener() {
         @Override
         public void onEditar(int row) {
             editarCliente(row); // Llama a la función de editar
         }

         @Override
         public void onDescargarPDF(int row) {
             descargarPDF(row); // Llama a la función de PDF
         }

         @Override
         public void onEliminar(int row) {
             eliminarCliente(row); // Llama a la función de eliminar
         }

         @Override
         public void onVerVehiculos(int row) {
             verVehiculos(row); // Llama a la función de ver vehículos
         }
     });
 }

 /**
  * Abre la ventana para crear un nuevo cliente y lo añade a la lista si se guarda.
  */
 private void agregarCliente() {
     ClienteFormDialog dialog = new ClienteFormDialog(SwingUtilities.getWindowAncestor(vista), null);
     dialog.setVisible(true);

     if (dialog.isGuardado()) {
         ClienteModelo nuevo = dialog.getCliente();
         // Generar ID dummy
         nuevo.setId(String.format("%03d", clientes.size() + 1));
         clientes.add(nuevo);
         vista.setClientes(clientes);
     }
 }

 /**
  * Abre la ventana de edición con los datos del cliente seleccionado.
  */
 private void editarCliente(int row) {
     if (row >= 0 && row < clientes.size()) {
         ClienteModelo cliente = clientes.get(row); // Obtenemos el cliente de la lista
         // Creamos el diálogo pasándole el cliente actual
         ClienteFormDialog dialog = new ClienteFormDialog(SwingUtilities.getWindowAncestor(vista), cliente);
         dialog.setVisible(true); // Mostramos la ventana

         // Si el usuario apretó "Guardar", actualizamos la tabla
         if (dialog.isGuardado()) {
             vista.setClientes(clientes); // Refrescar tabla
         }
     }
 }

 /**
  * Pregunta al usuario si desea eliminar y borra al cliente de la lista.
  */
 private void eliminarCliente(int row) {
     if (row >= 0 && row < clientes.size()) {
         int r = JOptionPane.showConfirmDialog(vista, "¿Está seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
         if (r == JOptionPane.YES_OPTION) {
             clientes.remove(row);
             vista.setClientes(clientes);
         }
     }
 }

 /**
  * Muestra la ventana de gestión de vehículos del cliente seleccionado.
  */
 private void verVehiculos(int row) {
     if (row >= 0 && row < clientes.size()) {
         ClienteModelo cliente = clientes.get(row); // Sacamos al cliente
         // Creamos y mostramos el diálogo de vehículos
         VehiculosDialog dialog = new VehiculosDialog(SwingUtilities.getWindowAncestor(vista), cliente);
         dialog.setVisible(true);
         
         // Refrescamos la tabla principal por si cambió la cantidad de vehículos (el resumen)
         vista.setClientes(clientes); 
     }
 }

 /**
  * Genera un archivo PDF con la información del cliente usando la librería iText.
  */
 private void descargarPDF(int row) {
     if (row >= 0 && row < clientes.size()) {
         ClienteModelo cliente = clientes.get(row);
         String fileName = "Historial_" + cliente.getNombreCompleto().replace(" ", "_") + ".pdf";
         
         try {
             Document document = new Document();
             PdfWriter.getInstance(document, new FileOutputStream(fileName));
             document.open();

             Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
             Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
             Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

             document.add(new Paragraph("TALLER MECÁNICO UABCS - Historial de Cliente", titleFont));
             document.add(new Paragraph("\n"));
             
             document.add(new Paragraph("DATOS DEL CLIENTE", headerFont));
             document.add(new Paragraph("ID: " + cliente.getId(), textFont));
             document.add(new Paragraph("Nombre: " + cliente.getNombreCompleto(), textFont));
             document.add(new Paragraph("Teléfono: " + cliente.getTelefono(), textFont));
             document.add(new Paragraph("Correo: " + cliente.getCorreo(), textFont));
             
             document.add(new Paragraph("\nVEHÍCULOS", headerFont));
             if (cliente.getVehiculos().isEmpty()) {
                 document.add(new Paragraph("No tiene vehículos registrados.", textFont));
             } else {
                 for (VehiculoModelo v : cliente.getVehiculos()) {
                     document.add(new Paragraph("- " + v.getMarca() + " " + v.getModelo() + " (" + v.getAnio() + ") Placas: " + v.getPlacas(), textFont));
                 }
             }

             document.close();
             JOptionPane.showMessageDialog(vista, "Historial guardado exitosamente como: " + fileName, "PDF Creado", JOptionPane.INFORMATION_MESSAGE);

         } catch (Exception e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(vista, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         }
     }
 }

 public ClientesVista getVista() {
     return vista;
 }

 private void cargarDatosPrueba() {
     ClienteModelo c1 = new ClienteModelo("001", "Santiago De Anda", "6121112233", "sade_24@uabcs.mx");
     c1.agregarVehiculo(new VehiculoModelo("v1", "Ford", "Explorer", 2012, "BCS-123"));
     clientes.add(c1);
 }
}
