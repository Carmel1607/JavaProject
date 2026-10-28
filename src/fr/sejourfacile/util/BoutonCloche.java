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

    // Couleurs de la charte (Indigo profond & Bleu roi - Aucun vert)
    private static final Color BLEU_BRAND = new Color(37, 99, 235);     // #2563EB
    private static final Color INDIGO_TEXT = new Color(26, 16, 83);     // #1A1053
    private static final Color BG_UNREAD = new Color(238, 242, 255);    // Teinte bleue très douce pour non lu
    private static final Color BG_SECTION = new Color(248, 250, 252);   // Barre de section "Aujourd'hui"

    public BoutonCloche(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        actualiserBadge();

        addActionListener(e -> ouvrirPopupNotifications());
    }

    public void actualiserBadge() {
        this.nbNonLues = NotificationDAO.getNbNonLues(idUtilisateur);
        if (nbNonLues > 0) {
            setText("🔔 " + nbNonLues);
            setBackground(new Color(254, 226, 226)); // Alerte rouge discrète
            setForeground(new Color(220, 38, 38));
        } else {
            setText("🔔");
            setBackground(Color.WHITE);
            setForeground(new Color(100, 116, 139));
        }
        repaint();
    }

    private void ouvrirPopupNotifications() {
        ArrayList<String> notifs = NotificationDAO.getNotifications(idUtilisateur);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), false);
        dialog.setUndecorated(true);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);

        // Carte Blanche Flottante avec coins très arrondis (Style exact de votre capture)
        JPanel carteRoot = new JPanel(new BorderLayout());
        carteRoot.setBackground(Color.WHITE);
        carteRoot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));
        carteRoot.putClientProperty(FlatClientProperties.STYLE, "arc: 24");

        // 1. EN-TÊTE : "Notifications" à gauche, "✔ Mark all as read" à droite
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel lblTitre = new JLabel("Notifications");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitre.setForeground(INDIGO_TEXT);
        header.add(lblTitre, BorderLayout.WEST);

        JButton btnMarquerLu = new JButton("✔ Tout marquer comme lu");
        btnMarquerLu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMarquerLu.setForeground(BLEU_BRAND);
        btnMarquerLu.setContentAreaFilled(false);
        btnMarquerLu.setBorderPainted(false);
        btnMarquerLu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMarquerLu.addActionListener(ev -> {
            NotificationDAO.marquerToutCommeLu(idUtilisateur);
            actualiserBadge();
            dialog.dispose();
        });
        header.add(btnMarquerLu, BorderLayout.EAST);
        carteRoot.add(header, BorderLayout.NORTH);

        // 2. CORPS : Séparateur "Today" + Liste des items avec icône ronde
        JPanel corps = new JPanel(new BorderLayout());
        corps.setOpaque(false);

        // Barre grise de date "Today / Aujourd'hui"
        JPanel sectionToday = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        sectionToday.setBackground(BG_SECTION);
        sectionToday.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(226, 232, 240)));
        JLabel lblToday = new JLabel("Aujourd'hui");
        lblToday.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblToday.setForeground(new Color(100, 116, 139));
        sectionToday.add(lblToday);
        corps.add(sectionToday, BorderLayout.NORTH);

        // Conteneur des notifications
        JPanel panelListe = new JPanel();
        panelListe.setLayout(new BoxLayout(panelListe, BoxLayout.Y_AXIS));
        panelListe.setBackground(Color.WHITE);

        if (notifs.isEmpty()) {
            JPanel pVide = new JPanel(new FlowLayout(FlowLayout.CENTER));
            pVide.setOpaque(false);
            pVide.setBorder(new EmptyBorder(40, 0, 40, 0));
            JLabel l = new JLabel("Aucune notification pour le moment.");
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setForeground(Color.GRAY);
            pVide.add(l);
            panelListe.add(pVide);
        } else {
            for (String raw : notifs) {
                boolean estNonLu = raw.contains("[Nouveau]");
                String messageClean = raw.replace("🔴 [Nouveau] ", "").replace("⚪ ", "");

                panelListe.add(creerItemNotification(messageClean, estNonLu));
                panelListe.add(Box.createVerticalStrut(2));
            }
        }

        JScrollPane scroll = new JScrollPane(panelListe);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        corps.add(scroll, BorderLayout.CENTER);
        carteRoot.add(corps, BorderLayout.CENTER);

        // 3. PIED DE PAGE : "Fermer le centre de notifications"
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)));

        JButton btnFermer = new JButton("Fermer les notifications");
        btnFermer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFermer.setForeground(INDIGO_TEXT);
        btnFermer.setContentAreaFilled(false);
        btnFermer.setBorderPainted(false);
        btnFermer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFermer.addActionListener(e -> dialog.dispose());
        footer.add(btnFermer);
        carteRoot.add(footer, BorderLayout.SOUTH);

        dialog.add(carteRoot);
        dialog.setVisible(true);
    }

    private JPanel creerItemNotification(String messageBrut, boolean nonLu) {
        JPanel item = new JPanel(new BorderLayout(14, 0));
        item.setBackground(nonLu ? BG_UNREAD : Color.WHITE);
        item.setBorder(new EmptyBorder(12, 14, 12, 14));
        item.putClientProperty(FlatClientProperties.STYLE, "arc: 14");

        // Avatar circulaire gauche
        JPanel avatar = new JPanel(new GridBagLayout());
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setBackground(Color.WHITE);
        avatar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(4, 4, 4, 4)
        ));
        avatar.putClientProperty(FlatClientProperties.STYLE, "arc: 999"); // Cercle parfait

        JLabel icon = new JLabel(nonLu ? "📄" : "🔔");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        avatar.add(icon);
        item.add(avatar, BorderLayout.WEST);

        // Contenu texte (Titre avec puce bleue + Description)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Ligne 1 : Titre + Puce bleue
        JPanel ligneTitre = new JPanel(new BorderLayout());
        ligneTitre.setOpaque(false);

        JLabel lblTitre = new JLabel("<html><font color='#2563EB'>●</font> <b>Mise à jour de dossier</b></html>");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitre.setForeground(new Color(30, 41, 59));
        ligneTitre.add(lblTitre, BorderLayout.WEST);

        JLabel lblTemps = new JLabel("Récemment");
        lblTemps.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTemps.setForeground(new Color(148, 163, 184));
        ligneTitre.add(lblTemps, BorderLayout.EAST);
        textPanel.add(ligneTitre);
        textPanel.add(Box.createVerticalStrut(4));

        // Ligne 2 : Message descriptif
        JLabel lblDesc = new JLabel("<html><p style='width:240px;'>" + messageBrut + "</p></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(71, 85, 105));
        textPanel.add(lblDesc);

        item.add(textPanel, BorderLayout.CENTER);
        return item;
    }
}
