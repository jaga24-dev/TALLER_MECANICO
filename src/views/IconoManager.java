package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Gestor de iconos PNG.
 * Carga imágenes desde recursos/iconos/ y las escala al tamaño deseado.
 * Si el archivo no existe, genera un icono de respaldo con la primera letra.
 *
 * USO: IconoManager.cargarIcono("dashboard.png", 24, 24);
 */
public class IconoManager {

    private static final String RUTA_ICONOS = "src" + File.separator + "img" + File.separator;
    private static final Map<String, ImageIcon> cache = new HashMap<>();

    /**
     * Carga un icono PNG y lo escala al tamaño indicado.
     * @param nombre Nombre del archivo (ej: "dashboard.png")
     * @param ancho  Ancho deseado en píxeles
     * @param alto   Alto deseado en píxeles
     * @return ImageIcon escalado
     */
    public static ImageIcon cargarIcono(String nombre, int ancho, int alto) {
        String clave = nombre + "_" + ancho + "x" + alto;
        if (cache.containsKey(clave)) {
            return cache.get(clave);
        }

        File archivo = new File(RUTA_ICONOS + nombre);
        if (archivo.exists()) {
            try {
                BufferedImage img = ImageIO.read(archivo);
                Image escalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                ImageIcon icono = new ImageIcon(escalada);
                cache.put(clave, icono);
                return icono;
            } catch (IOException e) {
                System.err.println("Error cargando icono: " + nombre);
            }
        } else {
            System.out.println("Icono no encontrado: " + archivo.getAbsolutePath()
                    + " — usando respaldo.");
        }

        ImageIcon fallback = crearIconoFallback(nombre, ancho, alto);
        cache.put(clave, fallback);
        return fallback;
    }

    /** Genera un icono circular dorado con la primera letra del nombre. */
    private static ImageIcon crearIconoFallback(String nombre, int ancho, int alto) {
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0xFF, 0xB8, 0x00, 180)); // #FFB800 alpha=180
        g2.fillOval(1, 1, ancho - 2, alto - 2);

        g2.setColor(Color.decode("#00314A"));
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.min(ancho, alto) / 2));
        String letra = nombre.replace(".png", "").substring(0, 1).toUpperCase();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(letra, (ancho - fm.stringWidth(letra)) / 2,
                (alto + fm.getAscent() - fm.getDescent()) / 2);

        g2.dispose();
        return new ImageIcon(img);
    }

    public static void limpiarCache() {
        cache.clear();
    }
}
