package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Diálogo modal de error personalizado.
 * Muestra un candado con error, "¡ATENCIÓN!", mensaje y botón REINTENTAR.
 * Diseño: fondo semitransparente oscuro + tarjeta con borde dorado.
 */
public class DialogoError extends JDialog {

    private static final Color GOLD = Color.decode("#FFB800");
    private static final Color BG_OVERLAY = new Color(0x00, 0x14, 0x23, 180);   // #001423 alpha=180
    private static final Color CARD_BG = new Color(0x00, 0x28, 0x3C, 240);      // #00283C alpha=240
    private static final Color TEXT_WHITE = Color.decode("#F0F0F0");

    public DialogoError(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG_OVERLAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);

        // Tarjeta central
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(340, 230));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Icono de candado con error (PNG)
        JLabel iconLabel = new JLabel(IconoManager.cargarIcono("candado error.png", 50, 50));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(100, 60));
        iconPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconPanel.add(iconLabel);

        JLabel titulo = new JLabel("¡ATENCIÓN!");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg1 = new JLabel("Usuario o contraseña incorrectos.");
        msg1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        msg1.setForeground(TEXT_WHITE);
        msg1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg2 = new JLabel("Por favor, inténtelo de nuevo");
        msg2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        msg2.setForeground(TEXT_WHITE);
        msg2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón REINTENTAR
        JButton btnReintentar = new JButton("REINTENTAR") {
            boolean hover = false;
            {
                setFont(new Font("Inter", Font.BOLD, 13));
                setForeground(GOLD);
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setPreferredSize(new Dimension(160, 36));
                setMaximumSize(new Dimension(160, 36));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (hover) {
                    g2.setColor(new Color(0xFF, 0xB8, 0x00, 30)); // #FFB800 alpha=30
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.setFont(getFont());
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnReintentar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReintentar.addActionListener(e -> dispose());

        card.add(iconPanel);
        card.add(Box.createVerticalStrut(5));
        card.add(titulo);
        card.add(Box.createVerticalStrut(8));
        card.add(msg1);
        card.add(msg2);
        card.add(Box.createVerticalStrut(15));
        card.add(btnReintentar);

        overlay.add(card);
        setContentPane(overlay);
    }

    /** Muestra el diálogo de error centrado sobre el frame padre. */
    public static void mostrar(JFrame parent) {
        DialogoError dialog = new DialogoError(parent);
        dialog.setVisible(true);
    }
}
