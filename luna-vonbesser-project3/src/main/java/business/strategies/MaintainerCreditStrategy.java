package business.strategies;

import model.User;
import data.dao.ScooterDAO;
import data.dao.ScooterDAOImp;
import data.dao.MaintenanceDAO;
import data.dao.MaintenanceDAOImp;
import java.sql.*;

// maintainers earn credits for returning scooters to stations and doing maintenance
// this strategy actually queries the database to calculate real credits
public class MaintainerCreditStrategy implements CreditStrategyInterface {
    private ScooterDAO scooterDao;
    private MaintenanceDAO maintenanceDao;
    
    public MaintainerCreditStrategy() {
        // initialize our DAOs to talk to the database
        this.scooterDao = new ScooterDAOImp();
        this.maintenanceDao = new MaintenanceDAOImp();
    }

    @Override
    public double calculateCredit(User user) {
        if (!user.isMaintainer()) return 0.0;
        
        double totalCredits = 0.0;
        int maintainerId = user.getId();
        
        try {
            // 1. credits for scooters returned to charging stations
            int scootersReturned = countScootersReturned(maintainerId);
            totalCredits += scootersReturned * 15.0;  // 15 credits per scooter returned
            
            // 2. credits for completed maintenance tasks
            int completedMaintenance = countCompletedMaintenance(maintainerId);
            totalCredits += completedMaintenance * 25.0;  // 25 credits per completed task
            
            // 3. bonus for urgent/priority maintenance
            int highPriorityCompleted = countHighPriorityMaintenance(maintainerId);
            totalCredits += highPriorityCompleted * 10.0;  // bonus 10 credits for urgent tasks
            
            System.out.println("debug: maintainer " + user.getName() + 
                             " earned $" + totalCredits + 
                             " (returned: " + scootersReturned + 
                             ", maintenance: " + completedMaintenance + ")");
            
        } catch (SQLException e) {
            System.err.println("error calculating maintainer credits: " + e.getMessage());
            // return a default value if we can't calculate
            return 100.0;  // fallback minimum
        }
        
        return totalCredits;
    }
    
    // count how many scooters this maintainer has returned to charging stations
    private int countScootersReturned(int maintainerId) throws SQLException {
        // we need to query the trips or activity table
        // for now, let's assume there's an Activity table that tracks this
        String sql = "SELECT COUNT(*) as count FROM UserActivity " +
                    "WHERE user_id = ? AND activity_type = 'RETURN_SCOOTER' " +
                    "AND MONTH(activity_date) = MONTH(CURRENT_DATE())";
        
        try (Connection conn = data.DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maintainerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;  // fallback if no activity table exists yet
    }
    
    // count completed maintenance tasks for this maintainer
    private int countCompletedMaintenance(int maintainerId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM MaintenanceAlerts " +
                    "WHERE assigned_to = ? AND status = 'RESOLVED' " +
                    "AND MONTH(resolved_at) = MONTH(CURRENT_DATE())";
        
        try (Connection conn = data.DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maintainerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }
    
    // count high priority completed maintenance
    private int countHighPriorityMaintenance(int maintainerId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM MaintenanceAlerts " +
                    "WHERE assigned_to = ? AND status = 'RESOLVED' " +
                    "AND priority IN ('HIGH', 'CRITICAL') " +
                    "AND MONTH(resolved_at) = MONTH(CURRENT_DATE())";
        
        try (Connection conn = data.DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maintainerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }

    @Override
    public String getStrategyName() {
        return "Maintainer Credit Strategy (Database-driven)";
    }
}
