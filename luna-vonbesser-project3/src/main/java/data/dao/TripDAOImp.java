package data.dao;

import data.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripDAOImp implements TripDAO {

    @Override
    public List<Map<String, Object>> getTripsByUser(int userId) {
        List<Map<String, Object>> trips = new ArrayList<>();
        String sql = "SELECT t.*, s.vehicle_number, s.make, s.model, "
                + "cs1.location_name as start_station_name, cs2.location_name as end_station_name "
                + "FROM Trips t "
                + "JOIN E_Scooters s ON t.scooter_id = s.id "
                + "LEFT JOIN Charging_Stations cs1 ON t.start_station_id = cs1.id "
                + "LEFT JOIN Charging_Stations cs2 ON t.end_station_id = cs2.id "
                + "WHERE t.user_id = ? "
                + "ORDER BY t.start_time DESC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> trip = new HashMap<>();
                trip.put("id", rs.getInt("id"));
                trip.put("vehicleNumber", rs.getString("vehicle_number"));
                trip.put("make", rs.getString("make"));
                trip.put("model", rs.getString("model"));
                trip.put("startTime", rs.getTimestamp("start_time"));
                trip.put("endTime", rs.getTimestamp("end_time"));
                trip.put("startStation", rs.getString("start_station_name"));
                trip.put("endStation", rs.getString("end_station_name"));
                trip.put("distance", rs.getDouble("distance_traveled"));
                trip.put("duration", rs.getInt("duration_minutes"));
                trip.put("cost", rs.getDouble("cost"));

                trips.add(trip);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting trips by user: " + e.getMessage());
            e.printStackTrace();
        }

        return trips;
    }

    @Override
    public List<Map<String, Object>> getTripsByScooter(int scooterId) {
        List<Map<String, Object>> trips = new ArrayList<>();
        String sql = "SELECT t.*, u.name as user_name, "
                + "cs1.location_name as start_station, cs2.location_name as end_station "
                + "FROM Trips t "
                + "JOIN Users u ON t.user_id = u.id "
                + "LEFT JOIN Charging_Stations cs1 ON t.start_station_id = cs1.id "
                + "LEFT JOIN Charging_Stations cs2 ON t.end_station_id = cs2.id "
                + "WHERE t.scooter_id = ? "
                + "ORDER BY t.start_time DESC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scooterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> trip = new HashMap<>();
                trip.put("id", rs.getInt("id"));
                trip.put("userName", rs.getString("user_name"));
                trip.put("startTime", rs.getTimestamp("start_time"));
                trip.put("endTime", rs.getTimestamp("end_time"));
                trip.put("startStation", rs.getString("start_station"));
                trip.put("endStation", rs.getString("end_station"));
                trip.put("distance", rs.getDouble("distance_traveled"));
                trip.put("duration", rs.getInt("duration_minutes"));
                trip.put("cost", rs.getDouble("cost"));

                trips.add(trip);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting trips by scooter: " + e.getMessage());
            e.printStackTrace();
        }

        return trips;
    }

    @Override
    public List<Map<String, Object>> getTripsBySponsor(int sponsorId) {
        List<Map<String, Object>> trips = new ArrayList<>();
        String sql = "SELECT t.*, s.vehicle_number, u.name as user_name, "
                + "cs1.location_name as start_station, cs2.location_name as end_station "
                + "FROM Trips t "
                + "JOIN E_Scooters s ON t.scooter_id = s.id "
                + "JOIN Users u ON t.user_id = u.id "
                + "LEFT JOIN Charging_Stations cs1 ON t.start_station_id = cs1.id "
                + "LEFT JOIN Charging_Stations cs2 ON t.end_station_id = cs2.id "
                + "WHERE s.sponsor_id = ? "
                + "AND MONTH(t.start_time) = MONTH(CURRENT_DATE()) "
                + "ORDER BY t.start_time DESC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sponsorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> trip = new HashMap<>();
                trip.put("id", rs.getInt("id"));
                trip.put("vehicleNumber", rs.getString("vehicle_number"));
                trip.put("userName", rs.getString("user_name"));
                trip.put("startTime", rs.getTimestamp("start_time"));
                trip.put("endTime", rs.getTimestamp("end_time"));
                trip.put("startStation", rs.getString("start_station"));
                trip.put("endStation", rs.getString("end_station"));
                trip.put("distance", rs.getDouble("distance_traveled"));
                trip.put("cost", rs.getDouble("cost"));

                trips.add(trip);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting trips by sponsor: " + e.getMessage());
            e.printStackTrace();
        }

        return trips;
    }

    @Override
    public boolean createTrip(int userId, int scooterId, int startStationId) {
        String sql = "INSERT INTO Trips (user_id, scooter_id, start_station_id) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, scooterId);
            stmt.setInt(3, startStationId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("error creating trip: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean completeTrip(int tripId, int endStationId, double distance, double cost) {
        String sql = "UPDATE Trips SET end_station_id = ?, end_time = NOW(), "
                + "distance_traveled = ?, cost = ?, "
                + "duration_minutes = TIMESTAMPDIFF(MINUTE, start_time, NOW()) "
                + "WHERE id = ?";

        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, endStationId);
            stmt.setDouble(2, distance);
            stmt.setDouble(3, cost);
            stmt.setInt(4, tripId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("error completing trip: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
