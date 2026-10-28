package fr.sejourfacile.model;

public class Etudiant extends Utilisateur {
    private String numeroEtranger;
    private String etablissement;
    private String niveauEtude;
    private TitreSejour titreSejour; // Association 0..1 du diagramme

    public Etudiant(int idUtilisateur, String nom, String prenom, String email, String motDePasse,
                    String numeroEtranger, String etablissement, String niveauEtude) {
        super(idUtilisateur, nom, prenom, email, motDePasse);
        this.numeroEtranger = numeroEtranger;
        this.etablissement = etablissement;
        this.niveauEtude = niveauEtude;
    }

    @Override
    public String afficherRole() {
        return "Étudiant International (" + niveauEtude + " à " + etablissement + ")";
    }

    // Getters & Setters
    public String getNumeroEtranger() { return numeroEtranger; }
    public String getEtablissement() { return etablissement; }
    public String getNiveauEtude() { return niveauEtude; }
    public TitreSejour getTitreSejour() { return titreSejour; }
    public void setTitreSejour(TitreSejour titreSejour) { this.titreSejour = titreSejour; }
}