package controllers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import models.ClienteModelo;
import models.OrdenServicioModelo;
import views.CrearOrdenVista;

/**
 * Controlador de la sección "Crear orden".
 * Toma los datos del formulario y los convierte en una OrdenServicioModelo.
 * 
 * NOTA PARA NOVATOS:
 * Este controlador lee todos los campos del formulario cuando el usuario
 * hace clic en "Crear Orden" y crea un objeto OrdenServicioModelo con esos datos.
 * Después, le pasa esa orden al controlador de Órdenes de Servicio para que
 * aparezca en la tabla.
 */
public class CrearOrdenControlador {

    private CrearOrdenVista vista;
    private OrdenesServicioControlador ordenesControlador; // Para agregar la nueva orden a la tabla

    private int contadorOrdenes = 1; // Para generar IDs incrementales
    private String rutaImagenSeleccionada = null;
    private List<ClienteModelo> listaClientes;

    public CrearOrdenControlador(CrearOrdenVista vista, OrdenesServicioControlador ordenesControlador) {
        this.vista = vista;
        this.ordenesControlador = ordenesControlador;

        inicializarEventos();
    }

    private void inicializarEventos() {
        cargarClientes();
        
        // Cuando cambie el cliente, actualizar vehículos
        vista.getCmbClientes().addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                actualizarVehiculosPorCliente();
            }
        });
        
        // Cuando el usuario hace clic en "Crear Orden"
        vista.getBtnCrearOrden().addActionListener(e -> crearOrden());
        
        // Botón subir imagen
        vista.getBtnSubirImagen().addActionListener(e -> seleccionarImagen());
        
        // Cálculo automático de total
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcularTotalAutomatico(); }
            public void removeUpdate(DocumentEvent e) { calcularTotalAutomatico(); }
            public void changedUpdate(DocumentEvent e) { calcularTotalAutomatico(); }
        };
        vista.getTxtCostoRefacciones().getDocument().addDocumentListener(docListener);
        vista.getTxtCostoServicios().getDocument().addDocumentListener(docListener);
        vista.getTxtImpuesto().getDocument().addDocumentListener(docListener);
        
        // Listener para PanelListaServicios (calcula total de refacciones)
        vista.getPanelListaServicios().setOnListChanged(() -> {
            double totalRefacciones = vista.getPanelListaServicios().calcularSubtotal();
            vista.getTxtCostoRefacciones().setText(String.format(java.util.Locale.US, "%.2f", totalRefacciones));
        });
    }

    private void cargarClientes() {
        listaClientes = ClienteModelo.obtenerTodos();
        vista.getCmbClientes().removeAllItems();
        for (ClienteModelo c : listaClientes) {
            vista.getCmbClientes().addItem(c.getNombreCompleto() + " - " + c.getCorreo());
        }
        
        // Autocompletado simple
        JTextField editor = (JTextField) vista.getCmbClientes().getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                // Ignorar teclas de navegación
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN || 
                    e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_LEFT || 
                    e.getKeyCode() == KeyEvent.VK_RIGHT) return;
                
                String texto = editor.getText();
                vista.getCmbClientes().hidePopup();
                vista.getCmbClientes().removeAllItems();
                if (texto.isEmpty()) {
                    for (ClienteModelo c : listaClientes) vista.getCmbClientes().addItem(c.getNombreCompleto() + " - " + c.getCorreo());
                } else {
                    for (ClienteModelo c : listaClientes) {
                        String item = c.getNombreCompleto() + " - " + c.getCorreo();
                        if (item.toLowerCase().contains(texto.toLowerCase())) {
                            vista.getCmbClientes().addItem(item);
                        }
                    }
                }
                editor.setText(texto);
                vista.getCmbClientes().showPopup();
            }
        });
        
     // Asegurar que al hacer clic en la flecha, se muestren todos los clientes
        vista.getCmbClientes().addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                String textoActual = editor.getText();
                vista.getCmbClientes().removeAllItems();
                for (ClienteModelo c : listaClientes) {
                    vista.getCmbClientes().addItem(c.getNombreCompleto() + " - " + c.getCorreo());
                }
                editor.setText(textoActual);
            }
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
        
        actualizarVehiculosPorCliente();
    }

    private void actualizarVehiculosPorCliente() {
        vista.getCmbVehiculos().removeAllItems();
        Object itemSeleccionado = vista.getCmbClientes().getSelectedItem();
        if (itemSeleccionado != null && listaClientes != null) {
            String seleccionadoStr = itemSeleccionado.toString();
            for (ClienteModelo c : listaClientes) {
                String itemStr = c.getNombreCompleto() + " - " + c.getCorreo();
                if (itemStr.equals(seleccionadoStr)) {
                    List<models.VehiculoModelo> vehiculos = models.VehiculoModelo.obtenerPorCliente(c.getId());
                    for (models.VehiculoModelo v : vehiculos) {
                        vista.getCmbVehiculos().addItem(v);
                    }
                    break;
                }
            }
        }
    }

    private void calcularTotalAutomatico() {
        double refacciones = 0, servicios = 0, impuesto = 0;
        try { refacciones = Double.parseDouble(vista.getTxtCostoRefacciones().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        try { servicios = Double.parseDouble(vista.getTxtCostoServicios().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        try { impuesto = Double.parseDouble(vista.getTxtImpuesto().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        vista.getTxtTotal().setText(String.format(java.util.Locale.US, "%.2f", refacciones + servicios + impuesto));
    }

    private void seleccionarImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
        int res = chooser.showOpenDialog(vista);
        if (res == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = chooser.getSelectedFile();
            try {
                File dir = new File("imagenes_ordenes");
                if (!dir.exists()) dir.mkdirs();
                
                File destino = new File(dir, System.currentTimeMillis() + "_" + archivoSeleccionado.getName());
                Files.copy(archivoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                rutaImagenSeleccionada = destino.getAbsolutePath();
                vista.getLblImgNombre().setText(archivoSeleccionado.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al copiar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Lee los datos del formulario, crea una OrdenServicioModelo y la agrega
     * a la lista de órdenes de servicio.
     */
    private void crearOrden() {
        // Validar que al menos el nombre del cliente no esté vacío
        String nombreCliente = "";
        Object itemSeleccionado = vista.getCmbClientes().getSelectedItem();
        if (itemSeleccionado != null) {
            nombreCliente = itemSeleccionado.toString().split(" - ")[0].trim();
        }
        
        if (nombreCliente.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor ingrese o seleccione el nombre del cliente.",
                    "Dato faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Leer datos del formulario
        models.VehiculoModelo vehiculoSeleccionado = (models.VehiculoModelo) vista.getCmbVehiculos().getSelectedItem();
        String vehiculoStr = "N/A";
        String idVehiculo = "1";
        if (vehiculoSeleccionado != null) {
            vehiculoStr = vehiculoSeleccionado.getMarca() + " " + vehiculoSeleccionado.getModelo() + " (" + vehiculoSeleccionado.getPlacas() + ")";
            idVehiculo = vehiculoSeleccionado.getId();
        }
        
        if (vehiculoSeleccionado == null) {
            JOptionPane.showMessageDialog(vista,
                    "Debe seleccionar o registrar un vehículo del cliente para crear la orden.",
                    "Dato faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fechaIngreso = vista.getTxtFechaIngreso().getText().trim();
        String fechaEntrega = vista.getTxtFechaEntrega().getText().trim();

        try {
            java.time.LocalDate.parse(fechaIngreso);
            if (!fechaEntrega.isEmpty()) {
                java.time.LocalDate.parse(fechaEntrega);
            }
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(vista, 
                "El formato de las fechas debe ser YYYY-MM-DD (ejemplo: 2026-05-27) y deben ser fechas válidas.",
                "Formato de fecha inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar Falla Reportada
        String descripcion = vista.getTxtDescripcionFalla().getText().trim();
        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "La descripción de la falla es obligatoria.", "Dato faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String condicion = vista.getTxtCondicionVehiculo().getText().trim();
        
        // Juntamos descripción y condición en Falla Reportada (el campo TEXT de la BD)
        String fallaCompleta = descripcion;
        if (!condicion.isEmpty()) fallaCompleta += " | Condición: " + condicion;

        // Validar Kilometraje
        int km = 0;
        try {
            String kmText = vista.getTxtKilometraje().getText().trim();
            if (!kmText.isEmpty()) {
                km = Integer.parseInt(kmText);
                if (km < 0) throw new NumberFormatException();
            } else {
                JOptionPane.showMessageDialog(vista, "El kilometraje es obligatorio.", "Dato faltante", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "El kilometraje debe ser un número entero válido y no negativo.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Forzar la adición de cualquier refacción que se haya quedado escrita en los campos pero sin presionar '+'
        vista.getPanelListaServicios().agregarElementoPendiente();

        // Obtener el ID del vehículo
        String idVehiculoStr = (String) vista.getCmbVehiculos().getSelectedItem();
        if (idVehiculoStr == null || !idVehiculoStr.contains(" - ")) {
            JOptionPane.showMessageDialog(vista, "Seleccione un vehículo válido.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar Nivel de Combustible
        String combustible = (String) vista.getCmbCombustible().getSelectedItem();
        if (combustible == null || combustible.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El nivel de combustible es obligatorio.", "Dato faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar Imagen
        if (rutaImagenSeleccionada == null || rutaImagenSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Es obligatorio subir una imagen de diagnóstico del vehículo.", "Dato faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estado = (String) vista.getCmbEstado().getSelectedItem();

        // Calcular costos con validación
        double costoRefacciones = 0, costoServicios = 0, impuesto = 0, subtotal = 0, total = 0;
        try { 
            String refText = vista.getTxtCostoRefacciones().getText().trim();
            if (refText.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El costo de refacciones es obligatorio (puede ser 0 si no hay).", "Dato faltante", JOptionPane.WARNING_MESSAGE);
                return;
            }
            costoRefacciones = Double.parseDouble(refText);
            
            String srvText = vista.getTxtCostoServicios().getText().trim();
            if (srvText.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El costo de servicios (mano de obra) es obligatorio (puede ser 0).", "Dato faltante", JOptionPane.WARNING_MESSAGE);
                return;
            }
            costoServicios = Double.parseDouble(srvText);
            
            String impText = vista.getTxtImpuesto().getText().trim();
            if (impText.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El impuesto es obligatorio (puede ser 0).", "Dato faltante", JOptionPane.WARNING_MESSAGE);
                return;
            }
            impuesto = Double.parseDouble(impText);
            
            if (costoRefacciones < 0 || costoServicios < 0 || impuesto < 0) {
                throw new NumberFormatException();
            }
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(vista, "Los costos e impuestos deben ser números válidos y no negativos.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        subtotal = costoRefacciones + costoServicios;
        total = subtotal + impuesto;

        // Mostrar el total calculado en el formulario
        vista.getTxtTotal().setText(String.format(java.util.Locale.US, "%.2f", total));

        // Crear la orden
        contadorOrdenes++;
        OrdenServicioModelo nuevaOrden = new OrdenServicioModelo(
                "ORD-" + String.format("%03d", contadorOrdenes),
                nombreCliente,
                vehiculoStr,
                fechaIngreso,
                fechaEntrega,
                costoServicios,      // Costo mano de obra (servicios)
                costoRefacciones,    // Costo refacciones
                total,               // Monto total
                estado
        );
        nuevaOrden.setSubtotal(subtotal);
        nuevaOrden.setImpuesto(impuesto);
        nuevaOrden.setIdVehiculo(idVehiculo);

        // Guardar datos adicionales del formulario en la orden
        nuevaOrden.setTipoRequerimiento(vista.getTipoFallaSeleccionado());
        nuevaOrden.setFallaReportada(fallaCompleta);
        nuevaOrden.setKilometraje(km);
        nuevaOrden.setNivelCombustible((String) vista.getCmbCombustible().getSelectedItem());
        nuevaOrden.setImagenDiagnostico(rutaImagenSeleccionada);

        // Agregar la orden al controlador de órdenes (aparecerá en la tabla)
        ordenesControlador.agregarOrden(nuevaOrden);
        
        // Guardar los detalles en la base de datos
        try {
            int dbId = Integer.parseInt(nuevaOrden.getId());
            for (models.DetalleOrdenModelo det : vista.getPanelListaServicios().getDetalles()) {
                det.setIdOrden(String.valueOf(dbId));
                det.guardar();
            }
        } catch (NumberFormatException ignored) {
            // El ID no era un número (guardado falló)
        }
        
        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(vista,
                "¡Orden de trabajo creada exitosamente!\nID: " + nuevaOrden.getId(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        // Limpiar el formulario para crear otra orden
        vista.limpiarFormulario();
        rutaImagenSeleccionada = null;
    }

    public CrearOrdenVista getVista() { return vista; }
}
