package fr.sejourfacile.util;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DonutChartPanel extends JPanel {
    private Map<String, Integer> donnees = new LinkedHashMap<>();
    private Map<String, Color> couleurs = new LinkedHashMap<>();

    public DonutChartPanel(String titre) {
        setOpaque(false);
        setLayout(new BorderLayout());
        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitre.setForeground(new Color(30, 41, 59));
        lblTitre.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        add(lblTitre, BorderLayout.NORTH);
    }

    public void setDonnees(int valides, int attente, int manquant, int rejetes) {
        donnees.clear();
        couleurs.clear();

        donnees.put("Validés", Math.max(0, valides));
        couleurs.put("Validés", new Color(16, 185, 129));

        donnees.put("En attente", Math.max(0, attente));
        couleurs.put("En attente", new Color(245, 158, 11));

        donnees.put("Doc manquant", Math.max(0, manquant));
        couleurs.put("Doc manquant", new Color(239, 68, 68));

        donnees.put("Rejetés", Math.max(0, rejetes));
        couleurs.put("Rejetés", new Color(99, 102, 241));

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int total = donnees.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) total = 1;

        int w = getWidth();
        int h = getHeight() - 30;
        int diametre = Math.min(w / 2, h - 20);
        int x = 15;
        int y = 35 + (h - diametre) / 2;

        double angleDebut = 90;
        for (Map.Entry<String, Integer> entry : donnees.entrySet()) {
            double arc = (entry.getValue() * 360.0) / total;
            g2.setColor(couleurs.get(entry.getKey()));
            g2.fillArc(x, y, diametre, diametre, (int) Math.round(angleDebut), (int) Math.round(arc));
            angleDebut += arc;
        }

        int trou = (int) (diametre * 0.55);
        int xt = x + (diametre - trou) / 2;
        int yt = y + (diametre - trou) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(xt, yt, trou, trou);

        g2.setColor(new Color(15, 23, 42));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String strTotal = String.valueOf(donnees.values().stream().mapToInt(Integer::intValue).sum());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(strTotal, xt + (trou - fm.stringWidth(strTotal)) / 2, yt + (trou / 2) + 2);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(148, 163, 184));
        fm = g2.getFontMetrics();
        g2.drawString("DOSSIERS", xt + (trou - fm.stringWidth("DOSSIERS")) / 2, yt + (trou / 2) + 16);

        int lx = x + diametre + 20;
        int ly = y + 20;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

        for (Map.Entry<String, Integer> entry : donnees.entrySet()) {
            g2.setColor(couleurs.get(entry.getKey()));
            g2.fillRoundRect(lx, ly - 10, 12, 12, 4, 4);

            g2.setColor(new Color(30, 41, 59));
            int pct = (entry.getValue() * 100) / total;
            String text = entry.getKey() + " (" + entry.getValue() + " • " + pct + "%)";
            g2.drawString(text, lx + 18, ly);
            ly += 24;
        }

        g2.dispose();
    }
}
