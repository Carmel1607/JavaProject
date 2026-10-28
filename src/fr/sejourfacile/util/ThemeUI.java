package fr.sejourfacile.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemeUI {
    public static final Color BG_APP = new Color(241, 245, 249);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color TEXT_MAIN = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(226, 232, 240);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_KPI_NUM = new Font("Segoe UI", Font.BOLD, 22);

    public static Border creerBordureCarte() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        );
    }
}
