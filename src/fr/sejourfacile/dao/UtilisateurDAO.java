package fr.sejourfacile.dao;

import fr.sejourfacile.model.*;
import java.sql.*;

public class UtilisateurDAO {

    public Utilisateur authentifier(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim());
            pstmt.setString(2, motDePasse);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("ETUDIANT".equalsIgnoreCase(role)) {
                    return new Etudiant(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("numero_etranger"),
                            rs.getString("etablissement"),
                            rs.getString("niveau_etude")
                    );
                } else if ("AGENT".equalsIgnoreCase(role)) {
                    return new AgentAdministratif(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("matricule"),
                            rs.getString("service")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur authentification : " + e.getMessage());
        }
        return null;
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM utilisateur WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.trim());
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean inscrireEtudiant(String nom, String prenom, String email, String mdp,
                                    String numeroEtranger, String etablissement, String niveau) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, numero_etranger, etablissement, niveau_etude) " +
                     "VALUES (?, ?, ?, ?, 'ETUDIANT', ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email.trim());
            pstmt.setString(4, mdp);
            pstmt.setString(5, numeroEtranger);
            pstmt.setString(6, etablissement);
            pstmt.setString(7, niveau);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur inscription étudiant : " + e.getMessage());
            return false;
        }
    }

    public boolean inscrireAgent(String nom, String prenom, String email, String mdp, String matricule, String service) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, matricule, service) " +
                     "VALUES (?, ?, ?, ?, 'AGENT', ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email.trim());
            pstmt.setString(4, mdp);
            pstmt.setString(5, matricule);
            pstmt.setString(6, service);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur inscription agent : " + e.getMessage());
            return false;
        }
    }
}
