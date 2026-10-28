package fr.sejourfacile.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:sejourfacile.db";
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                initialiserTables(connection);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite introuvable !");
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        return connection;
    }

    private static void initialiserTables(Connection conn) {
        if (conn == null) return;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS utilisateur (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nom TEXT NOT NULL, prenom TEXT NOT NULL, email TEXT UNIQUE NOT NULL, mot_de_passe TEXT NOT NULL, " +
                    "role TEXT NOT NULL, numero_etranger TEXT, etablissement TEXT, niveau_etude TEXT, matricule TEXT, service TEXT);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS dossier (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_etudiant INTEGER NOT NULL, type_demande TEXT NOT NULL, date_depot TEXT NOT NULL, statut TEXT NOT NULL, commentaire TEXT);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS document (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_dossier INTEGER NOT NULL, intitule TEXT NOT NULL, chemin_fichier TEXT NOT NULL, est_valide INTEGER DEFAULT 0);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS notification (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_utilisateur INTEGER NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "date_creation TEXT NOT NULL, " +
                    "est_lu INTEGER DEFAULT 0);");

            // Comptes démo par défaut
            stmt.executeUpdate("INSERT OR IGNORE INTO utilisateur (id, nom, prenom, email, mot_de_passe, role, matricule, service) " +
                    "VALUES (1, 'Dupont', 'Claire', 'agent@gouv.fr', 'admin123', 'AGENT', 'AGT-001', 'Bureau des Étrangers');");

            stmt.executeUpdate("INSERT OR IGNORE INTO utilisateur (id, nom, prenom, email, mot_de_passe, role, numero_etranger, etablissement, niveau_etude) " +
                    "VALUES (2, 'Diop', 'Amadou', 'amadou@etudiant.fr', 'etu123', 'ETUDIANT', '9901234567', 'Université de Tours', 'Master Informatique');");

        } catch (SQLException e) {
            System.err.println("Erreur initialisation tables : " + e.getMessage());
        }
    }
}
