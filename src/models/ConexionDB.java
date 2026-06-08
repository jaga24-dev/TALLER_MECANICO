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
        
        // 1. Intentar cargar desde el classpath (cuando está dentro del .jar)
        try (java.io.InputStream is = ConexionDB.class.getResourceAsStream("/.env")) {
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(is))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith("#")) continue;
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            env.put(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
                return; // Si se cargó correctamente desde el classpath, terminamos
            }
        } catch (Exception e) {
            // Silencioso, continuamos con la lectura de archivo
        }

        // 2. Fallback: Intentar cargar desde el sistema de archivos
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(".env"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    env.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar el archivo .env desde el classpath ni del archivo local: " + e.getMessage());
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
                    try (java.sql.Statement stmt2 = sharedConnection.createStatement()) {
                        stmt2.execute("CREATE TABLE IF NOT EXISTS detalle_orden (" +
                                "id_detalle INT AUTO_INCREMENT PRIMARY KEY, " +
                                "id_orden INT NOT NULL, " +
                                "concepto VARCHAR(255) NOT NULL, " +
                                "precio DECIMAL(10,2) NOT NULL, " +
                                "FOREIGN KEY (id_orden) REFERENCES ordenes_servicio(id_orden) ON DELETE CASCADE" +
                                ")");
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
