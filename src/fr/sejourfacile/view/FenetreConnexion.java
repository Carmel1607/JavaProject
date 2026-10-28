package fr.sejourfacile.view;

import com.formdev.flatlaf.FlatClientProperties;
import fr.sejourfacile.dao.UtilisateurDAO;
import fr.sejourfacile.model.AgentAdministratif;
import fr.sejourfacile.model.Etudiant;
import fr.sejourfacile.model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FenetreConnexion extends JFrame {
    private UtilisateurDAO utilisateurDAO;
    private boolean modeInscription = false;

    // Panneaux principaux (Split 50% / 50%)
    private JPanel panelGauche;
    private JPanel panelDroite;

    // Éléments du côté gauche (Bannière Indigo)
    private JLabel lblGaucheTitre;
    private JLabel lblGaucheSousTitre;
    private JButton btnGaucheAction;

    // Éléments du formulaire (Côté droit Blanc)
    private JLabel lblDroiteTitre;
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtEmail;
    private JPasswordField txtMdp;
    private JComboBox<String> comboRole;
    private JTextField txtExtra1; // Num étranger ou Matricule
    private JTextField txtExtra2; // Établissement ou Service
    private JButton btnSoumettre;

    // Couleurs du projet (Indigo profond & Bleu roi - Aucun vert)
    private static final Color COULEUR_INDIGO = new Color(26, 16, 83);   // #1A1053 (Sidebar du projet)
    private static final Color COULEUR_BLEU = new Color(37, 99, 235);    // #2563EB (Bleu officiel)
    private static final Color COULEUR_INPUT = new Color(241, 245, 249); // Fond des champs texte

    public FenetreConnexion() {
        utilisateurDAO = new UtilisateurDAO();

        setTitle("SéjourFacile — Authentification");
        setSize(850, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Conteneur en grille 1 ligne, 2 colonnes (50% / 50% exact)
        JPanel conteneurPrincipal = new JPanel(new GridLayout(1, 2));
        conteneurPrincipal.setBackground(Color.WHITE);

        // GAUCHE : Panneau Coloré Indigo
        panelGauche = new JPanel();
        panelGauche.setBackground(COULEUR_INDIGO);
        panelGauche.setLayout(new BoxLayout(panelGauche, BoxLayout.Y_AXIS));
        panelGauche.setBorder(new EmptyBorder(60, 45, 60, 45));

        lblGaucheTitre = new JLabel("Welcome Back!");
        lblGaucheTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGaucheTitre.setForeground(Color.WHITE);
        lblGaucheTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblGaucheSousTitre = new JLabel("<html><center>Pour rester connecté avec nous,<br>veuillez vous identifier avec vos accès personnels.</center></html>", SwingConstants.CENTER);
        lblGaucheSousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGaucheSousTitre.setForeground(new Color(224, 231, 255));
        lblGaucheSousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bouton Pilule Blanc Contour (Ghost Button de votre image)
        btnGaucheAction = new JButton("SIGN IN");
        btnGaucheAction.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGaucheAction.setForeground(Color.WHITE);
        btnGaucheAction.setBackground(COULEUR_INDIGO);
        btnGaucheAction.setContentAreaFilled(false);
        btnGaucheAction.setFocusPainted(false);
        btnGaucheAction.setPreferredSize(new Dimension(180, 42));
        btnGaucheAction.setMaximumSize(new Dimension(180, 42));
        btnGaucheAction.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGaucheAction.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnGaucheAction.putClientProperty(FlatClientProperties.STYLE, "arc: 999; borderWidth: 2; borderColor: #FFFFFF");
        btnGaucheAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGaucheAction.addActionListener(e -> basculerMode());

        panelGauche.add(Box.createVerticalGlue());
        panelGauche.add(lblGaucheTitre);
        panelGauche.add(Box.createVerticalStrut(15));
        panelGauche.add(lblGaucheSousTitre);
        panelGauche.add(Box.createVerticalStrut(30));
        panelGauche.add(btnGaucheAction);
        panelGauche.add(Box.createVerticalGlue());

        // DROITE : Panneau Blanc Formulaire
        panelDroite = new JPanel();
        panelDroite.setBackground(Color.WHITE);
        panelDroite.setLayout(new BoxLayout(panelDroite, BoxLayout.Y_AXIS));
        panelDroite.setBorder(new EmptyBorder(40, 50, 40, 50));

        lblDroiteTitre = new JLabel("Create Account");
        lblDroiteTitre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblDroiteTitre.setForeground(COULEUR_INDIGO);
        lblDroiteTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Champs de saisie avec le style de votre image (fond clair doux + coins arrondis)
        comboRole = new JComboBox<>(new String[]{"Étudiant International", "Agent Administratif"});
        styliserInput(comboRole, "Choisir le profil");
        comboRole.addActionListener(e -> adapterChampsRole());

        txtNom = new JTextField();
        styliserInput(txtNom, "👤  Nom & Prénom");

        txtEmail = new JTextField();
        styliserInput(txtEmail, "✉️  Email");

        txtMdp = new JPasswordField();
        styliserInput(txtMdp, "🔒  Mot de passe");

        txtExtra1 = new JTextField();
        styliserInput(txtExtra1, "🆔  N° AGDREF (Étranger)");

        txtExtra2 = new JTextField();
        styliserInput(txtExtra2, "🎓  Établissement / Université");

        // Bouton Pilule Plein (SIGN UP de votre image)
        btnSoumettre = new JButton("SIGN UP");
        btnSoumettre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSoumettre.setForeground(Color.WHITE);
        btnSoumettre.setBackground(COULEUR_BLEU);
        btnSoumettre.setPreferredSize(new Dimension(200, 44));
        btnSoumettre.setMaximumSize(new Dimension(200, 44));
        btnSoumettre.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSoumettre.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnSoumettre.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        btnSoumettre.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSoumettre.addActionListener(e -> executerAction());

        // Initialise en mode Inscription comme sur l'image
        modeInscription = true;
        reconstruireFormulaire();

        conteneurPrincipal.add(panelGauche);
        conteneurPrincipal.add(panelDroite);
        add(conteneurPrincipal);
    }

    private void styliserInput(JComponent comp, String placeholder) {
        comp.setPreferredSize(new Dimension(300, 42));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        comp.setBackground(COULEUR_INPUT);
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comp.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        comp.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0");
    }

    private void adapterChampsRole() {
        if (comboRole.getSelectedIndex() == 0) {
            txtExtra1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🆔  N° AGDREF (Étranger)");
            txtExtra2.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🎓  Établissement / Université");
        } else {
            txtExtra1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🆔  Matricule Agent");
            txtExtra2.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🏛️  Service / Préfecture");
        }
        panelDroite.revalidate();
        panelDroite.repaint();
    }

    private void basculerMode() {
        modeInscription = !modeInscription;
        reconstruireFormulaire();
    }

    private void reconstruireFormulaire() {
        panelDroite.removeAll();
        panelDroite.add(Box.createVerticalGlue());

        if (modeInscription) {
            // Mode INSCRIPTION (Exactement comme sur la photo)
            lblGaucheTitre.setText("Welcome Back!");
            lblGaucheSousTitre.setText("<html><center>Pour rester connecté avec nous,<br>veuillez vous identifier avec vos identifiants.</center></html>");
            btnGaucheAction.setText("SIGN IN");

            lblDroiteTitre.setText("Create Account");
            btnSoumettre.setText("SIGN UP");

            panelDroite.add(lblDroiteTitre);
            panelDroite.add(Box.createVerticalStrut(20));
            panelDroite.add(comboRole);
            panelDroite.add(Box.createVerticalStrut(10));
            panelDroite.add(txtNom);
            panelDroite.add(Box.createVerticalStrut(10));
            panelDroite.add(txtEmail);
            panelDroite.add(Box.createVerticalStrut(10));
            panelDroite.add(txtMdp);
            panelDroite.add(Box.createVerticalStrut(10));
            panelDroite.add(txtExtra1);
            panelDroite.add(Box.createVerticalStrut(10));
            panelDroite.add(txtExtra2);
            panelDroite.add(Box.createVerticalStrut(25));
            panelDroite.add(btnSoumettre);

        } else {
            // Mode CONNEXION (Inversion fidèle)
            lblGaucheTitre.setText("Hello, Friend!");
            lblGaucheSousTitre.setText("<html><center>Entrez vos coordonnées personnelles<br>et commencez votre démarche avec nous.</center></html>");
            btnGaucheAction.setText("SIGN UP");

            lblDroiteTitre.setText("Sign In to SéjourFacile");
            btnSoumettre.setText("SIGN IN");

            panelDroite.add(lblDroiteTitre);
            panelDroite.add(Box.createVerticalStrut(25));
            panelDroite.add(txtEmail);
            panelDroite.add(Box.createVerticalStrut(12));
            panelDroite.add(txtMdp);
            panelDroite.add(Box.createVerticalStrut(25));
            panelDroite.add(btnSoumettre);

            // Raccourcis démo pour test rapide
            JPanel pDemo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            pDemo.setOpaque(false);
            JButton b1 = new JButton("⚡ Étudiant Démo");
            b1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b1.addActionListener(ev -> { txtEmail.setText("amadou@etudiant.fr"); txtMdp.setText("etu123"); });
            JButton b2 = new JButton("⚡ Agent Démo");
            b2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b2.addActionListener(ev -> { txtEmail.setText("agent@gouv.fr"); txtMdp.setText("admin123"); });
            pDemo.add(b1);
            pDemo.add(b2);

            panelDroite.add(Box.createVerticalStrut(15));
            panelDroite.add(pDemo);
        }

        panelDroite.add(Box.createVerticalGlue());
        panelDroite.revalidate();
        panelDroite.repaint();
    }

    private void executerAction() {
        if (!modeInscription) {
            // Connexion
            String email = txtEmail.getText().trim();
            String mdp = new String(txtMdp.getPassword());
            Utilisateur user = utilisateurDAO.authentifier(email, mdp);
            if (user != null) {
                dispose();
                if (user instanceof Etudiant) {
                    new FenetreEtudiant((Etudiant) user).setVisible(true);
                } else if (user instanceof AgentAdministratif) {
                    new FenetreAgent((AgentAdministratif) user).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Identifiants invalides. Veuillez réessayer.", "Échec", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Inscription
            String nom = txtNom.getText().trim();
            String email = txtEmail.getText().trim();
            String mdp = new String(txtMdp.getPassword());

            if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires.", "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (utilisateurDAO.emailExiste(email)) {
                JOptionPane.showMessageDialog(this, "Cet email est déjà enregistré.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean succes;
            if (comboRole.getSelectedIndex() == 0) {
                succes = utilisateurDAO.inscrireEtudiant(nom, "", email, mdp, txtExtra1.getText(), txtExtra2.getText(), "Licence/Master");
            } else {
                succes = utilisateurDAO.inscrireAgent(nom, "", email, mdp, txtExtra1.getText(), txtExtra2.getText());
            }

            if (succes) {
                JOptionPane.showMessageDialog(this, "Compte créé avec succès ! Vous pouvez maintenant vous connecter.", "Félicitations", JOptionPane.INFORMATION_MESSAGE);
                basculerMode();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création du compte.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
