package business.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.dao.ChargingStationDAO;
import data.dao.ChargingStationDAOImp;
import data.dao.ScooterDAO;
import data.dao.ScooterDAOImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ChargingStation;
import model.Scooter;

public class ViewChargingStationsCommand implements CommandInterface {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        ChargingStationDAO stationDao = new ChargingStationDAOImp();
        ScooterDAO scooterDao = new ScooterDAOImp();
        List<ChargingStation> stations = stationDao.getAllChargingStations();
        Map<Integer, List<Scooter>> scootersByStation = new HashMap<>();
        Map<Integer, Integer> scooterCount = new HashMap<>();
        for (ChargingStation station : stations) {
            List<Scooter> scooters = scooterDao.getAllScooters();
            List<Scooter> atStation = new java.util.ArrayList<>();
            for (Scooter scooter : scooters) {
                if (station.getId() == scooter.getStationId()) {
                    atStation.add(scooter);
                }
            }
            scootersByStation.put(station.getId(), atStation);
            scooterCount.put(station.getId(), atStation.size());
        }
        request.setAttribute("stations", stations);
        request.setAttribute("scootersByStation", scootersByStation);
        request.setAttribute("scooterCount", scooterCount);
        return "viewChargingStations.jsp";
    }
    @Override
    public String getCommandName() { return "viewChargingStations"; }
}
