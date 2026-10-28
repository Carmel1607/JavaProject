package fr.sejourfacile.view;

import com.formdev.flatlaf.FlatClientProperties;
import fr.sejourfacile.dao.DossierDAO;
import fr.sejourfacile.dao.NotificationDAO;
import fr.sejourfacile.model.*;
import fr.sejourfacile.util.BoutonCloche;
import fr.sejourfacile.util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class FenetreEtudiant extends JFrame {
    private Etudiant etudiant;
    private DossierDAO dossierDAO;

    private CardLayout cardLayout;
    private JPanel panelContenu;

    private BoutonCloche boutonCloche;

    private JTable tableSuivi;
    private DefaultTableModel modelSuivi;
    private JLabel lblNotification;
    private JPanel panelAlerteManquante;
    private JButton btnAjouterPieceManquante;
    private int idDossierSelectionne = -1;
    private String statutDossierSelectionne = "";

    private JComboBox<TypeDemande> comboType;
    private DefaultListModel<String> modelDocs;
    private ArrayList<File> fichiersSelectionnes;

    public FenetreEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
        this.dossierDAO = new DossierDAO();
        this.fichiersSelectionnes = new ArrayList<>();

        setTitle("SéjourFacile — Portail Étudiant : " + etudiant.getNomComplet());
        setSize(1150, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 245, 249));

        root.add(creerSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelContenu = new JPanel(cardLayout);
        panelContenu.setOpaque(false);
        panelContenu.setBorder(new EmptyBorder(15, 20, 15, 20));

        panelContenu.add(creerVueSuivi(), "SUIVI");
        panelContenu.add(creerVueNouveauDossier(), "NOUVEAU");
        panelContenu.add(creerVueChecklist(), "CHECKLIST");

        root.add(panelContenu, BorderLayout.CENTER);
        add(root);

        rafraichirSuivi();
    }

    private JPanel creerSidebar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setPreferredSize(new Dimension(240, 0));
        sb.setBackground(new Color(26, 16, 83));

        JPanel panelProfil = new JPanel();
        panelProfil.setLayout(new BoxLayout(panelProfil, BoxLayout.Y_AXIS));
        panelProfil.setOpaque(false);
        panelProfil.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel avatar = new JLabel("🎓", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nom = new JLabel(etudiant.getNomComplet());
        nom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nom.setForeground(Color.WHITE);
        nom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel agdref = new JLabel("AGDREF : " + etudiant.getNumeroEtranger());
        agdref.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        agdref.setForeground(new Color(165, 180, 252));
        agdref.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelProfil.add(avatar);
        panelProfil.add(Box.createVerticalStrut(8));
        panelProfil.add(nom);
        panelProfil.add(agdref);
        sb.add(panelProfil, BorderLayout.NORTH);

        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setOpaque(false);
        panelMenu.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel l1 = new JLabel("MES DÉMARCHES");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l1.setForeground(new Color(129, 140, 248));
        panelMenu.add(l1);
        panelMenu.add(Box.createVerticalStrut(5));

        panelMenu.add(creerBoutonMenu("📋 Suivi de mes dossiers", () -> cardLayout.show(panelContenu, "SUIVI")));
        panelMenu.add(Box.createVerticalStrut(8));
        panelMenu.add(creerBoutonMenu("➕ Déposer une nouvelle demande", () -> cardLayout.show(panelContenu, "NOUVEAU")));
        panelMenu.add(Box.createVerticalStrut(8));
        panelMenu.add(creerBoutonMenu("💡 Simulateur & Checklist", () -> cardLayout.show(panelContenu, "CHECKLIST")));

        sb.add(panelMenu, BorderLayout.CENTER);

        JPanel panelBas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBas.setOpaque(false);
        panelBas.setBorder(new EmptyBorder(10, 15, 20, 15));
        JButton btnDeco = new JButton("Déconnexion");
        btnDeco.setPreferredSize(new Dimension(200, 38));
        btnDeco.setBackground(new Color(239, 68, 68));
        btnDeco.setForeground(Color.WHITE);
        btnDeco.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeco.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnDeco.addActionListener(e -> {
            dispose();
            new FenetreConnexion().setVisible(true);
        });
        panelBas.add(btnDeco);
        sb.add(panelBas, BorderLayout.SOUTH);

        return sb;
    }

    private JButton creerBoutonMenu(String texte, Runnable action) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(new Color(49, 46, 129));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel creerVueSuivi() {
        JPanel vue = new JPanel(new BorderLayout(15, 15));
        vue.setOpaque(false);

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Color.WHITE);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 18, 14, 18)
        ));
        banner.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JLabel lblTitre = new JLabel("Suivi et Régularisation de vos démarches de séjour");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        banner.add(lblTitre, BorderLayout.WEST);

        JPanel panelBtnsHaut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtnsHaut.setOpaque(false);

        JButton btnAttest = new JButton("📥 Télécharger Attestation");
        btnAttest.setBackground(new Color(16, 185, 129));
        btnAttest.setForeground(Color.WHITE);
        btnAttest.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnAttest.addActionListener(e -> genererAttestation());

        JButton btnRaf = new JButton("🔄 Rafraîchir");
        btnRaf.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnRaf.addActionListener(e -> rafraichirSuivi());

        boutonCloche = new BoutonCloche(etudiant.getIdUtilisateur());

        panelBtnsHaut.add(btnAttest);
        panelBtnsHaut.add(btnRaf);
        panelBtnsHaut.add(boutonCloche);
        banner.add(panelBtnsHaut, BorderLayout.EAST);
        vue.add(banner, BorderLayout.NORTH);

        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBackground(Color.WHITE);
        panelTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        panelTable.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        String[] cols = {"Référence #", "Démarche sollicitée", "Date de dépôt", "Statut actuel"};
        modelSuivi = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableSuivi = new JTable(modelSuivi);
        tableSuivi.setRowHeight(34);
        tableSuivi.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tableSuivi.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String val = String.valueOf(value);
                if ("VALIDE".equals(val)) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setText("● VALIDÉ");
                } else if ("REJETE".equals(val)) {
                    l.setForeground(new Color(239, 68, 68));
                    l.setText("● REJETÉ");
                } else if ("PIECE_MANQUANTE".equals(val)) {
                    l.setForeground(new Color(245, 158, 11));
                    l.setText("● PIÈCE MANQUANTE (Action requise)");
                } else {
                    l.setForeground(new Color(59, 130, 246));
                    l.setText("● EN ATTENTE D'INSTRUCTION");
                }
                return l;
            }
        });

        tableSuivi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableSuivi.getSelectedRow() != -1) {
                int r = tableSuivi.getSelectedRow();
                String refStr = (String) modelSuivi.getValueAt(r, 0);
                idDossierSelectionne = Integer.parseInt(refStr.replace("#", ""));
                statutDossierSelectionne = (String) modelSuivi.getValueAt(r, 3);
                String notif = (String) tableSuivi.getClientProperty("notif_" + r);

                lblNotification.setText("<html><b>Notification préfecture :</b> " + (notif != null ? notif : "Dossier en attente.") + "</html>");

                boolean estIncomplet = "PIECE_MANQUANTE".equals(statutDossierSelectionne);
                panelAlerteManquante.setVisible(estIncomplet);
                revalidate();
                repaint();
            }
        });

        panelTable.add(new JScrollPane(tableSuivi), BorderLayout.CENTER);
        vue.add(panelTable, BorderLayout.CENTER);

        JPanel panelBas = new JPanel(new BorderLayout(8, 8));
        panelBas.setOpaque(false);

        panelAlerteManquante = new JPanel(new BorderLayout(15, 0));
        panelAlerteManquante.setBackground(new Color(254, 243, 199));
        panelAlerteManquante.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        panelAlerteManquante.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        panelAlerteManquante.setVisible(false);

        JLabel lblAlerteText = new JLabel("<html>⚠️ <b>Votre dossier est incomplet !</b> Veuillez joindre le justificatif demandé pour relancer son instruction.</html>");
        lblAlerteText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAlerteText.setForeground(new Color(146, 64, 14));
        panelAlerteManquante.add(lblAlerteText, BorderLayout.CENTER);

        btnAjouterPieceManquante = new JButton("📤 Joindre le document manquant");
        btnAjouterPieceManquante.setBackground(new Color(245, 158, 11));
        btnAjouterPieceManquante.setForeground(Color.WHITE);
        btnAjouterPieceManquante.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAjouterPieceManquante.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnAjouterPieceManquante.addActionListener(e -> regulariserDossier());
        panelAlerteManquante.add(btnAjouterPieceManquante, BorderLayout.EAST);

        panelBas.add(panelAlerteManquante, BorderLayout.NORTH);

        JPanel panelInfoStandard = new JPanel(new BorderLayout());
        panelInfoStandard.setBackground(Color.WHITE);
        panelInfoStandard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 16, 10, 16)
        ));
        panelInfoStandard.putClientProperty(FlatClientProperties.STYLE, "arc: 12");

        lblNotification = new JLabel("Sélectionnez l'une de vos démarches pour consulter les remarques officielles.");
        lblNotification.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelInfoStandard.add(lblNotification, BorderLayout.CENTER);

        panelBas.add(panelInfoStandard, BorderLayout.SOUTH);
        vue.add(panelBas, BorderLayout.SOUTH);

        return vue;
    }

    private void regulariserDossier() {
        if (idDossierSelectionne == -1) return;

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Sélectionner la pièce justificative manquante");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fichier = fc.getSelectedFile();
            try {
                String chemin = FileManager.sauvegarderDocument(fichier, etudiant.getIdUtilisateur());
                DocumentJustificatif doc = new DocumentJustificatif(0, fichier.getName(), chemin);

                boolean ok = dossierDAO.completerDossier(idDossierSelectionne, doc);

                if (ok) {
                    NotificationDAO.envoyer(1, "L'étudiant " + etudiant.getNomComplet() + " a régularisé le dossier #" + idDossierSelectionne);

                    JOptionPane.showMessageDialog(this,
                            "Votre pièce justificative (" + fichier.getName() + ") a été transmise avec succès !\n" +
                            "Votre dossier repasse en cours d'instruction auprès de l'agent.",
                            "Dossier Régularisé", JOptionPane.INFORMATION_MESSAGE);

                    panelAlerteManquante.setVisible(false);
                    rafraichirSuivi();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel creerVueNouveauDossier() {
        JPanel vue = new JPanel(new BorderLayout(15, 15));
        vue.setBackground(Color.WHITE);
        vue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        vue.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JLabel lblTitre = new JLabel("Nouvelle demande de titre de séjour");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        vue.add(lblTitre, BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setOpaque(false);

        JLabel lblType = new JLabel("Sélectionnez la nature de la démarche :");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 13));
        centre.add(lblType);
        centre.add(Box.createVerticalStrut(5));

        comboType = new JComboBox<>(TypeDemande.values());
        comboType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comboType.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        centre.add(comboType);
        centre.add(Box.createVerticalStrut(15));

        JLabel lblDocs = new JLabel("Pièces justificatives à joindre (PDF / Image) :");
        lblDocs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        centre.add(lblDocs);
        centre.add(Box.createVerticalStrut(5));

        modelDocs = new DefaultListModel<>();
        JList<String> listDocs = new JList<>(modelDocs);
        listDocs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(listDocs);
        scroll.setPreferredSize(new Dimension(0, 160));
        centre.add(scroll);
        centre.add(Box.createVerticalStrut(10));

        JPanel panelBoutonsUpload = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBoutonsUpload.setOpaque(false);

        JButton btnParcourir = new JButton("📁 Choisir un fichier sur mon PC...");
        btnParcourir.setBackground(new Color(37, 99, 235));
        btnParcourir.setForeground(Color.WHITE);
        btnParcourir.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnParcourir.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                fichiersSelectionnes.add(f);
                modelDocs.addElement("📄 " + f.getName() + " (" + (f.length() / 1024) + " Ko)");
            }
        });

        JButton btnRetirer = new JButton("Retirer la sélection");
        btnRetirer.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnRetirer.addActionListener(e -> {
            int idx = listDocs.getSelectedIndex();
            if (idx != -1) {
                fichiersSelectionnes.remove(idx);
                modelDocs.remove(idx);
            }
        });

        panelBoutonsUpload.add(btnParcourir);
        panelBoutonsUpload.add(btnRetirer);
        centre.add(panelBoutonsUpload);

        vue.add(centre, BorderLayout.CENTER);

        JButton btnSoumettre = new JButton("🚀 Transmettre ma demande à la préfecture");
        btnSoumettre.setBackground(new Color(16, 185, 129));
        btnSoumettre.setForeground(Color.WHITE);
        btnSoumettre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSoumettre.setPreferredSize(new Dimension(0, 48));
        btnSoumettre.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnSoumettre.addActionListener(e -> soumettre());
        vue.add(btnSoumettre, BorderLayout.SOUTH);

        return vue;
    }

    private JPanel creerVueChecklist() {
        JPanel vue = new JPanel(new BorderLayout(15, 15));
        vue.setBackground(Color.WHITE);
        vue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));
        vue.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JLabel lbl = new JLabel("Checklist officielle des pièces à préparer");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        vue.add(lbl, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(6, 1, 8, 8));
        p.setOpaque(false);
        p.add(new JCheckBox("Passeport individuel en cours de validité"));
        p.add(new JCheckBox("Justificatif de domicile récent (< 6 mois)"));
        p.add(new JCheckBox("Certificat d'inscription pour l'année universitaire"));
        p.add(new JCheckBox("Relevés de notes universitaires de l'année précédente"));
        p.add(new JCheckBox("Attestation bancaire de ressources (minimum 615 €/mois)"));
        p.add(new JCheckBox("Titre de séjour ou visa actuel à renouveler"));
        vue.add(p, BorderLayout.CENTER);

        return vue;
    }

    private void genererAttestation() {
        if (tableSuivi.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez d'abord l'un de vos dossiers dans le tableau.");
            return;
        }

        int row = tableSuivi.getSelectedRow();
        String ref = (String) modelSuivi.getValueAt(row, 0);
        String dem = (String) modelSuivi.getValueAt(row, 1);
        String statut = (String) modelSuivi.getValueAt(row, 3);

        String contenu = "========================================================\n" +
                         "    RÉPUBLIQUE FRANÇAISE — ATTESTATION DE SÉJOUR         \n" +
                         "========================================================\n\n" +
                         "Demandeur : " + etudiant.getNomComplet() + "\n" +
                         "Identifiant AGDREF : " + etudiant.getNumeroEtranger() + "\n" +
                         "Établissement : " + etudiant.getEtablissement() + " (" + etudiant.getNiveauEtude() + ")\n" +
                         "Dossier Réf : " + ref + "\n" +
                         "Nature de la demande : " + dem + "\n" +
                         "Statut d'instruction : " + statut + "\n" +
                         "Date d'émission : " + LocalDate.now() + "\n\n" +
                         "Attestation officielle générée par SéjourFacile.\n" +
                         "========================================================\n";

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Attestation_Sejour_" + etudiant.getNom() + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write(contenu);
                JOptionPane.showMessageDialog(this, "Attestation officielle enregistrée avec succès !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        }
    }

    private void rafraichirSuivi() {
        modelSuivi.setRowCount(0);
        idDossierSelectionne = -1;
        panelAlerteManquante.setVisible(false);

        try (ResultSet rs = dossierDAO.getDossiersEtudiant(etudiant.getIdUtilisateur())) {
            int r = 0;
            while (rs.next()) {
                modelSuivi.addRow(new Object[]{
                        "#" + rs.getInt("id"),
                        rs.getString("type_demande"),
                        rs.getString("date_depot"),
                        rs.getString("statut")
                });
                tableSuivi.putClientProperty("notif_" + r, rs.getString("commentaire"));
                r++;
            }
        } catch (Exception ex) {
            System.err.println("Erreur suivi : " + ex.getMessage());
        }

        if (boutonCloche != null) boutonCloche.actualiserBadge();
    }

    private void soumettre() {
        if (fichiersSelectionnes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un fichier justificatif.");
            return;
        }

        TypeDemande typeChoisi = (TypeDemande) comboType.getSelectedItem();
        DossierDemande dossier = new DossierDemande(0, etudiant, typeChoisi);

        try {
            for (File f : fichiersSelectionnes) {
                String cheminRelatif = FileManager.sauvegarderDocument(f, etudiant.getIdUtilisateur());
                dossier.ajouterPiece(new DocumentJustificatif(0, f.getName(), cheminRelatif));
            }

            int idDossier = dossierDAO.creerDossier(dossier);

            NotificationDAO.envoyer(1, "Nouveau dossier #" + idDossier + " soumis par " + etudiant.getNomComplet());

            JOptionPane.showMessageDialog(this, "Demande transmise avec succès ! Référence : #" + idDossier);

            fichiersSelectionnes.clear();
            modelDocs.clear();
            rafraichirSuivi();
            cardLayout.show(panelContenu, "SUIVI");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}
