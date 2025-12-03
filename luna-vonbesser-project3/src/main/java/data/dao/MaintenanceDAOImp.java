package data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.DataSource;

public class MaintenanceDAOImp implements MaintenanceDAO {

    @Override
    public List<Map<String, Object>> getMaintenanceAlertsByMaintainer(int maintainerId) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        String sql = "SELECT ma.*, s.vehicle_number, s.make, s.model "
                + "FROM Maintenance_Alerts ma "
                + "JOIN E_Scooters s ON ma.scooter_id = s.id "
                + "WHERE (ma.assigned_to = ? OR ma.assigned_to IS NULL) "
                + "AND ma.status != 'RESOLVED' "
                + "ORDER BY ma.priority DESC, ma.created_date ASC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maintainerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("id", rs.getInt("id"));
                alert.put("scooterId", rs.getInt("scooter_id"));
                alert.put("vehicleNumber", rs.getString("vehicle_number"));
                alert.put("make", rs.getString("make"));
                alert.put("model", rs.getString("model"));
                alert.put("alertType", rs.getString("alert_type"));
                alert.put("description", rs.getString("description"));
                alert.put("priority", rs.getString("priority"));
                alert.put("status", rs.getString("status"));
                alert.put("createdDate", rs.getTimestamp("created_date"));
                alert.put("resolvedDate", rs.getTimestamp("resolved_date"));

                alerts.add(alert);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting maintenance alerts: " + e.getMessage());
            e.printStackTrace();
        }

        return alerts;
    }

    @Override
    public int countCompletedMaintenance(int maintainerId) {
        String sql = "SELECT COUNT(*) as count FROM Maintenance_Alerts "
                + "WHERE assigned_to = ? AND status = 'RESOLVED' "
                + "AND MONTH(created_date) = MONTH(CURRENT_DATE())";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maintainerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error counting completed maintenance: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int countHighPriorityCompleted(int maintainerId) {
        String sql = "SELECT COUNT(*) as count FROM Maintenance_Alerts "
                + "WHERE assigned_to = ? AND status = 'RESOLVED' "
                + "AND priority = 'HIGH' "
                + "AND MONTH(created_date) = MONTH(CURRENT_DATE())";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maintainerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error counting high priority maintenance: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean createMaintenanceAlert(int scooterId, String alertType, String description, String priority) {
        String sql = "INSERT INTO Maintenance_Alerts (scooter_id, alert_type, description, priority) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scooterId);
            stmt.setString(2, alertType);
            stmt.setString(3, description);
            stmt.setString(4, priority);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("error creating maintenance alert: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean assignMaintenanceTask(int alertId, int maintainerId) {
        String sql = "UPDATE Maintenance_Alerts SET assigned_to = ?, status = 'IN_PROGRESS' WHERE id = ?";
        try (java.sql.Connection conn = data.DataSource.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maintainerId);
            stmt.setInt(2, alertId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("error assigning maintenance task: " + e.getMessage());
        }
        return false;
    }

    // new method to mark maintenance as resolved
    public boolean resolveMaintenanceTask(int alertId, int maintainerId) {
        String sql = "UPDATE Maintenance_Alerts SET status = 'RESOLVED', resolved_date = NOW() "
                + "WHERE id = ? AND assigned_to = ?";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alertId);
            stmt.setInt(2, maintainerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("error resolving maintenance task: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
