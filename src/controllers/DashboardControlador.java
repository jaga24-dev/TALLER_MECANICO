package controllers;

import views.ClientesVista;
import views.CrearOrdenVista;
import views.DashboardVista;
import views.OrdenesServicioVista;
import views.VehiculosVista;

/**
 * Controlador principal del Dashboard.
 * Se encarga de:
 * 1. Manejar la navegación del menú lateral (cambiar la vista central).
 * 2. Inicializar todos los controladores secundarios.
 * 3. Manejar el "Cerrar sesión".
 * 
 * NOTA PARA NOVATOS:
 * Cada opción del menú lateral tiene su propia vista (pantalla) y su propio controlador.
 * Cuando el usuario hace clic en una opción, este controlador "cambia" el panel central
 * del dashboard por la vista correspondiente.
 * 
 * MAPA DE NAVEGACIÓN:
 *   index 0 = "Dashboard"           -> MainContentPanel (ya existía)
 *   index 1 = "Consultar clientes"  -> ClientesVista
 *   index 2 = "Crear orden"         -> CrearOrdenVista
 *   index 3 = "Órdenes de servicio" -> OrdenesServicioVista
 *   index 4 = "Vehículos"           -> VehiculosVista
 */
public class DashboardControlador {

    // --- Referencias principales ---
    private DashboardVista dashboard;
    private LoginControlador loginControlador;

    // --- Vistas secundarias (una por cada opción del menú) ---
    private ClientesVista clientesVista;
    private CrearOrdenVista crearOrdenVista;
    private OrdenesServicioVista ordenesVista;
    private VehiculosVista vehiculosVista;

    // --- Controladores secundarios ---
    private ClientesControlador clientesControlador;
    private CrearOrdenControlador crearOrdenControlador;
    private OrdenesServicioControlador ordenesControlador;
    private VehiculosControlador vehiculosControlador;

    public DashboardControlador(DashboardVista dashboard, LoginControlador loginCtrl) {
        this.dashboard = dashboard;
        this.loginControlador = loginCtrl;

        // ============ INICIALIZAR VISTAS ============
        // Creamos cada vista (pantalla)
        this.clientesVista = new ClientesVista();
        this.crearOrdenVista = new CrearOrdenVista();
        this.ordenesVista = new OrdenesServicioVista();
        this.vehiculosVista = new VehiculosVista();

        // ============ INICIALIZAR CONTROLADORES ============
        // Cada controlador conecta su vista con los datos
        this.clientesControlador = new ClientesControlador(this.clientesVista);
        this.ordenesControlador = new OrdenesServicioControlador(this.ordenesVista);
        this.vehiculosControlador = new VehiculosControlador(this.vehiculosVista);
        // El controlador de "Crear Orden" necesita acceso al controlador de órdenes
        // para poder agregar nuevas órdenes a la tabla
        this.crearOrdenControlador = new CrearOrdenControlador(this.crearOrdenVista, this.ordenesControlador);
        
        // Configurar listener para editar órdenes
        this.ordenesControlador.setOnEditarOrdenReq(orden -> {
            views.EditarOrdenVista editarVista = new views.EditarOrdenVista();
            new EditarOrdenControlador(editarVista, orden, () -> {
                // Al terminar de editar, volver a la tabla de órdenes
                dashboard.setMainContent(this.ordenesVista);
                // Refrescar la tabla para mostrar los cambios
                this.ordenesControlador.refrescarTabla();
            });
            dashboard.setMainContent(editarVista);
        });
        
        // ============ REGISTRAR EVENTOS ============

        // Botón "Cerrar sesión" del menú lateral
        dashboard.getSidebar().setOnCerrarSesion(this::cerrarSesion);

        // Navegación del menú lateral: cuando el usuario hace clic en una opción,
        // cambiamos el panel central del dashboard
        dashboard.getSidebar().setOnMenuSelectedListener((index, title) -> {
            switch (index) {
                case 0: // Dashboard (pantalla principal con indicadores)
                    actualizarDashboard();
                    dashboard.setMainContent(dashboard.getMainContent());
                    break;

                case 1: // Consultar clientes (tabla de clientes)
                	dashboard.setMainContent(this.clientesVista);
                    break;

                case 2: // Crear orden (formulario)
                    dashboard.setMainContent(this.crearOrdenVista);
                    break;

                case 3: // Órdenes de servicio (tabla de órdenes)
                    dashboard.setMainContent(this.ordenesVista);
                    break;

                case 4: // Vehículos (tabla de vehículos)
                    dashboard.setMainContent(this.vehiculosVista);
                    break;

                default:
                    System.out.println("Navegación a: " + title + " (No implementado aún)");
                    break;
            }
        });
        
        // Actualizar dashboard al inicio
        actualizarDashboard();
    }

    private void actualizarDashboard() {
        java.util.List<models.OrdenServicioModelo> ordenes = models.OrdenServicioModelo.obtenerTodas();
        
        int vehiculosIngresadosHoy = 0;
        int vehiculosEntregadosHoy = 0;
        int trabajosEnCurso = 0;
        double ingresosSemanales = 0;
        int ordenesListas = 0;
        java.util.List<String> proximasEntregas = new java.util.ArrayList<>();
        
        String fechaHoy = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"));

        for (models.OrdenServicioModelo o : ordenes) {
            // Vehículos de hoy
            if (fechaHoy.equals(o.getFechaIngreso())) vehiculosIngresadosHoy++;
            if ("LISTO".equalsIgnoreCase(o.getEstado()) || "ENTREGADO".equalsIgnoreCase(o.getEstado())) {
                if (fechaHoy.equals(o.getFechaEntregaEstimada()) || fechaHoy.equals(o.getFechaIngreso())) {
                    vehiculosEntregadosHoy++;
                }
            }
            
            // Trabajos en curso
            if ("EN ESPERA".equalsIgnoreCase(o.getEstado()) || "REVISIÓN".equalsIgnoreCase(o.getEstado()) || "REPARACIÓN".equalsIgnoreCase(o.getEstado())) {
                trabajosEnCurso++;
            }
            
            // Ingresos (simplificado)
            if ("LISTO".equalsIgnoreCase(o.getEstado()) || "ENTREGADO".equalsIgnoreCase(o.getEstado())) {
                ingresosSemanales += o.getMontoTotal();
                ordenesListas++;
            }
            
            // Próximas entregas
            if (!"ENTREGADO".equalsIgnoreCase(o.getEstado()) && !"LISTO".equalsIgnoreCase(o.getEstado())) {
                if (proximasEntregas.size() < 5) {
                    proximasEntregas.add(o.getVehiculoRelacionado() + " (" + o.getNombreCliente() + ")");
                }
            }
        }
        
        int eficiencia = ordenes.isEmpty() ? 0 : (int) Math.round((ordenesListas * 100.0) / ordenes.size());

        views.MainContentPanel mainPanel = dashboard.getMainContent();
        mainPanel.actualizarVehiculos(String.valueOf(vehiculosIngresadosHoy), vehiculosIngresadosHoy + " Ingresados, " + vehiculosEntregadosHoy + " Entregados");
        mainPanel.actualizarTrabajos(String.valueOf(trabajosEnCurso), trabajosEnCurso + " Órdenes activas");
        mainPanel.actualizarIngresos("$" + String.format("%.2f", ingresosSemanales) + " MXN", "Total acumulado");
        mainPanel.actualizarEficiencia(eficiencia + "%", "Órdenes completadas");
        mainPanel.actualizarEntregas(proximasEntregas);
    }

    /**
     * Cierra el dashboard y muestra la pantalla de login de nuevo.
     */
    private void cerrarSesion() {
        dashboard.dispose();
        loginControlador.mostrarLogin();
    }
}
