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
        vista.getTxtSubtotal().getDocument().addDocumentListener(docListener);
        vista.getTxtImpuesto().getDocument().addDocumentListener(docListener);
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
        double subtotal = 0, impuesto = 0;
        try { subtotal = Double.parseDouble(vista.getTxtSubtotal().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        try { impuesto = Double.parseDouble(vista.getTxtImpuesto().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        vista.getTxtTotal().setText(String.format("%.2f", subtotal + impuesto));
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

        String estado = (String) vista.getCmbEstado().getSelectedItem();

        // Calcular costos
        double subtotal = 0, impuesto = 0, total = 0;
        try { subtotal = Double.parseDouble(vista.getTxtSubtotal().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        try { impuesto = Double.parseDouble(vista.getTxtImpuesto().getText().trim()); } catch (Exception ex) { /* ignorar */ }
        total = subtotal + impuesto;

        // Mostrar el total calculado en el formulario
        vista.getTxtTotal().setText(String.format("%.2f", total));

        // Crear la orden
        contadorOrdenes++;
        OrdenServicioModelo nuevaOrden = new OrdenServicioModelo(
                "ORD-" + String.format("%03d", contadorOrdenes),
                nombreCliente,
                vehiculoStr,
                fechaIngreso,
                fechaEntrega,
                subtotal,       // Costo mano de obra = subtotal por ahora
                0,              // Costo refacciones
                total,          // Monto total
                estado
        );
        nuevaOrden.setIdVehiculo(idVehiculo);

        // Guardar datos adicionales del formulario en la orden
        nuevaOrden.setTipoRequerimiento(vista.getTipoFallaSeleccionado());
        
        String descripcion = vista.getTxtDescripcionFalla().getText().trim();
        String servicio = vista.getTxtServicioProducto().getText().trim();
        String condicion = vista.getTxtCondicionVehiculo().getText().trim();
        
        // Juntamos descripción, servicio y condición en Falla Reportada (el campo TEXT de la BD)
        String fallaCompleta = descripcion;
        if (!servicio.isEmpty()) fallaCompleta += " | Servicio: " + servicio;
        if (!condicion.isEmpty()) fallaCompleta += " | Condición: " + condicion;
        
        nuevaOrden.setFallaReportada(fallaCompleta);
        
        int km = 0;
        try {
            km = Integer.parseInt(vista.getTxtKilometraje().getText().trim());
        } catch (Exception ex) {
            // Ignorar formato incorrecto
        }
        nuevaOrden.setKilometraje(km);
        nuevaOrden.setNivelCombustible((String) vista.getCmbCombustible().getSelectedItem());
        nuevaOrden.setImagenDiagnostico(rutaImagenSeleccionada);

        // Agregar la orden al controlador de órdenes (aparecerá en la tabla)
        ordenesControlador.agregarOrden(nuevaOrden);

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
