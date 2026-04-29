package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SidebarPanel extends JPanel {

    private static final Color BG_DARK = new Color(0, 49, 74);       // #00314A
    private static final Color BG_MEDIUM = new Color(0, 69, 97);     // #004561
    private static final Color GOLD = Color.decode("#E4C25E");        // #FFB800
    private static final Color TEXT_WHITE = new Color(220, 230, 240);
    private static final Color HOVER_BG = new Color(0, 80, 110);

    private final String[] menuLabels = {
        "Dashboard", "Consultar clientes", "Crear orden",
        "Órdenes de servicio", "Vehículos"
    };
    private final String[] menuIcons = {
        "\u2261", "\uD83D\uDD0D", "\u2295", "\uD83D\uDCCB", "\uD83D\uDE97"
    };

    private int selectedIndex = 0;

    public SidebarPanel() {
        setPreferredSize(new Dimension(200, 0));
        setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // --- Top: Logo Area ---
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 10, 15));

        // TM / U logo text
        JLabel logoTM = new JLabel("TM");
        logoTM.setFont(new Font("SansSerif", Font.BOLD, 28));
        logoTM.setForeground(GOLD);
        logoTM.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoU = new JLabel("U");
        logoU.setFont(new Font("SansSerif", Font.BOLD, 36));
        logoU.setForeground(GOLD);
        logoU.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><center>UABCS - TALLER<br>MECANICO</center></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        logoPanel.add(logoTM);
        logoPanel.add(logoU);
        logoPanel.add(Box.createVerticalStrut(6));
        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(20));

        // --- Center: Menu Items ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        for (int i = 0; i < menuLabels.length; i++) {
            menuPanel.add(createMenuItem(menuIcons[i], menuLabels[i], i));
            menuPanel.add(Box.createVerticalStrut(4));
        }

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(menuPanel, BorderLayout.CENTER);

        // --- Bottom: Cerrar sesión ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        bottomPanel.setOpaque(false);
        JLabel logoutIcon = new JLabel("\uD83D\uDEAA");
        logoutIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutIcon.setForeground(GOLD);
        JLabel logoutLabel = new JLabel("Cerrar sesión");
        logoutLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        logoutLabel.setForeground(GOLD);
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(logoutIcon);
        bottomPanel.add(logoutLabel);

        add(topSection, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createMenuItem(String icon, String text, int index) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (index == selectedIndex) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_MEDIUM);
                    g2.fill(new RoundRectangle2D.Float(5, 0, getWidth() - 10, getHeight(), 12, 12));
                    // Gold left accent bar
                    g2.setColor(GOLD);
                    g2.fill(new RoundRectangle2D.Float(5, 2, 4, getHeight() - 4, 4, 4));
                    g2.dispose();
                }
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(200, 40));
        item.setPreferredSize(new Dimension(200, 40));
        item.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        iconLabel.setForeground(index == selectedIndex ? GOLD : GOLD);
        iconLabel.setPreferredSize(new Dimension(28, 30));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textLabel.setForeground(index == selectedIndex ? GOLD : GOLD);

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textLabel, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (index != selectedIndex) {
                    item.setBackground(HOVER_BG);
                    item.setOpaque(true);
                    item.repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                item.setOpaque(false);
                item.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedIndex = index;
                // repaint entire sidebar to update selection
                SidebarPanel.this.repaint();
            }
        });

        return item;
    }
}
