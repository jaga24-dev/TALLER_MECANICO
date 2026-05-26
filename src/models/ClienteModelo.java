package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

    // --- Métodos de Base de Datos ---

    public static List<ClienteModelo> obtenerTodos() {
        List<ClienteModelo> lista = new ArrayList<>();
        String query = "SELECT * FROM Clientes";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ClienteModelo cliente = new ClienteModelo(
                        String.valueOf(rs.getInt("id_cliente")),
                        rs.getString("nombre_completo"),
                        rs.getString("telefono"),
                        rs.getString("correo")
                );
                // Aquí podríamos cargar los vehículos también
                cliente.setVehiculos(VehiculoModelo.obtenerPorCliente(cliente.getId()));
                lista.add(cliente);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar() {
        String query = "INSERT INTO Clientes (nombre_completo, telefono, correo) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, this.nombreCompleto);
            ps.setString(2, this.telefono);
            ps.setString(3, this.correo);
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
        String query = "UPDATE Clientes SET nombre_completo = ?, telefono = ?, correo = ? WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, this.nombreCompleto);
            ps.setString(2, this.telefono);
            ps.setString(3, this.correo);
            ps.setInt(4, Integer.parseInt(this.id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar() {
        String query = "DELETE FROM Clientes WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(this.id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
