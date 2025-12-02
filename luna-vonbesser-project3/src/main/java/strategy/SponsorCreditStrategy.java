package strategy;

import model.User;
import data.dao.ScooterDAO;
import data.dao.ScooterDAOImp;
import data.dao.TripDAO;
import data.dao.TripDAOImp;
import java.sql.*;
import java.util.List;

// sponsors earn credits based on their scooters' availability and usage
// this actually queries the database for real data
public class SponsorCreditStrategy implements CreditStrategyInterface {

    private ScooterDAO scooterDao;
    private TripDAO tripDao;

    public SponsorCreditStrategy() {
        this.scooterDao = new ScooterDAOImp();
        this.tripDao = new TripDAOImp();
    }

    @Override
    public double calculateCredit(User user) {
        if (!user.isSponsor()) {
            return 0.0;
        }

        double totalCredits = 0.0;
        int sponsorId = user.getId();

        try {
            // 1. base credit per scooter (for making it available)
            List<model.Scooter> scooters = scooterDao.getScootersBySponsor(sponsorId);
            totalCredits += scooters.size() * 20.0;  // 20 credits per scooter

            // 2. usage-based credits (more trips = more credits)
            double usageCredits = calculateUsageCredits(sponsorId);
            totalCredits += usageCredits;

            // 3. availability bonus (scooters that are mostly available)
            double availabilityBonus = calculateAvailabilityBonus(sponsorId);
            totalCredits += availabilityBonus;

            System.out.println("debug: sponsor " + user.getName()
                    + " earned $" + totalCredits
                    + " (scooters: " + scooters.size()
                    + ", usage: $" + usageCredits + ")");

        } catch (Exception e) {
            System.err.println("error calculating sponsor credits: " + e.getMessage());
            // fallback to simple calculation
            List<model.Scooter> scooters = scooterDao.getScootersBySponsor(sponsorId);
            return scooters.size() * 25.0;
        }

        return totalCredits;
    }

    // calculate credits based on how much the sponsor's scooters are used
    private double calculateUsageCredits(int sponsorId) throws SQLException {
        String sql = "SELECT SUM(t.cost * 0.3) as usage_credits "
                + "FROM Trips t "
                + "JOIN EScooters s ON t.scooter_id = s.id "
                + "WHERE s.sponsor_id = ? "
                + "AND MONTH(t.start_time) = MONTH(CURRENT_DATE())";

        try (Connection conn = data.DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sponsorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("usage_credits");
            }
        }

        return 0.0;
    }

    // bonus for scooters that are mostly available (not in maintenance)
    private double calculateAvailabilityBonus(int sponsorId) throws SQLException {
        String sql = "SELECT COUNT(*) as total, "
                + "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) as available "
                + "FROM EScooters WHERE sponsor_id = ?";

        try (Connection conn = data.DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sponsorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                int available = rs.getInt("available");

                if (total > 0) {
                    double availabilityRate = (double) available / total;
                    if (availabilityRate > 0.8) {
                        return 50.0;  // bonus for high availability
                    } else if (availabilityRate > 0.5) {
                        return 25.0;  // smaller bonus for decent availability
                    }
                }
            }
        }

        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Sponsor Credit Strategy (Database-driven)";
    }
}
