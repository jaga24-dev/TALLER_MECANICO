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

/**
 * Panel lateral del dashboard con logo, menú de navegación e iconos PNG.
 * Los iconos se cargan desde recursos/iconos/ via IconoManager.
 * Botón "Cerrar sesión" notifica al controlador.
 */
public class SidebarPanel extends JPanel {

    private static final Color BG_DARK = Color.decode("#00314A");
    private static final Color BG_MEDIUM = Color.decode("#004561");
    private static final Color GOLD = Color.decode("#E4C25E");
    private static final Color HOVER_BG = Color.decode("#00506E");

    // Nombres de los iconos PNG (el usuario los coloca en recursos/iconos/)
    private final String[] menuLabels = {
            "Dashboard", "Consultar clientes", "Crear orden",
            "Órdenes de servicio", "Vehículos"
    };
    private final String[] menuIconFiles = {
            "dashboard.png", "buscar.png", "crear_orden.png",
            "ordenes.png", "vehiculo.png"
    };

    private int selectedIndex = 0;
    private Runnable onCerrarSesion;
    private OnMenuSelectedListener onMenuSelectedListener;
    
    public interface OnMenuSelectedListener {
        void onMenuSelected(int index, String title);
    }

    public SidebarPanel() {
        setPreferredSize(new Dimension(200, 0));
        setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // --- Logo ---
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 8, 15));
        
        //TM
        JLabel tm = new JLabel("<html><center>TM<br></center></html>");
        tm.setFont(new Font("Inter", Font.BOLD, 31));
        tm.setForeground(GOLD);
        tm.setAlignmentX(Component.CENTER_ALIGNMENT);
        tm.setHorizontalAlignment(SwingConstants.CENTER);

        // Logo PNG
        JLabel logoLabel = new JLabel(IconoManager.cargarIcono("U.png", 100, 75));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><center>UABCS - TALLER<br>MECANICO</center></html>");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 11));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        logoPanel.add(tm);
        logoPanel.add(Box.createVerticalStrut(6));
        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(6));
        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(15));

        // --- Menú ---
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < menuLabels.length; i++) {
            menuPanel.add(createMenuItem(menuIconFiles[i], menuLabels[i], i));
            menuPanel.add(Box.createVerticalStrut(4));
        }

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(menuPanel, BorderLayout.CENTER);

        // --- Cerrar sesión ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        bottomPanel.setOpaque(false);

        JLabel logoutIcon = new JLabel(IconoManager.cargarIcono("cerrar sesion.png", 18, 18));
        JLabel logoutLabel = new JLabel("Cerrar sesión");
        logoutLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        logoutLabel.setForeground(GOLD);
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onCerrarSesion != null) onCerrarSesion.run();
            }
        });

        bottomPanel.add(logoutIcon);
        bottomPanel.add(logoutLabel);

        add(topSection, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createMenuItem(String iconFile, String text, int index) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (index == selectedIndex) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_MEDIUM);
                    g2.fill(new RoundRectangle2D.Float(5, 0, getWidth() - 10,
                            getHeight(), 12, 12));
                    g2.setColor(GOLD);
                    g2.fill(new RoundRectangle2D.Float(5, 2, 4,
                            getHeight() - 4, 4, 4));
                    g2.dispose();
                }
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(200, 40));
        item.setPreferredSize(new Dimension(200, 40));
        item.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icono PNG
        JLabel iconLabel = new JLabel(IconoManager.cargarIcono(iconFile, 20, 20));
        iconLabel.setPreferredSize(new Dimension(28, 30));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        textLabel.setForeground(GOLD);

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
            public void mousePressed(MouseEvent e) {
                selectedIndex = index;
                SidebarPanel.this.repaint();
                if (onMenuSelectedListener != null) {
                    onMenuSelectedListener.onMenuSelected(index, text);
                }
            }
        });

        return item;
    }

    /** Registra callback para cuando se hace clic en "Cerrar sesión". */
    public void setOnCerrarSesion(Runnable callback) {
        this.onCerrarSesion = callback;
    }
    
    public void setOnMenuSelectedListener(OnMenuSelectedListener listener) {
        this.onMenuSelectedListener = listener;
    }
}
