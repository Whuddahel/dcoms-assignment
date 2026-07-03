package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Receptionist;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistRepository {

  public static boolean addReceptionist(Receptionist receptionist) {
    String insertUserSql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";
    String insertRecSql = "INSERT INTO Receptionist (userId) VALUES (?)";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId;
        try (PreparedStatement psUser =
            conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
          psUser.setString(1, receptionist.getFirstName());
          psUser.setString(2, receptionist.getLastName());
          psUser.setString(3, receptionist.getUserRole());
          psUser.setString(4, receptionist.getIcPassportNo());
          psUser.setString(5, receptionist.getEmail());
          psUser.setString(6, receptionist.getPasswordHash());
          psUser.executeUpdate();

          try (ResultSet rs = psUser.getGeneratedKeys()) {
            if (rs.next()) {
              userId = rs.getInt(1);
            } else {
              throw new SQLException("Failed to retrieve generated userId.");
            }
          }
        }

        try (PreparedStatement psRec = conn.prepareStatement(insertRecSql)) {
          psRec.setInt(1, userId);
          psRec.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Receptionist getReceptionistById(int receptionistId) {
    String sql =
        "SELECT r.receptionistId, r.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM Receptionist r "
            + "JOIN Users u ON r.userId = u.userId "
            + "WHERE r.receptionistId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, receptionistId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Receptionist(
              rs.getInt("receptionistId"),
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              rs.getString("userRole"),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              null);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean updateReceptionist(Receptionist receptionist) {
    String sql =
        "UPDATE Users SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? "
            + "WHERE userId = (SELECT userId FROM Receptionist WHERE receptionistId = ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, receptionist.getFirstName());
      ps.setString(2, receptionist.getLastName());
      ps.setString(3, receptionist.getUserRole());
      ps.setString(4, receptionist.getIcPassportNo());
      ps.setString(5, receptionist.getEmail());
      ps.setString(6, receptionist.getPasswordHash());
      ps.setInt(7, receptionist.getReceptionistId());
      int rows = ps.executeUpdate();
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean deleteReceptionist(int receptionistId) {
    String selectSql = "SELECT userId FROM Receptionist WHERE receptionistId = ?";
    String deleteRecSql = "DELETE FROM Receptionist WHERE receptionistId = ?";
    String deleteUserSql = "DELETE FROM Users WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId = -1;
        try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
          psSelect.setInt(1, receptionistId);
          try (ResultSet rs = psSelect.executeQuery()) {
            if (rs.next()) {
              userId = rs.getInt("userId");
            }
          }
        }

        if (userId == -1) {
          throw new SQLException("Receptionist not found with receptionistId: " + receptionistId);
        }

        try (PreparedStatement psRec = conn.prepareStatement(deleteRecSql)) {
          psRec.setInt(1, receptionistId);
          psRec.executeUpdate();
        }

        try (PreparedStatement psUser = conn.prepareStatement(deleteUserSql)) {
          psUser.setInt(1, userId);
          psUser.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<Receptionist> listAllReceptionists() throws SQLException {
    String sql =
        "SELECT r.receptionistId, r.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM Receptionist r "
            + "JOIN Users u ON r.userId = u.userId";

    List<Receptionist> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== RECEPTIONISTS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        Receptionist receptionist =
            new Receptionist(
                rs.getInt("receptionistId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null);
        list.add(receptionist);
        System.out.println(
            receptionist.getReceptionistId()
                + " | "
                + receptionist.getFullName()
                + " | "
                + receptionist.getEmail());
      }
      if (empty) {
        System.out.println("(no receptionists found)");
      }
      System.out.println("=================================");
    }
    return list;
  }

  public static List<Receptionist> getAllReceptionists() throws SQLException {
    String sql =
        "SELECT r.receptionistId, r.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM Receptionist r "
            + "JOIN Users u ON r.userId = u.userId";

    List<Receptionist> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new Receptionist(
                rs.getInt("receptionistId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null));
      }
    }
    return list;
  }
}
