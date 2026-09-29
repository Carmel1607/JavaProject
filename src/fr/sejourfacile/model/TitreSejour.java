package fr.sejourfacile.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TitreSejour {
    private int idTitre;
    private String numeroTitre;
    private LocalDate dateDelivrance;
    private LocalDate dateExpiration;
    private String typeTitre;

    public TitreSejour(int idTitre, String numeroTitre, LocalDate dateDelivrance, LocalDate dateExpiration, String typeTitre) {
        this.idTitre = idTitre;
        this.numeroTitre = numeroTitre;
        this.dateDelivrance = dateDelivrance;
        this.dateExpiration = dateExpiration;
        this.typeTitre = typeTitre;
    }

    public boolean estExpire() {
        return LocalDate.now().isAfter(dateExpiration);
    }

    public long getJoursRestants() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dateExpiration);
    }

    // Getters
    public int getIdTitre() { return idTitre; }
    public String getNumeroTitre() { return numeroTitre; }
    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public String getTypeTitre() { return typeTitre; }
}