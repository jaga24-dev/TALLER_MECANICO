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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

/**
 * Panel de contenido principal del dashboard.
 * Contiene header, indicadores clave con iconos PNG y sección de estados.
 */
public class MainContentPanel extends JPanel {

    private static final Color BG_LIGHT = Color.decode("#EBF0F5");
    private static final Color HEADER_BG = Color.decode("#00314A");
    private static final Color GOLD = Color.decode("#FFB800");
    private static final Color TEAL_DARK = Color.decode("#005064");
    private static final Color BORDER_TEAL = Color.decode("#008296");

    public MainContentPanel() {
        setBackground(BG_LIGHT);
        setLayout(new BorderLayout());

        JPanel header = createHeader();

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JLabel indicadoresTitle = new JLabel("INDICADORES CLAVE");
        indicadoresTitle.setFont(new Font("Inter", Font.BOLD, 16));
        indicadoresTitle.setForeground(GOLD);
        indicadoresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(indicadoresTitle);
        body.add(Box.createVerticalStrut(12));

        // Iconos PNG para las tarjetas
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 14, 14));
        cardsGrid.setOpaque(false);
        cardsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        cardsGrid.add(new IndicatorCard("VEHÍCULOS DE HOY", "0",
                "0 Ingresados, 0 Entregados", "vehiculo.png"));
        cardsGrid.add(new IndicatorCard("TRABAJOS EN CURSO", "0",
                "1 Reparación", "trabajos.png"));
        cardsGrid.add(new IndicatorCard("INGRESOS SEMANALES", "$0 MXN",
                "+0% de ayer", "ingresos.png"));
        cardsGrid.add(new IndicatorCard("EFICIENCIA DEL TALLER", "0%",
                "Tiempo promedio: 3.0 hrs", "eficiencia.png"));

        body.add(cardsGrid);
        body.add(Box.createVerticalStrut(18));

        JLabel estadosTitle = new JLabel("ESTADOS");
        estadosTitle.setFont(new Font("Inter", Font.BOLD, 16));
        estadosTitle.setForeground(TEAL_DARK);
        estadosTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(estadosTitle);
        body.add(Box.createVerticalStrut(10));

        JPanel entregasCard = createEntregasCard();
        entregasCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(entregasCard);

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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_BG,
                        getWidth(), 0, Color.decode("#004664"));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(10, 5,
                        getWidth() - 20, getHeight() - 10, 16, 16));
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 60));
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 25, 5, 25));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel dashTitle = new JLabel("DASHBOARD DE RENDIMIENTO");
        dashTitle.setFont(new Font("Inter", Font.BOLD, 13));
        dashTitle.setForeground(GOLD);

        // Fecha dinámica
        String fecha = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE d MMM yyyy", new Locale("es", "MX")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        JLabel dashDate = new JLabel(fecha);
        dashDate.setFont(new Font("Inter", Font.PLAIN, 11));
        dashDate.setForeground(GOLD);

        leftPanel.add(dashTitle);
        leftPanel.add(dashDate);

        JLabel techLabel = new JLabel("  Técnico: Juan Angel  ");
        techLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        techLabel.setForeground(GOLD);
        techLabel.setIcon(IconoManager.cargarIcono("tecnico.png", 16, 16));
        techLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

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
        entregaItem.setForeground(Color.decode("#323C46"));

        card.add(entregaTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(entregaItem);

        return card;
    }

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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.draw(new RoundRectangle2D.Float(
                    x + strokeWidth / 2f, y + strokeWidth / 2f,
                    w - strokeWidth, h - strokeWidth, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(strokeWidth + 2, strokeWidth + 2,
                    strokeWidth + 2, strokeWidth + 2);
        }
    }
}
