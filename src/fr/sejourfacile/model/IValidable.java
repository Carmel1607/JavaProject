package fr.sejourfacile.model;

public interface IValidable {
    void valider(String motif);
    void rejeter(String motif);
    void demanderPiece(String details);
}