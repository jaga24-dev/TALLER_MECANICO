package models;

/**
 * Modelo que representa una Orden de Servicio en el taller.
 * Contiene los datos del cliente, vehículo, costos y estado del trabajo.
 * 
 * NOTA PARA NOVATOS: Esta clase es un "POJO" (Plain Old Java Object),
 * que simplemente guarda datos con getters y setters.
 */
public class OrdenServicioModelo {

    // --- Datos de la orden ---
    private String id;                  // Identificador único de la orden
    private String nombreCliente;       // Ej: "S.De Anda"
    private String vehiculoRelacionado; // Ej: "Ford Explorer"
    private String fechaIngreso;        // Ej: "31/3/2026"
    private String fechaEntrega;        // Ej: "4/4/2026"

    // --- Costos ---
    private double costoManoObra;       // Ej: 300.00
    private double costoRefacciones;    // Ej: 500.00
    private double montoTotal;          // Ej: 820.00 (incluye IVA)

    // --- Estado ---
    private String estado;              // "LISTO", "EN REPARACIÓN", "EN ESPERA"

    // --- Datos adicionales para el formulario de "Crear Orden" ---
    private String tipoFalla;           // "Reparación", "Mantención" o "Garantía"
    private String descripcionFalla;    // Descripción detallada de la falla
    private String servicioProducto;    // Descripción del servicio o producto
    private String kilometraje;         // Kilometraje actual del vehículo
    private String nivelCombustible;    // "E", "1/4", "1/2", "3/4", "F"
    private String condicionVehiculo;   // Condición general del vehículo

    // Constructor vacío
    public OrdenServicioModelo() {
        this.estado = "EN ESPERA";
    }

    // Constructor con datos principales (para la tabla)
    public OrdenServicioModelo(String id, String nombreCliente, String vehiculoRelacionado,
                                String fechaIngreso, String fechaEntrega,
                                double costoManoObra, double costoRefacciones,
                                double montoTotal, String estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.vehiculoRelacionado = vehiculoRelacionado;
        this.fechaIngreso = fechaIngreso;
        this.fechaEntrega = fechaEntrega;
        this.costoManoObra = costoManoObra;
        this.costoRefacciones = costoRefacciones;
        this.montoTotal = montoTotal;
        this.estado = estado;
    }

    // --- Getters y Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getVehiculoRelacionado() { return vehiculoRelacionado; }
    public void setVehiculoRelacionado(String vehiculoRelacionado) { this.vehiculoRelacionado = vehiculoRelacionado; }

    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public double getCostoManoObra() { return costoManoObra; }
    public void setCostoManoObra(double costoManoObra) { this.costoManoObra = costoManoObra; }

    public double getCostoRefacciones() { return costoRefacciones; }
    public void setCostoRefacciones(double costoRefacciones) { this.costoRefacciones = costoRefacciones; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoFalla() { return tipoFalla; }
    public void setTipoFalla(String tipoFalla) { this.tipoFalla = tipoFalla; }

    public String getDescripcionFalla() { return descripcionFalla; }
    public void setDescripcionFalla(String descripcionFalla) { this.descripcionFalla = descripcionFalla; }

    public String getServicioProducto() { return servicioProducto; }
    public void setServicioProducto(String servicioProducto) { this.servicioProducto = servicioProducto; }

    public String getKilometraje() { return kilometraje; }
    public void setKilometraje(String kilometraje) { this.kilometraje = kilometraje; }

    public String getNivelCombustible() { return nivelCombustible; }
    public void setNivelCombustible(String nivelCombustible) { this.nivelCombustible = nivelCombustible; }

    public String getCondicionVehiculo() { return condicionVehiculo; }
    public void setCondicionVehiculo(String condicionVehiculo) { this.condicionVehiculo = condicionVehiculo; }
}
