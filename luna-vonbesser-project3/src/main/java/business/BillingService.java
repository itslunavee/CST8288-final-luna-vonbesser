package business;

import model.User;
import data.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

// this is the business logic for handling billing and payments
// it calculates debits, processes payments, and manages account status
public class BillingService {

    // rates for calculating costs
    private static final double RATE_PER_KM = 0.25;      // $0.25 per kilometer
    private static final double RATE_PER_MINUTE = 0.10;  // $0.10 per minute

    // calculate the cost of a trip
    public double calculateTripCost(double distanceKm, int durationMinutes) {
        double distanceCost = distanceKm * RATE_PER_KM;
        double timeCost = durationMinutes * RATE_PER_MINUTE;
        double total = distanceCost + timeCost;

        // apply discount for longer trips
        if (total > 5.00) {
            total *= 0.9;  // 10% discount
        }

        // round to 2 decimal places
        return Math.round(total * 100.0) / 100.0;
    }

    // charge a user for a completed trip
    public boolean chargeForTrip(int userId, int tripId, double distanceKm, int durationMinutes) {
        double cost = calculateTripCost(distanceKm, durationMinutes);

        try (Connection conn = DataSource.getConnection()) {
            // start transaction
            conn.setAutoCommit(false);

            try {
                // 1. update user's balance
                String updateBalance = "UPDATE Users SET credits_balance = credits_balance - ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateBalance)) {
                    stmt.setDouble(1, cost);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }

                // 2. record the debit transaction
                String insertDebit = "INSERT INTO User_Credits (user_id, credit_type, amount, description) "
                        + "VALUES (?, 'USAGE', ?, ?)";
                String description = String.format("Trip #%d: %.1f km, %d min", tripId, distanceKm, durationMinutes);

                try (PreparedStatement stmt = conn.prepareStatement(insertDebit)) {
                    stmt.setInt(1, userId);
                    stmt.setDouble(2, -cost);  // negative amount for debit
                    stmt.setString(3, description);
                    stmt.executeUpdate();
                }

                // 3. update trip with cost
                String updateTrip = "UPDATE Trips SET cost = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateTrip)) {
                    stmt.setDouble(1, cost);
                    stmt.setInt(2, tripId);
                    stmt.executeUpdate();
                }

                // commit transaction
                conn.commit();
                System.out.println("charged user #" + userId + " $" + cost + " for trip #" + tripId);
                return true;

            } catch (SQLException e) {
                // rollback on error
                conn.rollback();
                System.err.println("error charging for trip: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("database error in chargeForTrip: " + e.getMessage());
            return false;
        }
    }

    // get user's billing summary
    public Map<String, Object> getBillingSummary(int userId) {
        Map<String, Object> summary = new HashMap<>();

        String sql = "SELECT "
                + "credits_balance as balance, "
                + "(SELECT COALESCE(SUM(ABS(amount)), 0) FROM User_Credits "
                + " WHERE user_id = ? AND credit_type = 'USAGE' AND is_paid = FALSE) as unpaid, "
                + "(SELECT COALESCE(SUM(amount), 0) FROM User_Credits "
                + " WHERE user_id = ? AND credit_type = 'USAGE' "
                + " AND MONTH(transaction_date) = MONTH(CURRENT_DATE())) as monthly_usage "
                + "FROM Users WHERE id = ?";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                summary.put("balance", rs.getDouble("balance"));
                summary.put("unpaid", rs.getDouble("unpaid"));
                summary.put("monthlyUsage", rs.getDouble("monthly_usage"));

                // calculate due date (10 days after month end)
                LocalDate today = LocalDate.now();
                LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
                LocalDate dueDate = endOfMonth.plusDays(10);
                summary.put("dueDate", dueDate.toString());

                // check if overdue
                boolean isOverdue = rs.getDouble("unpaid") > 0
                        && today.isAfter(dueDate);
                summary.put("isOverdue", isOverdue);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting billing summary: " + e.getMessage());
        }

        return summary;
    }

    // process a payment
    public boolean processPayment(int userId, double amount, String paymentMethod) {
        // get unpaid amount
        double unpaid = getUnpaidAmount(userId);

        if (unpaid <= 0) {
            System.out.println("no unpaid amount for user #" + userId);
            return false;
        }

        if (amount > unpaid) {
            amount = unpaid;  // can't pay more than owed
        }

        try (Connection conn = DataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. record payment
                String insertPayment = "INSERT INTO User_Credits (user_id, credit_type, amount, description, is_paid) "
                        + "VALUES (?, 'PAYMENT', ?, ?, TRUE)";

                try (PreparedStatement stmt = conn.prepareStatement(insertPayment)) {
                    stmt.setInt(1, userId);
                    stmt.setDouble(2, amount);
                    stmt.setString(3, "Payment via " + paymentMethod);
                    stmt.executeUpdate();
                }

                // 2. update user balance
                String updateBalance = "UPDATE Users SET credits_balance = credits_balance + ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateBalance)) {
                    stmt.setDouble(1, amount);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }

                // 3. mark oldest unpaid debits as paid
                markDebitsAsPaid(userId, amount, conn);

                // 4. update account status if all paid
                updateAccountStatus(userId, conn);

                conn.commit();
                System.out.println("processed payment of $" + amount + " for user #" + userId);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("error processing payment: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("database error in processPayment: " + e.getMessage());
            return false;
        }
    }

    // helper method to get unpaid amount
    private double getUnpaidAmount(int userId) {
        String sql = "SELECT COALESCE(SUM(ABS(amount)), 0) as unpaid "
                + "FROM User_Credits "
                + "WHERE user_id = ? AND credit_type = 'USAGE' AND is_paid = FALSE";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("unpaid");
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting unpaid amount: " + e.getMessage());
        }

        return 0.0;
    }

    // mark debits as paid (oldest first)
    private void markDebitsAsPaid(int userId, double paymentAmount, Connection conn) throws SQLException {
        String sql = "SELECT id, amount FROM User_Credits "
                + "WHERE user_id = ? AND credit_type = 'USAGE' AND is_paid = FALSE "
                + "ORDER BY transaction_date ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            double remaining = paymentAmount;

            while (rs.next() && remaining > 0) {
                int creditId = rs.getInt("id");
                double debitAmount = Math.abs(rs.getDouble("amount"));

                if (debitAmount <= remaining) {
                    // mark entire debit as paid
                    markSingleDebitPaid(creditId, conn);
                    remaining -= debitAmount;
                } else {
                    // partial payment - mark as paid and create adjustment
                    markSingleDebitPaid(creditId, conn);
                    // create a new credit for the overpayment?
                    remaining = 0;
                }
            }

            rs.close();
        }
    }

    // mark a single debit as paid
    private void markSingleDebitPaid(int creditId, Connection conn) throws SQLException {
        String sql = "UPDATE User_Credits SET is_paid = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creditId);
            stmt.executeUpdate();
        }
    }

    // update user's account status
    private void updateAccountStatus(int userId, Connection conn) throws SQLException {
        double unpaid = getUnpaidAmount(userId);
        String status = unpaid > 0 ? "OVERDUE" : "ACTIVE";

        String sql = "UPDATE Users SET account_status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // get user's transaction history
    public ResultSet getTransactionHistory(int userId) throws SQLException {
        String sql = "SELECT * FROM User_Credits WHERE user_id = ? "
                + "ORDER BY transaction_date DESC";

        Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);

        return stmt.executeQuery();
        // note: caller must close the result set and connection!
    }

    // calculate estimated cost for a planned trip
    public double estimateTripCost(double distanceKm, int estimatedMinutes) {
        return calculateTripCost(distanceKm, estimatedMinutes);
    }
}
