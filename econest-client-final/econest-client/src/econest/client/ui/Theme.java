package econest.client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

// colours and fonts in one place so the screens match
public final class Theme {
    public static final Color MOSS      = new Color(0x2F5233);   // header, primary buttons
    public static final Color MOSS_DARK = new Color(0x1F3A23);
    public static final Color SAGE      = new Color(0xE4EBDD);   // table selection, row stripes
    public static final Color PAPER     = new Color(0xF7F8F4);   // window background
    public static final Color INK       = new Color(0x1E2620);
    public static final Color CLAY      = new Color(0xA64B2A);   // destructive actions
    public static final Color MUTED     = new Color(0x6B7A6C);

    public static final Font BASE  = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font BOLD  = BASE.deriveFont(Font.BOLD);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);

    private Theme() { }

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = button(text, Color.WHITE);
        b.setForeground(MOSS);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MOSS), BorderFactory.createEmptyBorder(7, 15, 7, 15)));
        return b;
    }

    public static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xB9C4B3)), BorderFactory.createEmptyBorder(5, 7, 5, 7));
    }

    public static JTextField textField() {
        JTextField f = new JTextField();
        f.setFont(BASE);
        f.setBorder(fieldBorder());
        return f;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BASE);
        l.setForeground(MUTED);
        return l;
    }
}
