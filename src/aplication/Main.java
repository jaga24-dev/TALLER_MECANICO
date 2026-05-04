package aplication;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controllers.LoginControlador;
import models.UsuarioModelo;
import views.LoginVista;

/**
* Punto de entrada de la aplicación.
* Inicializa Modelo, Vista y Controlador del Login.
*/
public class Main {
   public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
           try {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
           } catch (Exception ignored) {
           }

           UsuarioModelo modelo = new UsuarioModelo();
           LoginVista loginVista = new LoginVista();
           LoginControlador controlador = new LoginControlador(modelo, loginVista);
           loginVista.setVisible(true);
       });
   }
}
