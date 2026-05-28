package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class ConexionDB {

    private static Map<String, String> env = null;
    private static Connection sharedConnection = null;
    private static boolean alterEjecutado = false;

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

    public static synchronized Connection obtenerConexion() {
        cargarEnv();
        try {
            if (sharedConnection == null || sharedConnection.isClosed() || !sharedConnection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = env.getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/taller_mecanico");
                String user = env.getOrDefault("DB_USER", "root");
                String pass = env.getOrDefault("DB_PASSWORD", "");
                
                sharedConnection = DriverManager.getConnection(url, user, pass);
                
                if (!alterEjecutado) {
                    try (java.sql.Statement stmt = sharedConnection.createStatement()) {
                        stmt.execute("ALTER TABLE vehiculos ADD COLUMN imagen_vehiculo VARCHAR(255) DEFAULT NULL;");
                    } catch (Exception ignored) {
                    }
                    alterEjecutado = true;
                }
            }
            
            // Retornamos un Proxy que ignora el método close() para que try-with-resources no cierre la conexión compartida
            return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                new java.lang.reflect.InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("close")) {
                            return null; // Ignoramos el close()
                        }
                        return method.invoke(sharedConnection, args);
                    }
                }
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
