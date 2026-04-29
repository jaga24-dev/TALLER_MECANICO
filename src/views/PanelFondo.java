package views;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Panel que dibuja una imagen de fondo escalada (cover) con un gradiente
 * oscuro superpuesto en la mitad derecha para el formulario de login.
 */
public class PanelFondo extends JPanel {

    private BufferedImage imagenFondo;

    public PanelFondo(String rutaImagen) {
        setOpaque(true);
        try {
            File archivo = new File(rutaImagen);
            if (archivo.exists()) {
                imagenFondo = ImageIO.read(archivo);
            } else {
                System.out.println("Imagen de fondo no encontrada: " + archivo.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error cargando imagen de fondo: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        if (imagenFondo != null) {
            // Cover: escalar para cubrir todo el panel
            double scaleX = (double) w / imagenFondo.getWidth();
            double scaleY = (double) h / imagenFondo.getHeight();
            double scale = Math.max(scaleX, scaleY);
            int imgW = (int) (imagenFondo.getWidth() * scale);
            int imgH = (int) (imagenFondo.getHeight() * scale);
            int imgX = (w - imgW) / 2;
            int imgY = (h - imgH) / 2;
            g2.drawImage(imagenFondo, imgX, imgY, imgW, imgH, null);
        } else {
            // Fallback: gradiente oscuro
            GradientPaint gp = new GradientPaint(
                    0, 0, Color.decode("#00283C"),
                    w, h, Color.decode("#001928"));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }

        // Gradiente oscuro en la mitad derecha (para el formulario)
        GradientPaint overlay = new GradientPaint(
                (int) (w * 0.35), 0, new Color(0x00, 0x1E, 0x32, 0),     // #001E32 alpha=0
                (int) (w * 0.55), 0, new Color(0x00, 0x1E, 0x32, 230));  // #001E32 alpha=230
        g2.setPaint(overlay);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }
}
