package fr.sejourfacile.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoutonModerne extends JButton {
    private Color couleurNormale;
    private Color couleurHover;
    private boolean isHovered = false;

    public BoutonModerne(String texte, Color bg, Color fg) {
        super(texte);
        this.couleurNormale = bg;
        this.couleurHover = bg.darker();

        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setForeground(fg);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHovered ? couleurHover : couleurNormale);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(couleurNormale.darker());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
