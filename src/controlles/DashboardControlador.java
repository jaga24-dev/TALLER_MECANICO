package controlles;

import views.DashboardVista;

/**
 * Controlador del Dashboard.
 * Maneja la lógica de "Cerrar sesión" para volver al login.
 */
public class DashboardControlador {

    private DashboardVista dashboard;
    private LoginControlador loginControlador;

    public DashboardControlador(DashboardVista dashboard, LoginControlador loginCtrl) {
        this.dashboard = dashboard;
        this.loginControlador = loginCtrl;

        // Registrar callback para "Cerrar sesión" en el sidebar
        dashboard.getSidebar().setOnCerrarSesion(this::cerrarSesion);
    }

    private void cerrarSesion() {
        dashboard.dispose();
        loginControlador.mostrarLogin();
    }
}
