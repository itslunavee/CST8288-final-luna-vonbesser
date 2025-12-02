package data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import data.DataSource;
import model.Scooter;

// actual implementation that talks to the database for scooters
public class ScooterDAOImp implements ScooterDAO {

    @Override
    public boolean addScooter(Scooter scooter) {
        // this matches the database columns from your schema
        String sql = "INSERT INTO E_Scooters (vehicle_number, make, model, color, "
                   + "battery_capacity, current_charge_level, status, sponsor_id, "
                   + "current_station_id, total_usage_hours, tire_wear_hours, brake_wear_hours) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // set the values from the scooter object
            stmt.setString(1, scooter.getVehicleNumber());
            stmt.setString(2, scooter.getMake());
            stmt.setString(3, scooter.getModel());
            
            // color might be null
            if (scooter.getColor() != null) {
                stmt.setString(4, scooter.getColor());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            
            stmt.setInt(5, scooter.getBatteryCapacity());
            stmt.setInt(6, scooter.getCurrentCharge());
            stmt.setString(7, scooter.getStatus());
            stmt.setInt(8, scooter.getSponsorId());
            
            // station id might be null
            if (scooter.getStationId() != null) {
                stmt.setInt(9, scooter.getStationId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            
            stmt.setDouble(10, scooter.getTotalUsageHours());
            stmt.setDouble(11, scooter.getTireWearHours());
            stmt.setDouble(12, scooter.getBrakeWearHours());

            int rowsAffected = stmt.executeUpdate();
            
            // get the auto-generated id and set it on the scooter object
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        scooter.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("error adding scooter: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public List<Scooter> getAllScooters() {
        String sql = "SELECT * FROM E_Scooters";
        List<Scooter> scooters = new ArrayList<>();

        try (Connection conn = DataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Scooter scooter = createScooterFromResultSet(rs);
                scooters.add(scooter);
            }

        } catch (SQLException e) {
            System.err.println("error getting all scooters: " + e.getMessage());
            e.printStackTrace();
        }

        return scooters;
    }

    @Override
    public List<Scooter> getScootersBySponsor(int sponsorId) {
        String sql = "SELECT * FROM E_Scooters WHERE sponsor_id = ?";
        List<Scooter> scooters = new ArrayList<>();

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sponsorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Scooter scooter = createScooterFromResultSet(rs);
                scooters.add(scooter);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("error getting scooters by sponsor: " + e.getMessage());
            e.printStackTrace();
        }

        return scooters;
    }
    
    // helper method to create a scooter from a database result set
    // this makes the code cleaner and avoids repetition
    private Scooter createScooterFromResultSet(ResultSet rs) throws SQLException {
        // first create a builder with the required fields
        Scooter.ScooterBuilder builder = new Scooter.ScooterBuilder(
            rs.getString("vehicle_number"),
            rs.getString("make"),
            rs.getString("model"),
            rs.getInt("sponsor_id")
        );
        
        // now set all the optional fields using the builder methods
        builder.color(rs.getString("color"))
               .batteryCapacity(rs.getInt("battery_capacity"))
               .currentCharge(rs.getInt("current_charge_level"))
               .status(rs.getString("status"))
               .totalUsageHours(rs.getDouble("total_usage_hours"))
               .tireWearHours(rs.getDouble("tire_wear_hours"))
               .brakeWearHours(rs.getDouble("brake_wear_hours"));
        
        // handle nullable station id
        int stationId = rs.getInt("current_station_id");
        if (!rs.wasNull()) {
            builder.stationId(stationId);
        } else {
            builder.stationId(null);
        }
        
        // last known location isn't in our database schema, so skip it
        // if we had it, we'd do: builder.lastKnownLocation(rs.getString("last_known_location"))
        
        // build the scooter
        Scooter scooter = builder.build();
        // set the id separately (not through builder since it comes from db)
        scooter.setId(rs.getInt("id"));

        // Register MaintenanceObserver for maintenance alerts
        scooter.addObserver(new data.observers.MaintenanceObserver());

        return scooter;
    }
    
    // added method to get a single scooter by vehicle number
    @Override
    public Scooter getScooterByVehicleNumber(String vehicleNumber) {
        String sql = "SELECT * FROM E_Scooters WHERE vehicle_number = ?";
        
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return createScooterFromResultSet(rs);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("error getting scooter by vehicle number: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // added method to update a scooter
    @Override
    public boolean updateScooter(Scooter scooter) {
        String sql = "UPDATE E_Scooters SET make = ?, model = ?, color = ?, battery_capacity = ?, "
                   + "current_charge_level = ?, status = ?, sponsor_id = ?, current_station_id = ?, "
                   + "total_usage_hours = ?, tire_wear_hours = ?, brake_wear_hours = ? "
                   + "WHERE vehicle_number = ?";
        
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, scooter.getMake());
            stmt.setString(2, scooter.getModel());
            
            if (scooter.getColor() != null) {
                stmt.setString(3, scooter.getColor());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            
            stmt.setInt(4, scooter.getBatteryCapacity());
            stmt.setInt(5, scooter.getCurrentCharge());
            stmt.setString(6, scooter.getStatus());
            stmt.setInt(7, scooter.getSponsorId());
            
            if (scooter.getStationId() != null) {
                stmt.setInt(8, scooter.getStationId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setDouble(9, scooter.getTotalUsageHours());
            stmt.setDouble(10, scooter.getTireWearHours());
            stmt.setDouble(11, scooter.getBrakeWearHours());
            stmt.setString(12, scooter.getVehicleNumber());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("error updating scooter: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // added method to delete a scooter
    @Override
    public boolean deleteScooter(String vehicleNumber) {
        String sql = "DELETE FROM E_Scooters WHERE vehicle_number = ?";
        
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleNumber);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("error deleting scooter: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
