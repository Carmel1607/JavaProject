package fr.sejourfacile.view;

import com.formdev.flatlaf.FlatClientProperties;
import fr.sejourfacile.dao.DatabaseConnection;
import fr.sejourfacile.dao.DossierDAO;
import fr.sejourfacile.dao.NotificationDAO;
import fr.sejourfacile.model.*;
import fr.sejourfacile.util.BoutonCloche;
import fr.sejourfacile.util.DonutChartPanel;
import fr.sejourfacile.util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class FenetreAgent extends JFrame {
    private AgentAdministratif agent;
    private DossierDAO dossierDAO;

    // Navigation
    private CardLayout cardLayout;
    private JPanel panelContenuCentral;

    // Cloche de notifications
    private BoutonCloche boutonCloche;

    // Éléments du Dashboard
    private DonutChartPanel donutChart;
    private JLabel lblTotal, lblAttente, lblValides, lblManquants;
    private JTable tableDossiers;
    private DefaultTableModel modelTable;
    private DefaultListModel<String> modelDocs;
    private ArrayList<DocumentJustificatif> documentsDossierCourant;
    private int idDossierSelectionne = -1;
    private int idEtudiantSelectionne = -1;

    // Recherche et filtre
    private JTextField txtRecherche;
    private JComboBox<String> comboFiltreStatut;

    // Vue Base de Données
    private JTable tableSQL;
    private DefaultTableModel modelTableSQL;
    private JComboBox<String> comboChoixTable;

    public FenetreAgent(AgentAdministratif agent) {
        this.agent = agent;
        this.dossierDAO = new DossierDAO();
        this.documentsDossierCourant = new ArrayList<>();

        setTitle("SéjourFacile Pro — Préfecture & Étudiants Internationaux");
        setSize(1200, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelGlobal = new JPanel(new BorderLayout());
        panelGlobal.setBackground(new Color(241, 245, 249));

        // 1. Sidebar gauche Indigo (#1A1053)
        panelGlobal.add(creerSidebar(), BorderLayout.WEST);

        // 2. Zone centrale
        cardLayout = new CardLayout();
        panelContenuCentral = new JPanel(cardLayout);
        panelContenuCentral.setOpaque(false);
        panelContenuCentral.setBorder(new EmptyBorder(15, 20, 15, 20));

        panelContenuCentral.add(creerVueDashboard(), "DASHBOARD");
        panelContenuCentral.add(creerVueBD(), "DATABASE");

        panelGlobal.add(panelContenuCentral, BorderLayout.CENTER);
        add(panelGlobal);

        chargerDossiersDepuisBD();
    }

    private JPanel creerSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(26, 16, 83));

        // Profil
        JPanel panelProfil = new JPanel();
        panelProfil.setLayout(new BoxLayout(panelProfil, BoxLayout.Y_AXIS));
        panelProfil.setOpaque(false);
        panelProfil.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel avatar = new JLabel("👮", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nom = new JLabel(agent.getNomComplet());
        nom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nom.setForeground(Color.WHITE);
        nom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mail = new JLabel(agent.getEmail());
        mail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        mail.setForeground(new Color(165, 180, 252));
        mail.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelProfil.add(avatar);
        panelProfil.add(Box.createVerticalStrut(8));
        panelProfil.add(nom);
        panelProfil.add(mail);
        sidebar.add(panelProfil, BorderLayout.NORTH);

        // Menu
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setOpaque(false);
        panelMenu.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel l1 = new JLabel("MAIN");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l1.setForeground(new Color(129, 140, 248));
        panelMenu.add(l1);
        panelMenu.add(Box.createVerticalStrut(5));

        panelMenu.add(creerBoutonMenu("📊 Dashboard & Graphiques", () -> cardLayout.show(panelContenuCentral, "DASHBOARD")));
        panelMenu.add(Box.createVerticalStrut(15));

        JLabel l2 = new JLabel("ADMINISTRATION");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l2.setForeground(new Color(129, 140, 248));
        panelMenu.add(l2);
        panelMenu.add(Box.createVerticalStrut(5));

        panelMenu.add(creerBoutonMenu("🗄️ Base de Données SQL", () -> {
            cardLayout.show(panelContenuCentral, "DATABASE");
            chargerTableSQL("dossier");
        }));
        sidebar.add(panelMenu, BorderLayout.CENTER);

        // Déconnexion
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
        sidebar.add(panelBas, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton creerBoutonMenu(String texte, Runnable action) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btn.setBackground(new Color(49, 46, 129));
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel creerVueDashboard() {
        JPanel vue = new JPanel(new BorderLayout(15, 15));
        vue.setOpaque(false);

        // Top Bar avec Champ de Recherche et Cloche de Notification
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 16, 10, 16)
        ));
        topBar.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JLabel lblVueTitre = new JLabel("Aperçu Général des Dossiers");
        lblVueTitre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(lblVueTitre, BorderLayout.WEST);

        JPanel panelFiltres = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelFiltres.setOpaque(false);

        txtRecherche = new JTextField(15);
        txtRecherche.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Rechercher...");
        txtRecherche.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        panelFiltres.add(txtRecherche);

        comboFiltreStatut = new JComboBox<>(new String[]{"TOUS", "EN_ATTENTE", "VALIDE", "REJETE", "PIECE_MANQUANTE"});
        comboFiltreStatut.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        panelFiltres.add(comboFiltreStatut);

        JButton btnFiltrer = new JButton("Filtrer");
        btnFiltrer.setBackground(new Color(37, 99, 235));
        btnFiltrer.setForeground(Color.WHITE);
        btnFiltrer.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnFiltrer.addActionListener(e -> chargerDossiersDepuisBD());
        panelFiltres.add(btnFiltrer);

        // CLOCHE NOTIFICATION INTÉGRÉE
        boutonCloche = new BoutonCloche(agent.getIdUtilisateur());
        panelFiltres.add(boutonCloche);

        topBar.add(panelFiltres, BorderLayout.EAST);
        vue.add(topBar, BorderLayout.NORTH);

        // Graphique Donut + 4 Cartes Métriques
        JPanel panelStats = new JPanel(new GridLayout(1, 2, 15, 0));
        panelStats.setOpaque(false);

        donutChart = new DonutChartPanel("RÉPARTITION PAR STATUT (TEMPS RÉEL)");
        donutChart.setBackground(Color.WHITE);
        donutChart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        donutChart.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        panelStats.add(donutChart);

        JPanel gridKpi = new JPanel(new GridLayout(2, 2, 10, 10));
        gridKpi.setOpaque(false);
        lblTotal = new JLabel("0");
        lblAttente = new JLabel("0");
        lblValides = new JLabel("0");
        lblManquants = new JLabel("0");

        gridKpi.add(creerCarteApple("TOTAL DEMANDES", lblTotal, new Color(37, 99, 235)));
        gridKpi.add(creerCarteApple("EN COURS D'EXAMEN", lblAttente, new Color(245, 158, 11)));
        gridKpi.add(creerCarteApple("TITRES VALIDÉS", lblValides, new Color(16, 185, 129)));
        gridKpi.add(creerCarteApple("PIÈCES MANQUANTES", lblManquants, new Color(239, 68, 68)));
        panelStats.add(gridKpi);

        vue.add(panelStats, BorderLayout.CENTER);

        // Tableau et volet d'instruction
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.65);
        split.setDividerSize(8);
        split.setOpaque(false);

        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBackground(Color.WHITE);
        panelTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        panelTable.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        String[] cols = {"Réf #", "Demandeur", "N° AGDREF", "Type", "Dépôt", "Statut"};
        modelTable = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDossiers = new JTable(modelTable);
        tableDossiers.setRowHeight(34);
        tableDossiers.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tableDossiers.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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
                    l.setText("● DOC MANQUANT");
                } else {
                    l.setForeground(new Color(59, 130, 246));
                    l.setText("● EN ATTENTE");
                }
                return l;
            }
        });

        tableDossiers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableDossiers.getSelectedRow() != -1) {
                int r = tableDossiers.getSelectedRow();
                idDossierSelectionne = (int) modelTable.getValueAt(r, 0);
                idEtudiantSelectionne = (int) tableDossiers.getClientProperty("etudiant_id_" + r);
                chargerPieces(idDossierSelectionne);
            }
        });

        panelTable.add(new JScrollPane(tableDossiers), BorderLayout.CENTER);
        split.setLeftComponent(panelTable);

        // Volet d'instruction
        JPanel panelInst = new JPanel(new BorderLayout(10, 10));
        panelInst.setBackground(Color.WHITE);
        panelInst.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        panelInst.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JLabel lblPieceTitre = new JLabel("Pièces justificatives");
        lblPieceTitre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInst.add(lblPieceTitre, BorderLayout.NORTH);

        modelDocs = new DefaultListModel<>();
        JList<String> listDocs = new JList<>(modelDocs);
        panelInst.add(new JScrollPane(listDocs), BorderLayout.CENTER);

        JPanel panelBoutonsInst = new JPanel(new GridLayout(4, 1, 6, 6));
        panelBoutonsInst.setOpaque(false);

        JButton btnOuvrir = new JButton("👁️ Ouvrir le document sélectionné");
        btnOuvrir.setBackground(new Color(37, 99, 235));
        btnOuvrir.setForeground(Color.WHITE);
        btnOuvrir.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnOuvrir.addActionListener(e -> {
            int idx = listDocs.getSelectedIndex();
            if (idx != -1 && idx < documentsDossierCourant.size()) {
                FileManager.ouvrirFichier(documentsDossierCourant.get(idx).getCheminFichier());
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une pièce dans la liste ci-dessus.");
            }
        });

        JButton btnVal = new JButton("✔ Valider la demande");
        btnVal.setBackground(new Color(16, 185, 129));
        btnVal.setForeground(Color.WHITE);
        btnVal.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnVal.addActionListener(e -> decider(StatutDossier.VALIDE));

        JButton btnRej = new JButton("✖ Rejeter la demande");
        btnRej.setBackground(new Color(239, 68, 68));
        btnRej.setForeground(Color.WHITE);
        btnRej.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnRej.addActionListener(e -> decider(StatutDossier.REJETE));

        JButton btnManq = new JButton("⚠ Demander pièce complémentaire");
        btnManq.setBackground(new Color(245, 158, 11));
        btnManq.setForeground(Color.WHITE);
        btnManq.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnManq.addActionListener(e -> decider(StatutDossier.PIECE_MANQUANTE));

        panelBoutonsInst.add(btnOuvrir);
        panelBoutonsInst.add(btnVal);
        panelBoutonsInst.add(btnRej);
        panelBoutonsInst.add(btnManq);

        panelInst.add(panelBoutonsInst, BorderLayout.SOUTH);
        split.setRightComponent(panelInst);

        JPanel panelBasSplit = new JPanel(new BorderLayout());
        panelBasSplit.setOpaque(false);
        panelBasSplit.setPreferredSize(new Dimension(0, 310));
        panelBasSplit.add(split, BorderLayout.CENTER);

        vue.add(panelBasSplit, BorderLayout.SOUTH);
        return vue;
    }

    private JPanel creerCarteApple(String titre, JLabel labelVal, Color accent) {
        JPanel carte = new JPanel(new BorderLayout(5, 5));
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        carte.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JLabel lbl = new JLabel(titre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 116, 139));

        labelVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        labelVal.setForeground(accent);

        carte.add(lbl, BorderLayout.NORTH);
        carte.add(labelVal, BorderLayout.CENTER);
        return carte;
    }

    private JPanel creerVueBD() {
        JPanel vue = new JPanel(new BorderLayout(15, 15));
        vue.setBackground(Color.WHITE);
        vue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        vue.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        top.setOpaque(false);
        top.add(new JLabel("Inspecter la table SQL :"));
        comboChoixTable = new JComboBox<>(new String[]{"dossier", "utilisateur", "document", "notification"});
        comboChoixTable.addActionListener(e -> chargerTableSQL((String) comboChoixTable.getSelectedItem()));
        top.add(comboChoixTable);

        JButton btnRel = new JButton("🔄 Rafraîchir les données SQL");
        btnRel.setBackground(new Color(37, 99, 235));
        btnRel.setForeground(Color.WHITE);
        btnRel.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnRel.addActionListener(e -> chargerTableSQL((String) comboChoixTable.getSelectedItem()));
        top.add(btnRel);

        vue.add(top, BorderLayout.NORTH);

        modelTableSQL = new DefaultTableModel();
        tableSQL = new JTable(modelTableSQL);
        tableSQL.setRowHeight(30);
        vue.add(new JScrollPane(tableSQL), BorderLayout.CENTER);

        return vue;
    }

    private void chargerTableSQL(String table) {
        modelTableSQL.setRowCount(0);
        modelTableSQL.setColumnCount(0);
        String sql = "SELECT * FROM " + table;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int n = meta.getColumnCount();
            for (int i = 1; i <= n; i++) modelTableSQL.addColumn(meta.getColumnName(i));

            while (rs.next()) {
                Object[] row = new Object[n];
                for (int i = 1; i <= n; i++) row[i - 1] = rs.getObject(i);
                modelTableSQL.addRow(row);
            }
        } catch (SQLException ex) {
            System.err.println("Erreur SQL : " + ex.getMessage());
        }
    }

    private void chargerDossiersDepuisBD() {
        modelTable.setRowCount(0);
        modelDocs.clear();
        documentsDossierCourant.clear();
        idDossierSelectionne = -1;

        int total = 0, attente = 0, valides = 0, manquants = 0;
        String filtre = (String) comboFiltreStatut.getSelectedItem();
        String rech = txtRecherche.getText().trim();

        StringBuilder sql = new StringBuilder(
                "SELECT d.id, d.id_etudiant, u.prenom || ' ' || u.nom AS demandeur, u.numero_etranger, d.type_demande, d.date_depot, d.statut " +
                "FROM dossier d JOIN utilisateur u ON d.id_etudiant = u.id WHERE 1=1 "
        );

        if (!"TOUS".equals(filtre)) sql.append(" AND d.statut = '").append(filtre).append("'");
        if (!rech.isEmpty()) sql.append(" AND (u.nom LIKE '%").append(rech).append("%' OR u.numero_etranger LIKE '%").append(rech).append("%')");
        sql.append(" ORDER BY d.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {

            int indexLigne = 0;
            while (rs.next()) {
                total++;
                String st = rs.getString("statut");
                if ("EN_ATTENTE".equals(st)) attente++;
                else if ("VALIDE".equals(st)) valides++;
                else if ("PIECE_MANQUANTE".equals(st)) manquants++;

                modelTable.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("demandeur"),
                        rs.getString("numero_etranger"),
                        rs.getString("type_demande"),
                        rs.getString("date_depot"),
                        st
                });
                tableDossiers.putClientProperty("etudiant_id_" + indexLigne, rs.getInt("id_etudiant"));
                indexLigne++;
            }
        } catch (Exception ex) {
            System.err.println("Erreur chargement : " + ex.getMessage());
        }

        lblTotal.setText(String.valueOf(total));
        lblAttente.setText(String.valueOf(attente));
        lblValides.setText(String.valueOf(valides));
        lblManquants.setText(String.valueOf(manquants));

        donutChart.setDonnees(valides, attente, manquants, total - (valides + attente + manquants));
        if (boutonCloche != null) boutonCloche.actualiserBadge();
    }

    private void chargerPieces(int idDossier) {
        modelDocs.clear();
        documentsDossierCourant = dossierDAO.getDocumentsParDossier(idDossier);
        if (documentsDossierCourant.isEmpty()) {
            modelDocs.addElement("(Aucun document téléversé)");
        } else {
            for (DocumentJustificatif doc : documentsDossierCourant) {
                modelDocs.addElement("📄 " + doc.getIntitule());
            }
        }
    }

    private void decider(StatutDossier statut) {
        if (idDossierSelectionne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez d'abord un dossier dans le tableau.");
            return;
        }

        String motif = JOptionPane.showInputDialog(this, "Indiquez la notification officielle transmise au demandeur :");
        if (motif != null && !motif.trim().isEmpty()) {
            dossierDAO.mettreAJourStatut(idDossierSelectionne, statut, motif);

            if (idEtudiantSelectionne != -1) {
                NotificationDAO.envoyer(idEtudiantSelectionne, "Votre dossier #" + idDossierSelectionne + " est désormais : " + statut + " (" + motif + ")");
            }

            JOptionPane.showMessageDialog(this, "Dossier #" + idDossierSelectionne + " mis à jour !");
            chargerDossiersDepuisBD();
        }
    }
}