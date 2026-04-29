package controlles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import models.UsuarioModelo;
import views.DashboardVista;
import views.DialogoError;
import views.LoginVista;

/**
 * Controlador del Login.
 * Conecta el modelo (UsuarioModelo) con la vista (LoginVista).
 * Valida campos vacíos, credenciales, y navega al Dashboard.
 */
public class LoginControlador {

    private UsuarioModelo modelo;
    private LoginVista vista;

    public LoginControlador(UsuarioModelo modelo, LoginVista vista) {
        this.modelo = modelo;
        this.vista = vista;

        // Listener del botón ACCEDER
        vista.getBotonAcceder().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intentarLogin();
            }
        });

        // Enter en campo usuario → mover a contraseña
        vista.getCampoUsuario().getCampo().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    vista.getCampoPassword().getCampo().requestFocus();
                }
            }
        });

        // Enter en campo contraseña → intentar login
        vista.getCampoPassword().getCampo().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        });
    }

    private void intentarLogin() {
        String usuario = vista.getCampoUsuario().getTexto();
        String password = vista.getCampoPassword().getTexto();

        // Validar campos vacíos
        boolean hayError = false;
        if (usuario.isEmpty()) {
            vista.getCampoUsuario().setError(true);
            hayError = true;
        } else {
            vista.getCampoUsuario().setError(false);
        }
        if (password.isEmpty()) {
            vista.getCampoPassword().setError(true);
            hayError = true;
        } else {
            vista.getCampoPassword().setError(false);
        }

        if (hayError) return;

        // Validar credenciales
        if (modelo.validarCredenciales(usuario, password)) {
            abrirDashboard();
        } else {
            DialogoError.mostrar(vista);
        }
    }

    private void abrirDashboard() {
        vista.setVisible(false);

        DashboardVista dashboard = new DashboardVista();
        DashboardControlador dashCtrl = new DashboardControlador(dashboard, this);
        dashboard.setVisible(true);
    }

    /** Vuelve a mostrar la ventana de login (llamado desde DashboardControlador). */
    public void mostrarLogin() {
        vista.getCampoUsuario().limpiar();
        vista.getCampoPassword().limpiar();
        vista.setVisible(true);
    }
}
