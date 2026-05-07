package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import models.OrdenServicioModelo;
import views.EditarOrdenVista;

/**
 * Controlador para la vista EditarOrdenVista.
 */
public class EditarOrdenControlador {

    private EditarOrdenVista vista;
    private OrdenServicioModelo orden;
    private Runnable alTerminar; // Callback para volver a la tabla

    public EditarOrdenControlador(EditarOrdenVista vista, OrdenServicioModelo orden, Runnable alTerminar) {
        this.vista = vista;
        this.orden = orden;
        this.alTerminar = alTerminar;

        // Cargar los datos del modelo a la vista
        vista.cargarDatosOrden(orden);

        // Inicializar eventos
        inicializarEventos();
    }

    private void inicializarEventos() {
        // Evento Cancelar
        vista.getBtnCancelar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Volver sin guardar cambios
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });

        // Evento Confirmar Cambios
        vista.getBtnConfirmar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Guardar cambios en el modelo
                vista.actualizarModelo(orden);
                // Volver a la tabla
                if (alTerminar != null) {
                    alTerminar.run();
                }
            }
        });
    }

    public EditarOrdenVista getVista() {
        return vista;
    }
}
