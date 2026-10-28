package fr.sejourfacile.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManager {
    private static final String DOSSIER_UPLOAD = "uploads";

    static {
        File dossier = new File(DOSSIER_UPLOAD);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
    }

    public static String sauvegarderDocument(File fichierSource, int idDossier) throws IOException {
        String extension = "";
        String nomFichier = fichierSource.getName();
        int indexPoint = nomFichier.lastIndexOf('.');
        if (indexPoint > 0) {
            extension = nomFichier.substring(indexPoint);
        }

        String nomDestination = "doc_dossier" + idDossier + "_" + System.currentTimeMillis() + extension;
        File fichierDestination = new File(DOSSIER_UPLOAD + File.separator + nomDestination);

        Files.copy(fichierSource.toPath(), fichierDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return fichierDestination.getPath();
    }

    public static void ouvrirFichier(String cheminFichier) {
        try {
            File fichier = new File(cheminFichier);
            if (fichier.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fichier);
            } else {
                System.err.println("Fichier introuvable : " + cheminFichier);
            }
        } catch (Exception e) {
            System.err.println("Erreur ouverture fichier : " + e.getMessage());
        }
    }
}
