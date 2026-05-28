package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DetalleOrdenModelo {

    private String id;
    private String idOrden;
    private String concepto;
    private double precio;

    public DetalleOrdenModelo() {
    }

    public DetalleOrdenModelo(String id, String idOrden, String concepto, double precio) {
        this.id = id;
        this.idOrden = idOrden;
        this.concepto = concepto;
        this.precio = precio;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    // --- Métodos de Base de Datos ---

    public static List<DetalleOrdenModelo> obtenerPorOrden(String idOrden) {
        List<DetalleOrdenModelo> lista = new ArrayList<>();
        String query = "SELECT * FROM detalle_orden WHERE id_orden = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(idOrden));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetalleOrdenModelo(
                            String.valueOf(rs.getInt("id_detalle")),
                            String.valueOf(rs.getInt("id_orden")),
                            rs.getString("concepto"),
                            rs.getDouble("precio")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar() {
        String query = "INSERT INTO detalle_orden (id_orden, concepto, precio) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, Integer.parseInt(this.idOrden));
            ps.setString(2, this.concepto);
            ps.setDouble(3, this.precio);
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
}
