package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioModelo {

    private String id;
    private String idVehiculo;
    private String fechaIngreso;
    private String fechaEntregaEstimada;
    private String tipoRequerimiento;
    private int kilometraje;
    private String nivelCombustible;
    private String fallaReportada;
    private String estado;
    private double costoRefacciones;
    private double costoManoObra;
    private double subtotal;
    private double impuesto;
    private double montoTotal;
    private String imagenDiagnostico;
    
    // Campos temporales para compatibilidad con la vista
    private String nombreCliente;
    private String vehiculoRelacionado;

    public OrdenServicioModelo() {
        this.estado = "En espera";
    }

    public OrdenServicioModelo(String id, String nombreCliente, String vehiculoRelacionado,
                               String fechaIngreso, String fechaEntregaEstimada,
                               double costoManoObra, double costoRefacciones,
                               double montoTotal, String estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.vehiculoRelacionado = vehiculoRelacionado;
        this.fechaIngreso = fechaIngreso;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.costoManoObra = costoManoObra;
        this.costoRefacciones = costoRefacciones;
        this.montoTotal = montoTotal;
        this.estado = estado;
    }

    // --- Getters y Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(String idVehiculo) { this.idVehiculo = idVehiculo; }
    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(String fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }
    public String getTipoRequerimiento() { return tipoRequerimiento; }
    public void setTipoRequerimiento(String tipoRequerimiento) { this.tipoRequerimiento = tipoRequerimiento; }
    public int getKilometraje() { return kilometraje; }
    public void setKilometraje(int kilometraje) { this.kilometraje = kilometraje; }
    public String getNivelCombustible() { return nivelCombustible; }
    public void setNivelCombustible(String nivelCombustible) { this.nivelCombustible = nivelCombustible; }
    public String getFallaReportada() { return fallaReportada; }
    public void setFallaReportada(String fallaReportada) { this.fallaReportada = fallaReportada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getCostoRefacciones() { return costoRefacciones; }
    public void setCostoRefacciones(double costoRefacciones) { this.costoRefacciones = costoRefacciones; }
    public double getCostoManoObra() { return costoManoObra; }
    public void setCostoManoObra(double costoManoObra) { this.costoManoObra = costoManoObra; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getImpuesto() { return impuesto; }
    public void setImpuesto(double impuesto) { this.impuesto = impuesto; }
    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
    public String getImagenDiagnostico() { return imagenDiagnostico; }
    public void setImagenDiagnostico(String imagenDiagnostico) { this.imagenDiagnostico = imagenDiagnostico; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getVehiculoRelacionado() { return vehiculoRelacionado; }
    public void setVehiculoRelacionado(String vehiculoRelacionado) { this.vehiculoRelacionado = vehiculoRelacionado; }

    // --- Métodos BD ---

    public static List<OrdenServicioModelo> obtenerTodas() {
        List<OrdenServicioModelo> lista = new ArrayList<>();
        // Unimos con Vehiculos y Clientes para obtener nombres
        String query = "SELECT o.*, v.marca, v.modelo, c.nombre_completo " +
                       "FROM Ordenes_Servicio o " +
                       "JOIN Vehiculos v ON o.id_vehiculo = v.id_vehiculo " +
                       "JOIN Clientes c ON v.id_cliente = c.id_cliente";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrdenServicioModelo orden = new OrdenServicioModelo();
                orden.setId(String.valueOf(rs.getInt("id_orden")));
                orden.setIdVehiculo(String.valueOf(rs.getInt("id_vehiculo")));
                orden.setFechaIngreso(rs.getString("fecha_ingreso"));
                orden.setFechaEntregaEstimada(rs.getString("fecha_entrega_estimada"));
                orden.setTipoRequerimiento(rs.getString("tipo_requerimiento"));
                orden.setKilometraje(rs.getInt("kilometraje"));
                orden.setNivelCombustible(rs.getString("nivel_combustible"));
                orden.setFallaReportada(rs.getString("falla_reportada"));
                orden.setEstado(rs.getString("estado"));
                orden.setCostoRefacciones(rs.getDouble("costo_refacciones"));
                orden.setCostoManoObra(rs.getDouble("costo_mano_obra"));
                orden.setSubtotal(rs.getDouble("subtotal"));
                orden.setImpuesto(rs.getDouble("impuesto"));
                orden.setMontoTotal(rs.getDouble("monto_total"));
                orden.setImagenDiagnostico(rs.getString("imagen_diagnostico"));
                
                orden.setNombreCliente(rs.getString("nombre_completo"));
                orden.setVehiculoRelacionado(rs.getString("marca") + " " + rs.getString("modelo"));
                
                lista.add(orden);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<OrdenServicioModelo> obtenerPorVehiculo(String idVehiculo) {
        List<OrdenServicioModelo> lista = new ArrayList<>();
        
        int idVehiculoInt;
        try {
            idVehiculoInt = Integer.parseInt(idVehiculo);
        } catch (NumberFormatException e) {
            // Si el ID no es numérico (ej. "v1" de los datos de prueba), no puede estar en la BD
            return lista;
        }

        String query = "SELECT o.*, v.marca, v.modelo, c.nombre_completo " +
                       "FROM Ordenes_Servicio o " +
                       "JOIN Vehiculos v ON o.id_vehiculo = v.id_vehiculo " +
                       "JOIN Clientes c ON v.id_cliente = c.id_cliente " +
                       "WHERE o.id_vehiculo = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idVehiculoInt);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdenServicioModelo orden = new OrdenServicioModelo();
                    orden.setId(String.valueOf(rs.getInt("id_orden")));
                    orden.setIdVehiculo(String.valueOf(rs.getInt("id_vehiculo")));
                    orden.setFechaIngreso(rs.getString("fecha_ingreso"));
                    orden.setFechaEntregaEstimada(rs.getString("fecha_entrega_estimada"));
                    orden.setTipoRequerimiento(rs.getString("tipo_requerimiento"));
                    orden.setKilometraje(rs.getInt("kilometraje"));
                    orden.setNivelCombustible(rs.getString("nivel_combustible"));
                    orden.setFallaReportada(rs.getString("falla_reportada"));
                    orden.setEstado(rs.getString("estado"));
                    orden.setCostoRefacciones(rs.getDouble("costo_refacciones"));
                    orden.setCostoManoObra(rs.getDouble("costo_mano_obra"));
                    orden.setSubtotal(rs.getDouble("subtotal"));
                    orden.setImpuesto(rs.getDouble("impuesto"));
                    orden.setMontoTotal(rs.getDouble("monto_total"));
                    orden.setImagenDiagnostico(rs.getString("imagen_diagnostico"));
                    
                    orden.setNombreCliente(rs.getString("nombre_completo"));
                    orden.setVehiculoRelacionado(rs.getString("marca") + " " + rs.getString("modelo"));
                    
                    lista.add(orden);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar() {
        String query = "INSERT INTO Ordenes_Servicio (id_vehiculo, fecha_ingreso, fecha_entrega_estimada, tipo_requerimiento, " +
                       "kilometraje, nivel_combustible, falla_reportada, estado, costo_refacciones, costo_mano_obra, subtotal, " +
                       "impuesto, monto_total, imagen_diagnostico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, Integer.parseInt(this.idVehiculo));
            ps.setString(2, this.fechaIngreso);
            ps.setString(3, this.fechaEntregaEstimada);
            ps.setString(4, this.tipoRequerimiento);
            ps.setInt(5, this.kilometraje);
            ps.setString(6, this.nivelCombustible);
            ps.setString(7, this.fallaReportada);
            ps.setString(8, this.estado);
            ps.setDouble(9, this.costoRefacciones);
            ps.setDouble(10, this.costoManoObra);
            ps.setDouble(11, this.subtotal);
            ps.setDouble(12, this.impuesto);
            ps.setDouble(13, this.montoTotal);
            ps.setString(14, this.imagenDiagnostico);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = String.valueOf(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar() {
        //String query = "UPDATE Ordenes_Servicio SET estado=?, fecha_entrega_estimada=?, costo_refacciones=?, costo_mano_obra=?, subtotal=?, impuesto=?, monto_total=? WHERE id_orden=?";
    	String query = "UPDATE Ordenes_Servicio SET estado=?, fecha_ingreso=?, fecha_entrega_estimada=?, costo_refacciones=?, costo_mano_obra=?, subtotal=?, impuesto=?, monto_total=? WHERE id_orden=?";
    	try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, this.estado);
            ps.setString(2, this.fechaIngreso);
            ps.setString(3, this.fechaEntregaEstimada);
            ps.setDouble(4, this.costoRefacciones);
            ps.setDouble(5, this.costoManoObra);
            ps.setDouble(6, this.subtotal);
            ps.setDouble(7, this.impuesto);
            ps.setDouble(8, this.montoTotal);
            ps.setInt(9, Integer.parseInt(this.id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean eliminar(String id) {
        String query = "DELETE FROM Ordenes_Servicio WHERE id_orden=?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
