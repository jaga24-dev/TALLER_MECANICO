package models;

import java.util.ArrayList;
import java.util.List;

public class ClienteModelo {
    private String id;
    private String nombreCompleto;
    private String telefono;
    private String correo;
    private List<VehiculoModelo> vehiculos;

    public ClienteModelo() {
        this.vehiculos = new ArrayList<>();
    }

    public ClienteModelo(String id, String nombreCompleto, String telefono, String correo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
        this.vehiculos = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public List<VehiculoModelo> getVehiculos() { return vehiculos; }
    public void setVehiculos(List<VehiculoModelo> vehiculos) { this.vehiculos = vehiculos; }

    public void agregarVehiculo(VehiculoModelo vehiculo) {
        this.vehiculos.add(vehiculo);
    }

    public void eliminarVehiculo(VehiculoModelo vehiculo) {
        this.vehiculos.remove(vehiculo);
    }
    
    public String getResumenVehiculos() {
        if (vehiculos.isEmpty()) return "Sin vehículos";
        if (vehiculos.size() == 1) return vehiculos.get(0).toString();
        return vehiculos.get(0).toString() + " (+" + (vehiculos.size() - 1) + " más)";
    }
}
