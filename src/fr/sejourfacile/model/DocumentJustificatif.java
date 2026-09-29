package fr.sejourfacile.model;

public class DocumentJustificatif {
    private int idDoc;
    private String intitule;
    private String cheminFichier;
    private boolean estValide;

    public DocumentJustificatif(int idDoc, String intitule, String cheminFichier) {
        this.idDoc = idDoc;
        this.intitule = intitule;
        this.cheminFichier = cheminFichier;
        this.estValide = false; // par défaut non validé
    }

    public void validerDocument() {
        this.estValide = true;
    }

    // Getters
    public int getIdDoc() { return idDoc; }
    public String getIntitule() { return intitule; }
    public String getCheminFichier() { return cheminFichier; }
    public boolean isEstValide() { return estValide; }
}