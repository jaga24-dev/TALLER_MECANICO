package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehiculoModelo {

    private String id;
    private String idCliente;
    private String marca;
    private String modelo;
    private int anio;
    private String placas;
    private String numeroSerie;
    private String imagen;

    public VehiculoModelo() {
    }

    public VehiculoModelo(String id, String idCliente, String marca, String modelo, int anio, String placas, String numeroSerie, String imagen) {
        this.id = id;
        this.idCliente = idCliente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
        this.numeroSerie = numeroSerie;
        this.imagen = imagen;
    }

    // Para retrocompatibilidad temporal con algunas partes de la interfaz
    public VehiculoModelo(String id, String marca, String modelo, int anio, String placas) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getPlacas() { return placas; }
    public void setPlacas(String placas) { this.placas = placas; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    @Override
    public String toString() {
        return marca + " " + modelo + " (" + anio + ")";
    }

    // --- Métodos de Base de Datos ---

    public static List<VehiculoModelo> obtenerTodos() {
        List<VehiculoModelo> lista = new ArrayList<>();
        String query = "SELECT * FROM vehiculos";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new VehiculoModelo(
                        String.valueOf(rs.getInt("id_vehiculo")),
                        String.valueOf(rs.getInt("id_cliente")),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("anio"),
                        rs.getString("placas"),
                        rs.getString("numero_serie"),
                        rs.getString("imagen_vehiculo")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<VehiculoModelo> obtenerPorCliente(String idCliente) {
        List<VehiculoModelo> lista = new ArrayList<>();
        String query = "SELECT * FROM vehiculos WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idCliente));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new VehiculoModelo(
                            String.valueOf(rs.getInt("id_vehiculo")),
                            String.valueOf(rs.getInt("id_cliente")),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getInt("anio"),
                            rs.getString("placas"),
                            rs.getString("numero_serie"),
                            rs.getString("imagen_vehiculo")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar() {
        String query = "INSERT INTO vehiculos (id_cliente, marca, modelo, anio, placas, numero_serie, imagen_vehiculo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, Integer.parseInt(this.idCliente));
            ps.setString(2, this.marca);
            ps.setString(3, this.modelo);
            ps.setInt(4, this.anio);
            ps.setString(5, this.placas);
            ps.setString(6, this.numeroSerie);
            ps.setString(7, this.imagen);
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
        String query = "UPDATE vehiculos SET id_cliente=?, marca=?, modelo=?, anio=?, placas=?, numero_serie=?, imagen_vehiculo=? WHERE id_vehiculo=?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(this.idCliente));
            ps.setString(2, this.marca);
            ps.setString(3, this.modelo);
            ps.setInt(4, this.anio);
            ps.setString(5, this.placas);
            ps.setString(6, this.numeroSerie);
            ps.setString(7, this.imagen);
            ps.setInt(8, Integer.parseInt(this.id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean eliminar(String id) {
        String query = "DELETE FROM vehiculos WHERE id_vehiculo=?";
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
