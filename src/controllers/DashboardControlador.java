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

        // ============ REGISTRAR EVENTOS ============

        // Botón "Cerrar sesión" del menú lateral
        dashboard.getSidebar().setOnCerrarSesion(this::cerrarSesion);

        // Navegación del menú lateral: cuando el usuario hace clic en una opción,
        // cambiamos el panel central del dashboard
        dashboard.getSidebar().setOnMenuSelectedListener((index, title) -> {
            switch (index) {
                case 0: // Dashboard (pantalla principal con indicadores)
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
    }

    /**
     * Cierra el dashboard y muestra la pantalla de login de nuevo.
     */
    private void cerrarSesion() {
        dashboard.dispose();
        loginControlador.mostrarLogin();
    }
}
