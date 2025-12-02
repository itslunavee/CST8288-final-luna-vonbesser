package data.dao;

import model.Scooter;
import java.util.List;

// interface for scooter data access operations
public interface ScooterDAO {
    
    // add a new scooter to the database
    boolean addScooter(Scooter scooter);
    
    // get all scooters (for reports)
    List<Scooter> getAllScooters();
    
    // get scooters by sponsor
    List<Scooter> getScootersBySponsor(int sponsorId);
    
    // get a scooter by its vehicle number (unique identifier)
    Scooter getScooterByVehicleNumber(String vehicleNumber);
    
    // update an existing scooter
    boolean updateScooter(Scooter scooter);
    
    // delete a scooter
    boolean deleteScooter(String vehicleNumber);
}
