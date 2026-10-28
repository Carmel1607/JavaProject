package fr.sejourfacile.model;

public abstract class Utilisateur {
    protected int idUtilisateur;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String motDePasse;

    public Utilisateur(int idUtilisateur, String nom, String prenom, String email, String motDePasse) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public String getNomComplet() {
        return prenom + " " + nom.toUpperCase();
    }

    // Méthode abstraite que chaque sous-classe DOIT obligatoirement définir
    public abstract String afficherRole();

    // Getters et Setters
    public int getIdUtilisateur() { return idUtilisateur; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
}