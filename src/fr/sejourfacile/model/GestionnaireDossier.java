package fr.sejourfacile.model;

import java.util.ArrayList;

public class GestionnaireDossier {
    private ArrayList<DossierDemande> listeDossiers;

    public GestionnaireDossier() {
        this.listeDossiers = new ArrayList<>();
    }

    // Ajouter un dossier dans la liste globale
    public void ajouterDossier(DossierDemande dossier) {
        this.listeDossiers.add(dossier);
    }

    // Filtrer par statut (ex: tous les dossiers EN_ATTENTE pour les agents)
    public ArrayList<DossierDemande> filtrerParStatut(StatutDossier statut) {
        ArrayList<DossierDemande> resultat = new ArrayList<>();
        for (DossierDemande d : listeDossiers) {
            if (d.getStatut() == statut) {
                resultat.add(d);
            }
        }
        return resultat;
    }

    // Rechercher un dossier par nom ou prénom du demandeur
    public ArrayList<DossierDemande> rechercherParDemandeur(String nom) {
        ArrayList<DossierDemande> resultat = new ArrayList<>();
        for (DossierDemande d : listeDossiers) {
            if (d.getDemandeur().getNom().equalsIgnoreCase(nom) || 
                d.getDemandeur().getPrenom().equalsIgnoreCase(nom)) {
                resultat.add(d);
            }
        }
        return resultat;
    }

    public ArrayList<DossierDemande> getListeDossiers() {
        return listeDossiers;
    }
}