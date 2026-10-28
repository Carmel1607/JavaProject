package fr.sejourfacile.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NotificationDAO {

    public static void envoyer(int idUtilisateur, String message) {
        String sql = "INSERT INTO notification (id_utilisateur, message, date_creation, est_lu) VALUES (?, ?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            pstmt.setString(2, message);
            pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur envoi notif : " + e.getMessage());
        }
    }

    public static ArrayList<String> getNotifications(int idUtilisateur) {
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT message, date_creation, est_lu FROM notification WHERE id_utilisateur = ? ORDER BY id DESC LIMIT 15";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String prefix = rs.getInt("est_lu") == 0 ? "🔴 [Nouveau] " : "⚪ ";
                list.add(prefix + rs.getString("date_creation") + " — " + rs.getString("message"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture notifs : " + e.getMessage());
        }
        return list;
    }

    public static int getNbNonLues(int idUtilisateur) {
        String sql = "SELECT COUNT(*) FROM notification WHERE id_utilisateur = ? AND est_lu = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur comptage notifs : " + e.getMessage());
        }
        return 0;
    }

    public static void marquerToutCommeLu(int idUtilisateur) {
        String sql = "UPDATE notification SET est_lu = 1 WHERE id_utilisateur = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur màj notifs : " + e.getMessage());
        }
    }
}
