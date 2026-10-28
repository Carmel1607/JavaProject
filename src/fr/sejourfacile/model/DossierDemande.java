package fr.sejourfacile.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class DossierDemande implements IValidable, IExportable {
    private int idDossier;
    private LocalDate dateDepot;
    private TypeDemande typeDemande;
    private StatutDossier statut;
    private String commentaireAgent;
    private Etudiant demandeur;
    private ArrayList<DocumentJustificatif> piecesJointes;

    public DossierDemande(int idDossier, Etudiant demandeur, TypeDemande typeDemande) {
        this.idDossier = idDossier;
        this.demandeur = demandeur;
        this.typeDemande = typeDemande;
        this.dateDepot = LocalDate.now();
        this.statut = StatutDossier.EN_ATTENTE;
        this.commentaireAgent = "En attente de prise en charge.";
        this.piecesJointes = new ArrayList<>();
    }

    // Gestion des pièces jointes (ArrayList)
    public void ajouterPiece(DocumentJustificatif doc) {
        this.piecesJointes.add(doc);
    }

    // Implémentation du contrat de l'interface IValidable
    @Override
    public void valider(String motif) {
        this.statut = StatutDossier.VALIDE;
        this.commentaireAgent = "Dossier validé : " + motif;
    }

    @Override
    public void rejeter(String motif) {
        this.statut = StatutDossier.REJETE;
        this.commentaireAgent = "Dossier rejeté : " + motif;
    }

    @Override
    public void demanderPiece(String details) {
        this.statut = StatutDossier.PIECE_MANQUANTE;
        this.commentaireAgent = "Pièce(s) manquante(s) : " + details;
    }

    // Implémentation du contrat de l'interface IExportable
    @Override
    public String genererRecapitulatif() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("       RÉCAPITULATIF DU DOSSIER DE SÉJOUR N° ").append(idDossier).append("\n");
        sb.append("====================================================\n");
        sb.append("Demandeur          : ").append(demandeur.getNomComplet()).append("\n");
        sb.append("N° Étranger (AGDREF): ").append(demandeur.getNumeroEtranger()).append("\n");
        sb.append("Établissement      : ").append(demandeur.getEtablissement()).append(" (").append(demandeur.getNiveauEtude()).append(")\n");
        sb.append("Type de demande    : ").append(typeDemande).append("\n");
        sb.append("Date de dépôt      : ").append(dateDepot).append("\n");
        sb.append("Statut actuel      : [").append(statut).append("]\n");
        sb.append("Décision / Motif   : ").append(commentaireAgent).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append("Pièces justificatives fournies (").append(piecesJointes.size()).append(") :\n");
        if (piecesJointes.isEmpty()) {
            sb.append("  (Aucune pièce jointe transmise)\n");
        } else {
            for (DocumentJustificatif doc : piecesJointes) {
                sb.append("  • ").append(doc.getIntitule())
                  .append(" [").append(doc.isEstValide() ? "CONFORME" : "À VÉRIFIER").append("]\n");
            }
        }
        sb.append("====================================================\n");
        return sb.toString();
    }

    // Getters & Setters
    public int getIdDossier() { return idDossier; }
    public LocalDate getDateDepot() { return dateDepot; }
    public TypeDemande getTypeDemande() { return typeDemande; }
    public StatutDossier getStatut() { return statut; }
    public String getCommentaireAgent() { return commentaireAgent; }
    public Etudiant getDemandeur() { return demandeur; }
    public ArrayList<DocumentJustificatif> getPiecesJointes() { return piecesJointes; }
}