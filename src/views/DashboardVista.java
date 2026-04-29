package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Vista principal del Dashboard.
 * Contiene el SidebarPanel (izquierda) y MainContentPanel (centro).
 */
public class DashboardVista extends JFrame {

    private SidebarPanel sidebar;
    private MainContentPanel mainContent;

    public DashboardVista() {
        setTitle("UABCS - Taller Mecánico | Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        sidebar = new SidebarPanel();
        mainContent = new MainContentPanel();

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    public SidebarPanel getSidebar() {
        return sidebar;
    }

    public MainContentPanel getMainContent() {
        return mainContent;
    }
}
