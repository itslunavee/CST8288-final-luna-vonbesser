package data.dao;

import java.util.List;
import java.util.Map;

public interface TripDAO {

    // get trips for a specific user
    List<Map<String, Object>> getTripsByUser(int userId);

    // get trips for a specific scooter
    List<Map<String, Object>> getTripsByScooter(int scooterId);

    // get trips for a sponsor's scooters (to calculate sponsor credits)
    List<Map<String, Object>> getTripsBySponsor(int sponsorId);

    // create a new trip
    boolean createTrip(int userId, int scooterId, int startStationId);

    // complete a trip
    boolean completeTrip(int tripId, int endStationId, double distance, double cost);
}
