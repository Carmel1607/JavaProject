package fr.sejourfacile.util;

import com.formdev.flatlaf.FlatClientProperties;
import fr.sejourfacile.dao.NotificationDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class BoutonCloche extends JButton {
    private int idUtilisateur;
    private int nbNonLues = 0;

    public BoutonCloche(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        putClientProperties();
        actualiserBadge();

        addActionListener(e -> ouvrirMenuNotifications());
    }

    private void putClientProperties() {
        putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
    }

    public void actualiserBadge() {
        this.nbNonLues = NotificationDAO.getNbNonLues(idUtilisateur);
        if (nbNonLues > 0) {
            setText("🔔 " + nbNonLues);
            setBackground(new Color(254, 226, 226));
            setForeground(new Color(220, 38, 38));
        } else {
            setText("🔔");
            setBackground(Color.WHITE);
            setForeground(new Color(100, 116, 139));
        }
        repaint();
    }

    private void ouvrirMenuNotifications() {
        ArrayList<String> notifs = NotificationDAO.getNotifications(idUtilisateur);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Centre de Notifications", true);
        dialog.setSize(480, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        JLabel lblTitre = new JLabel("Notifications (" + nbNonLues + " non lue" + (nbNonLues > 1 ? "s" : "") + ")");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblTitre, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        if (notifs.isEmpty()) {
            model.addElement("Aucune notification pour le moment.");
        } else {
            for (String n : notifs) model.addElement(n);
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnMarquerLu = new JButton("✔ Tout marquer comme lu");
        btnMarquerLu.setBackground(new Color(37, 99, 235));
        btnMarquerLu.setForeground(Color.WHITE);
        btnMarquerLu.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnMarquerLu.addActionListener(ev -> {
            NotificationDAO.marquerToutCommeLu(idUtilisateur);
            actualiserBadge();
            dialog.dispose();
        });
        panel.add(btnMarquerLu, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
