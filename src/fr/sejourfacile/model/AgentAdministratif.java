package fr.sejourfacile.model;

public class AgentAdministratif extends Utilisateur {
    private String matricule;
    private String service;

    public AgentAdministratif(int idUtilisateur, String nom, String prenom, String email, String motDePasse,
                             String matricule, String service) {
        super(idUtilisateur, nom, prenom, email, motDePasse);
        this.matricule = matricule;
        this.service = service;
    }

    @Override
    public String afficherRole() {
        return "Agent Administratif [Service: " + service + " | Matricule: " + matricule + "]";
    }

    public String getMatricule() { return matricule; }
    public String getService() { return service; }
}
