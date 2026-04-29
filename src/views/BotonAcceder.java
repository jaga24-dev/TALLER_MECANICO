package views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Botón estilizado "ACCEDER" con fondo teal, bordes redondeados,
 * icono PNG de volante y efecto hover.
 */
public class BotonAcceder extends JButton {

    private static final Color BG_NORMAL = Color.decode("#014461");
    private static final Color BG_HOVER = Color.decode("#148296");
    private static final Color BG_PRESSED = Color.decode("#0F5564");
    private static final Color TEXT_COLOR = Color.decode("#E4C25E");

    private boolean hovering = false;
    private final ImageIcon iconoVolante;

    public BotonAcceder() {
        super("ACCEDER");
        setFont(new Font("Inter", Font.BOLD, 16));
        setForeground(TEXT_COLOR);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(260, 45));

        iconoVolante = IconoManager.cargarIcono("entrar.png", 22, 22);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = getModel().isPressed() ? BG_PRESSED : hovering ? BG_HOVER : BG_NORMAL;
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));

        // Texto
        g2.setFont(getFont());
        g2.setColor(TEXT_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        String texto = getText();
        int totalW = fm.stringWidth(texto) + 30;
        int startX = (getWidth() - totalW) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(texto, startX, textY);

        // Icono PNG de volante a la derecha del texto
        int iconX = startX + fm.stringWidth(texto) + 8;
        int iconY = (getHeight() - 22) / 2;
        iconoVolante.paintIcon(this, g2, iconX, iconY);

        g2.dispose();
    }
}
