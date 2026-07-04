package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.ClinicAdministrator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClinicAdministratorRepository {

  public static boolean addClinicAdministrator(ClinicAdministrator admin) throws SQLException {
    String insertUserSql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";
    String insertAdminSql = "INSERT INTO ClinicAdministrator (userId) VALUES (?)";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId;
        try (PreparedStatement psUser =
            conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
          psUser.setString(1, admin.getFirstName());
          psUser.setString(2, admin.getLastName());
          psUser.setString(3, admin.getUserRole());
          psUser.setString(4, admin.getIcPassportNo());
          psUser.setString(5, admin.getEmail());
          psUser.setString(6, admin.getPasswordHash());
          psUser.executeUpdate();

          try (ResultSet rs = psUser.getGeneratedKeys()) {
            if (rs.next()) {
              userId = rs.getInt(1);
            } else {
              throw new SQLException("Failed to retrieve generated userId.");
            }
          }
        }

        try (PreparedStatement psAdmin = conn.prepareStatement(insertAdminSql)) {
          psAdmin.setInt(1, userId);
          psAdmin.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  public static ClinicAdministrator getClinicAdministratorById(int adminId) throws SQLException {
    String sql =
        "SELECT a.adminId, a.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM ClinicAdministrator a "
            + "JOIN Users u ON a.userId = u.userId "
            + "WHERE a.adminId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, adminId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new ClinicAdministrator(
              rs.getInt("adminId"),
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              rs.getString("userRole"),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              null);
        }
      }
    }
    return null;
  }

  public static boolean updateClinicAdministrator(ClinicAdministrator admin) throws SQLException {
    String sql =
        "UPDATE Users SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? "
            + "WHERE userId = (SELECT userId FROM ClinicAdministrator WHERE adminId = ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, admin.getFirstName());
      ps.setString(2, admin.getLastName());
      ps.setString(3, admin.getUserRole());
      ps.setString(4, admin.getIcPassportNo());
      ps.setString(5, admin.getEmail());
      ps.setString(6, admin.getPasswordHash());
      ps.setInt(7, admin.getAdminId());
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static boolean deleteClinicAdministrator(int adminId) throws SQLException {
    String selectSql = "SELECT userId FROM ClinicAdministrator WHERE adminId = ?";
    String deleteAdminSql = "DELETE FROM ClinicAdministrator WHERE adminId = ?";
    String deleteUserSql = "DELETE FROM Users WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId = -1;
        try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
          psSelect.setInt(1, adminId);
          try (ResultSet rs = psSelect.executeQuery()) {
            if (rs.next()) {
              userId = rs.getInt("userId");
            }
          }
        }

        if (userId == -1) {
          throw new SQLException("ClinicAdministrator not found with adminId: " + adminId);
        }

        try (PreparedStatement psAdmin = conn.prepareStatement(deleteAdminSql)) {
          psAdmin.setInt(1, adminId);
          psAdmin.executeUpdate();
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
    }
  }

  // TODO: Remove before submission
  public static List<ClinicAdministrator> listAllClinicAdministrators() throws SQLException {
    String sql =
        "SELECT a.adminId, a.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM ClinicAdministrator a "
            + "JOIN Users u ON a.userId = u.userId";

    List<ClinicAdministrator> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== CLINIC ADMINISTRATORS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        ClinicAdministrator admin =
            new ClinicAdministrator(
                rs.getInt("adminId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null);
        list.add(admin);
        System.out.println(
            admin.getAdminId() + " | " + admin.getFullName() + " | " + admin.getEmail());
      }
      if (empty) {
        System.out.println("(no administrators found)");
      }
      System.out.println("=========================================");
    }
    return list;
  }

  public static List<ClinicAdministrator> getAllClinicAdministrators() throws SQLException {
    String sql =
        "SELECT a.adminId, a.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM ClinicAdministrator a "
            + "JOIN Users u ON a.userId = u.userId";

    List<ClinicAdministrator> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new ClinicAdministrator(
                rs.getInt("adminId"),
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
