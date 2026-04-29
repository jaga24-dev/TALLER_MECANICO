package views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

/**
 * Campo de texto personalizado con borde redondeado dorado.
 * Soporta estado de error (fondo rosa + mensaje "Obligatorio").
 */
public class CampoTexto extends JPanel {

    private static final Color GOLD = Color.decode("#FFB800");
    private static final Color BG_NORMAL = Color.decode("#FFFFF5");
    private static final Color BG_ERROR = Color.decode("#FFD2D2");
    private static final Color TEXT_ERROR = Color.decode("#FFC1B9");

    private JTextField campo;
    private JLabel labelError;
    private boolean esPassword;

    public CampoTexto(boolean esPassword) {
        this.esPassword = esPassword;
        setOpaque(false);
        setLayout(new BorderLayout());

        if (esPassword) {
            JPasswordField pf = new JPasswordField(20);
            pf.setEchoChar('●');
            campo = pf;
        } else {
            campo = new JTextField(20);
        }

        campo.setFont(new Font("Inter", Font.PLAIN, 14));
        campo.setBackground(BG_NORMAL);
        campo.setForeground(Color.decode("#1E1E1E"));
        campo.setCaretColor(Color.decode("#00314A"));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new BordeRedondeado(GOLD, 10, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campo.setPreferredSize(new Dimension(260, 38));

        labelError = new JLabel("⚠ Obligatorio");
        labelError.setFont(new Font("Inter", Font.PLAIN, 11));
        labelError.setForeground(TEXT_ERROR);
        labelError.setHorizontalAlignment(SwingConstants.RIGHT);
        labelError.setVisible(false);

        add(campo, BorderLayout.CENTER);
        add(labelError, BorderLayout.SOUTH);
    }

    public String getTexto() {
        if (esPassword) {
            return new String(((JPasswordField) campo).getPassword());
        }
        return campo.getText().trim();
    }

    public void setError(boolean error) {
        campo.setBackground(error ? BG_ERROR : BG_NORMAL);
        labelError.setVisible(error);
        repaint();
    }

    public void limpiar() {
        campo.setText("");
        setError(false);
    }

    public JTextField getCampo() {
        return campo;
    }

    /** Borde redondeado para el campo de texto. */
    private static class BordeRedondeado extends AbstractBorder {
        private final Color color;
        private final int radio;
        private final int grosor;

        BordeRedondeado(Color color, int radio, int grosor) {
            this.color = color;
            this.radio = radio;
            this.grosor = grosor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor));
            g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, w - 2, h - 2, radio, radio));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(grosor + 1, grosor + 1, grosor + 1, grosor + 1);
        }
    }
}
