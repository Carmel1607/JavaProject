package fr.sejourfacile.dao;

import fr.sejourfacile.model.*;
import java.sql.*;
import java.util.ArrayList;

public class DossierDAO {

    public int creerDossier(DossierDemande dossier) {
        String sql = "INSERT INTO dossier (id_etudiant, type_demande, date_depot, statut, commentaire) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, dossier.getDemandeur().getIdUtilisateur());
            pstmt.setString(2, dossier.getTypeDemande().name());
            pstmt.setString(3, dossier.getDateDepot().toString());
            pstmt.setString(4, dossier.getStatut().name());
            pstmt.setString(5, dossier.getCommentaireAgent());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int idGenere = rs.getInt(1);
                for (DocumentJustificatif doc : dossier.getPiecesJointes()) {
                    ajouterDocument(idGenere, doc);
                }
                return idGenere;
            }
        } catch (SQLException e) {
            System.err.println("Erreur création dossier : " + e.getMessage());
        }
        return -1;
    }

    public void ajouterDocument(int idDossier, DocumentJustificatif doc) {
        String sql = "INSERT INTO document (id_dossier, intitule, chemin_fichier, est_valide) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDossier);
            pstmt.setString(2, doc.getIntitule());
            pstmt.setString(3, doc.getCheminFichier());
            pstmt.setInt(4, doc.isEstValide() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur ajout document : " + e.getMessage());
        }
    }

    public void mettreAJourStatut(int idDossier, StatutDossier statut, String commentaire) {
        String sql = "UPDATE dossier SET statut = ?, commentaire = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statut.name());
            pstmt.setString(2, commentaire);
            pstmt.setInt(3, idDossier);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour statut : " + e.getMessage());
        }
    }

    public boolean completerDossier(int idDossier, DocumentJustificatif doc) {
        try {
            ajouterDocument(idDossier, doc);
            String commentaire = "Pièce complémentaire fournie : " + doc.getIntitule();
            mettreAJourStatut(idDossier, StatutDossier.EN_ATTENTE, commentaire);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur régularisation dossier : " + e.getMessage());
            return false;
        }
    }

    public ArrayList<DocumentJustificatif> getDocumentsParDossier(int idDossier) {
        ArrayList<DocumentJustificatif> docs = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE id_dossier = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDossier);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DocumentJustificatif doc = new DocumentJustificatif(
                        rs.getInt("id"),
                        rs.getString("intitule"),
                        rs.getString("chemin_fichier")
                );
                if (rs.getInt("est_valide") == 1) doc.validerDocument();
                docs.add(doc);
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement documents : " + e.getMessage());
        }
        return docs;
    }

    public ResultSet getDossiersEtudiant(int idEtudiant) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT id, type_demande, date_depot, statut, commentaire FROM dossier WHERE id_etudiant = ? ORDER BY id DESC";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idEtudiant);
        return pstmt.executeQuery();
    }
}
