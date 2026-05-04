package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

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

    public CrearOrdenControlador(CrearOrdenVista vista, OrdenesServicioControlador ordenesControlador) {
        this.vista = vista;
        this.ordenesControlador = ordenesControlador;

        inicializarEventos();
    }

    private void inicializarEventos() {
        // Cuando el usuario hace clic en "Crear Orden"
        vista.getBtnCrearOrden().addActionListener(e -> crearOrden());
    }

    /**
     * Lee los datos del formulario, crea una OrdenServicioModelo y la agrega
     * a la lista de órdenes de servicio.
     */
    private void crearOrden() {
        // Validar que al menos el nombre del cliente no esté vacío
        String nombreCliente = vista.getTxtNombreCliente().getText().trim();
        if (nombreCliente.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor ingrese el nombre del cliente.",
                    "Dato faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Leer datos del formulario
        String vehiculo = vista.getTxtNombreVehiculo().getText().trim();
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
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
                vehiculo.isEmpty() ? "N/A" : vehiculo,
                fechaHoy,       // Fecha de ingreso = hoy
                "",             // Fecha de entrega (pendiente)
                subtotal,       // Costo mano de obra = subtotal por ahora
                0,              // Costo refacciones
                total,          // Monto total
                estado
        );

        // Guardar datos adicionales del formulario en la orden
        nuevaOrden.setTipoFalla(vista.getTipoFallaSeleccionado());
        nuevaOrden.setDescripcionFalla(vista.getTxtDescripcionFalla().getText().trim());
        nuevaOrden.setServicioProducto(vista.getTxtServicioProducto().getText().trim());
        nuevaOrden.setKilometraje(vista.getTxtKilometraje().getText().trim());
        nuevaOrden.setNivelCombustible((String) vista.getCmbCombustible().getSelectedItem());
        nuevaOrden.setCondicionVehiculo(vista.getTxtCondicionVehiculo().getText().trim());

        // Agregar la orden al controlador de órdenes (aparecerá en la tabla)
        ordenesControlador.agregarOrden(nuevaOrden);

        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(vista,
                "¡Orden de trabajo creada exitosamente!\nID: " + nuevaOrden.getId(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        // Limpiar el formulario para crear otra orden
        vista.limpiarFormulario();
    }

    public CrearOrdenVista getVista() { return vista; }
}
