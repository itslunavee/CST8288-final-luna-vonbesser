package data.dao;

import java.util.List;

import model.ChargingStation;

// DAO interface for charging stations
public interface ChargingStationDAO {
    boolean addChargingStation(ChargingStation station);
    ChargingStation getChargingStationById(int id);
    List<ChargingStation> getAllChargingStations();
    boolean updateChargingStation(ChargingStation station);
    boolean deleteChargingStation(int id);
    int countScootersAtStation(int stationId);
}
