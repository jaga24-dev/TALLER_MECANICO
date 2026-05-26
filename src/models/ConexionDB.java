package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class ConexionDB {

    private static Map<String, String> env = null;

    private static void cargarEnv() {
        if (env != null) return;
        env = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    env.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar el archivo .env: " + e.getMessage());
        }
    }

    public static Connection obtenerConexion() {
        cargarEnv();
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = env.getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/taller_mecanico");
            String user = env.getOrDefault("DB_USER", "root");
            String pass = env.getOrDefault("DB_PASSWORD", "");
            
            conn = DriverManager.getConnection(url, user, pass);
            
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE Vehiculos ADD COLUMN imagen_vehiculo VARCHAR(255) DEFAULT NULL;");
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
