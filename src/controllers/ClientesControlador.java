package controllers;




import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
 private List<ClienteModelo> todosLosClientes;
 private List<ClienteModelo> clientesFiltrados;
 private List<ClienteModelo> clientesMostrados;
 
 private int paginaActual = 1;
 private int elementosPorPagina = 8;


 public ClientesControlador(ClientesVista vista) {
     this.vista = vista;
     this.todosLosClientes = ClienteModelo.obtenerTodos(); // Cargar de la base de datos
     this.clientesFiltrados = new ArrayList<>(this.todosLosClientes);
     this.clientesMostrados = new ArrayList<>();
     
     inicializarEventos();
     actualizarVista();
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
         clientesFiltrados = new ArrayList<>(todosLosClientes);
     } else {
         clientesFiltrados = new ArrayList<>();
         for (ClienteModelo c : todosLosClientes) {
             if (c.getNombreCompleto().toLowerCase().contains(texto) ||
                 c.getTelefono().toLowerCase().contains(texto) ||
                 c.getCorreo().toLowerCase().contains(texto) ||
                 c.getId().toLowerCase().contains(texto)) {
                 clientesFiltrados.add(c);
             }
         }
     }
     paginaActual = 1; // Volver a la primera página tras buscar
     actualizarVista();
 }

 private void actualizarVista() {
     int totalElementos = clientesFiltrados.size();
     int totalPaginas = (int) Math.ceil((double) totalElementos / elementosPorPagina);
     if (totalPaginas == 0) totalPaginas = 1;
     
     if (paginaActual > totalPaginas) paginaActual = totalPaginas;

     int inicio = (paginaActual - 1) * elementosPorPagina;
     int fin = Math.min(inicio + elementosPorPagina, totalElementos);

     clientesMostrados = new ArrayList<>(clientesFiltrados.subList(inicio, fin));
     vista.setClientes(clientesMostrados);
     vista.actualizarPaginacion(paginaActual, totalPaginas);
 }


 /**
  * Abre la ventana para crear un nuevo cliente y lo añade a la BD.
  */
 private void agregarCliente() {
     ClienteFormDialog dialog = new ClienteFormDialog(SwingUtilities.getWindowAncestor(vista), null);
     dialog.setVisible(true);

     if (dialog.isGuardado()) {
         ClienteModelo nuevo = dialog.getCliente();
         if (nuevo.guardar()) {
        	 todosLosClientes.add(nuevo);
             buscar(); // Refresca filtros y actualiza vista
         } else {
             JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
         }
     }
 }

 /**
  * Abre la ventana de edición con los datos del cliente seleccionado y actualiza la BD.
  */
 private void editarCliente(int row) {
	 if (row >= 0 && row < clientesMostrados.size()) {
         ClienteModelo cliente = clientesMostrados.get(row); // Obtenemos el cliente de la sublista
         // Creamos el diálogo pasándole el cliente actual
         ClienteFormDialog dialog = new ClienteFormDialog(SwingUtilities.getWindowAncestor(vista), cliente);
         dialog.setVisible(true); // Mostramos la ventana

         // Si el usuario apretó "Guardar", actualizamos la BD y tabla
         if (dialog.isGuardado()) {
             if (cliente.actualizar()) {
            	 actualizarVista(); // Refrescar tabla actual
             } else {
                 JOptionPane.showMessageDialog(vista, "Error al actualizar en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 }

 /**
  * Pregunta al usuario si desea eliminar y borra al cliente de la BD.
  */
 private void eliminarCliente(int row) {
     if (row >= 0 && row < clientesMostrados.size()) {
         int r = JOptionPane.showConfirmDialog(vista, "¿Está seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
         if (r == JOptionPane.YES_OPTION) {
             ClienteModelo cliente = clientesMostrados.get(row);
             if (cliente.eliminar()) {
                 todosLosClientes.remove(cliente);
                 buscar(); // Refresca lista filtrada y pagina
             } else {
                 JOptionPane.showMessageDialog(vista, "Error al eliminar de la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 }

 /**
  * Muestra la ventana de gestión de vehículos del cliente seleccionado.
  */
 private void verVehiculos(int row) {
     if (row >= 0 && row < clientesMostrados.size()) {
         ClienteModelo cliente = clientesMostrados.get(row); // Sacamos al cliente
      // Abrimos el formulario de vehículo en modo agregar
         java.util.List<ClienteModelo> listaClientes = ClienteModelo.obtenerTodos();
         VehiculosDialog dialog = new VehiculosDialog(SwingUtilities.getWindowAncestor(vista), null, listaClientes);
         dialog.setVisible(true);
         
         if (dialog.isGuardado()) {
             int anio = 0;
             try { anio = Integer.parseInt(dialog.getAnioText()); } catch (NumberFormatException ex) { }
             VehiculoModelo nuevo = new VehiculoModelo(
                 null,
                 cliente.getId(),
                 dialog.getMarca(), dialog.getModelo(), anio, dialog.getPlacas(), 
                 java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                 dialog.getRutaImagen()
             );
             if (nuevo.guardar()) {
                 cliente.agregarVehiculo(nuevo);
                 actualizarVista();
             } else {
                 JOptionPane.showMessageDialog(vista, "Error al guardar el vehículo en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
             }
         } 
     }
 }

 /**
  * Genera un archivo PDF con la información del cliente usando la librería iText.
  */
 private void descargarPDF(int row) {
     if (row >= 0 && row < clientesMostrados.size()) {
         ClienteModelo cliente = clientesMostrados.get(row);
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
 
 public void refrescarTabla() {
     this.todosLosClientes = ClienteModelo.obtenerTodos();
     buscar();
 }
}
