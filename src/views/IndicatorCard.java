package views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

public class IndicatorCard extends JPanel {

    private static final Color BORDER_TEAL = Color.decode("#008296");
    private static final Color TITLE_COLOR = Color.decode("#006478");
    private static final Color VALUE_COLOR = Color.decode("#003246");
    private static final Color SUB_COLOR = Color.decode("#647882");

    public IndicatorCard(String title, String value, String subtitle, String iconFile) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(BORDER_TEAL, 10, 2),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 11));
        titleLabel.setForeground(TITLE_COLOR);
        JLabel iconLabel = new JLabel(IconoManager.cargarIcono(iconFile, 28, 28));
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(iconLabel, BorderLayout.EAST);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 26));
        valueLabel.setForeground(VALUE_COLOR);
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        subLabel.setForeground(SUB_COLOR);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(valueLabel);
        centerPanel.add(Box.createVerticalStrut(2));
        centerPanel.add(subLabel);

        add(topRow, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int strokeWidth;
        RoundBorder(Color c, int r, int s) { color = c; radius = r; strokeWidth = s; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.draw(new RoundRectangle2D.Float(x + strokeWidth / 2f, y + strokeWidth / 2f,
                    w - strokeWidth, h - strokeWidth, radius, radius));
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(strokeWidth + 2, strokeWidth + 2, strokeWidth + 2, strokeWidth + 2);
        }
    }
}
