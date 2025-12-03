package data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import data.DataSource;
import model.ChargingStation;

// actual implementation that talks to the database for charging stations
public class ChargingStationDAOImp implements ChargingStationDAO {

    @Override
    public boolean addChargingStation(ChargingStation station) {
        String sql = "INSERT INTO charging_stations (location_name, latitude, longitude, max_capacity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, station.getLocationName());
            stmt.setDouble(2, station.getLatitude());
            stmt.setDouble(3, station.getLongitude());
            stmt.setInt(4, station.getMaxCapacity());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        station.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("error adding charging station: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ChargingStation getChargingStationById(int id) {
        String sql = "SELECT * FROM charging_stations WHERE id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createChargingStationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("error getting charging station by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ChargingStation> getAllChargingStations() {
        String sql = "SELECT * FROM charging_stations";
        List<ChargingStation> stations = new ArrayList<>();
        try (Connection conn = DataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stations.add(createChargingStationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("error getting all charging stations: " + e.getMessage());
        }
        return stations;
    }

    @Override
    public boolean updateChargingStation(ChargingStation station) {
        String sql = "UPDATE charging_stations SET location_name = ?, latitude = ?, longitude = ?, max_capacity = ? WHERE id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, station.getLocationName());
            stmt.setDouble(2, station.getLatitude());
            stmt.setDouble(3, station.getLongitude());
            stmt.setInt(4, station.getMaxCapacity());
            stmt.setInt(5, station.getId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("error updating charging station: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteChargingStation(int id) {
        String sql = "DELETE FROM charging_stations WHERE id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("error deleting charging station: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int countScootersAtStation(int stationId) {
        String sql = "SELECT COUNT(*) FROM e_scooters WHERE current_station_id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("error counting scooters at station: " + e.getMessage());
        }
        return 0;
    }

    // helper method to create ChargingStation from ResultSet
    private ChargingStation createChargingStationFromResultSet(ResultSet rs) throws SQLException {
        return new ChargingStation(
            rs.getInt("id"),
            rs.getString("location_name"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            rs.getInt("max_capacity")
        );
    }
}
