package views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

public class MainContentPanel extends JPanel {

    private static final Color BG_LIGHT = new Color(235, 240, 245);
    private static final Color HEADER_BG = new Color(0, 49, 74);
    private static final Color GOLD = new Color(255, 184, 0);
    private static final Color TEAL_DARK = new Color(0, 80, 100);
    private static final Color BORDER_TEAL = new Color(0, 130, 150);

    public MainContentPanel() {
        setBackground(BG_LIGHT);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = createHeader();

        // --- BODY (scrollable) ---
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // "INDICADORES CLAVE" title
        JLabel indicadoresTitle = new JLabel("INDICADORES CLAVE");
        indicadoresTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        indicadoresTitle.setForeground(GOLD);
        indicadoresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(indicadoresTitle);
        body.add(Box.createVerticalStrut(12));

        // indicator cards grid (2x2)
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 14, 14));
        cardsGrid.setOpaque(false);
        cardsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        cardsGrid.add(new IndicatorCard("VEHÍCULOS DE HOY", "0",
                "0 Ingresados, 0 Entregados", "\uD83D\uDE97"));
        cardsGrid.add(new IndicatorCard("TRABAJOS EN CURSO", "0",
                "1 Reparación", "\uD83D\uDD27"));
        cardsGrid.add(new IndicatorCard("INGRESOS SEMANALES", "$0 MXN",
                "+0% de ayer", "\uD83D\uDCB0"));
        cardsGrid.add(new IndicatorCard("EFICIENCIA DEL TALLER", "0%",
                "Tiempo promedio: 3.0 hrs", "\u2699"));

        body.add(cardsGrid);
        body.add(Box.createVerticalStrut(18));

        // "ESTADOS" title
        JLabel estadosTitle = new JLabel("ESTADOS");
        estadosTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        estadosTitle.setForeground(TEAL_DARK);
        estadosTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(estadosTitle);
        body.add(Box.createVerticalStrut(10));

        // Próximas Entregas card
        JPanel entregasCard = createEntregasCard();
        entregasCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(entregasCard);

        // Wrap body in scroll pane
        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_BG, getWidth(), 0, new Color(0, 70, 100));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(10, 5, getWidth() - 20, getHeight() - 10, 16, 16));
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 60));
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 25, 5, 25));

        // Left: Dashboard title
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JLabel dashTitle = new JLabel("DASHBOARD DE RENDIMIENTO");
        dashTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        dashTitle.setForeground(GOLD);
        JLabel dashDate = new JLabel("Sábado 4 abr 2026");
        dashDate.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dashDate.setForeground(GOLD);
        leftPanel.add(dashTitle);
        leftPanel.add(dashDate);

        // Right: Técnico badge
        JLabel techLabel = new JLabel("  \u2699 Técnico: Juan Angel  ");
        techLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        techLabel.setForeground(GOLD);
        techLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        techLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(techLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createEntregasCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_TEAL, 10, 2),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel entregaTitle = new JLabel("PRÓXIMAS ENTREGAS (Hoy)");
        entregaTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        entregaTitle.setForeground(GOLD);

        JLabel entregaItem = new JLabel("  1.  Ford Explorer 2012 (Santiago De Anda)");
        entregaItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        entregaItem.setForeground(new Color(50, 60, 70));

        card.add(entregaTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(entregaItem);

        return card;
    }

    // Rounded border implementation
    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int strokeWidth;

        RoundBorder(Color color, int radius, int strokeWidth) {
            this.color = color;
            this.radius = radius;
            this.strokeWidth = strokeWidth;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.draw(new RoundRectangle2D.Float(
                x + strokeWidth / 2f, y + strokeWidth / 2f,
                w - strokeWidth, h - strokeWidth,
                radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(strokeWidth + 2, strokeWidth + 2, strokeWidth + 2, strokeWidth + 2);
        }
    }
}
